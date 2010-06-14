/* Redis stat utility.
 *
 * Copyright (c) 2009-2010, Salvatore Sanfilippo <antirez at gmail dot com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *   * Neither the name of Redis nor the names of its contributors may be used
 *     to endorse or promote products derived from this software without
 *     specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

#include "fmacros.h"

#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <stdarg.h>
#include <limits.h>
#include <math.h>
#include <sys/time.h>

#include "sds.h"
#include "zmalloc.h"
#include "hiredis.h"
#include "anet.h"

#define REDIS_NOTUSED(V) ((void) V)

#define STAT_VMSTAT 0
#define STAT_VMPAGE 1
#define STAT_OVERVIEW 2
#define STAT_ONDISK_SIZE 3
#define STAT_LATENCY 4

static struct config {
    char *hostip;
    int hostport;
    int delay;
    int stat; /* The kind of output to produce: STAT_* */
    int samplesize;
    int logscale;
} config;

static long long microseconds(void) {
    struct timeval tv;
    long long mst;

    gettimeofday(&tv, NULL);
    mst = ((long long)tv.tv_sec)*1000000;
    mst += tv.tv_usec;
    return mst;
}

/* Convert an amount of bytes into a human readable string in the form
 * of 100B, 2G, 100M, 4K, and so forth. */
void bytesToHuman(char *s, long long n) {
    double d;

    if (n < 0) {
        *s = '-';
        s++;
        n = -n;
    }
    if (n < 1024) {
        /* Bytes */
        sprintf(s,"%lluB",n);
        return;
    } else if (n < (1024*1024)) {
        d = (double)n/(1024);
        sprintf(s,"%.2fK",d);
    } else if (n < (1024LL*1024*1024)) {
        d = (double)n/(1024*1024);
        sprintf(s,"%.2fM",d);
    } else if (n < (1024LL*1024*1024*1024)) {
        d = (double)n/(1024LL*1024*1024);
        sprintf(s,"%.2fG",d);
    }
}

void usage(char *wrong) {
    if (wrong)
        printf("Wrong option '%s' or option argument missing\n\n",wrong);
    printf(
"Usage: redis-stat <type> ... options ...\n\n"
"Statistic types:\n"
" overview (default)   Print general information about a Redis instance.\n"
" latency              Measure Redis server latency.\n"
"\n"
"Options:\n"
" host <hostname>      Server hostname (default 127.0.0.1)\n"
" port <hostname>      Server port (default 6379)\n"
" delay <milliseconds> Delay between requests (default: 1000 ms, 1 second).\n"
);
    exit(1);
}

static int parseOptions(int argc, char **argv) {
    int i;

    for (i = 1; i < argc; i++) {
        int lastarg = i==argc-1;
        
        if (!strcmp(argv[i],"host") && !lastarg) {
            char *ip = zmalloc(32);
            if (anetResolve(NULL,argv[i+1],ip) == ANET_ERR) {
                printf("Can't resolve %s\n", argv[i]);
                exit(1);
            }
            config.hostip = ip;
            i++;
        } else if (!strcmp(argv[i],"port") && !lastarg) {
            config.hostport = atoi(argv[i+1]);
            i++;
        } else if (!strcmp(argv[i],"delay") && !lastarg) {
            config.delay = atoi(argv[i+1]);
            i++;
        } else if (!strcmp(argv[i],"samplesize") && !lastarg) {
            config.samplesize = atoi(argv[i+1]);
            i++;
        } else if (!strcmp(argv[i],"overview")) {
            config.stat = STAT_OVERVIEW;
        } else if (!strcmp(argv[i],"latency")) {
            config.stat = STAT_LATENCY;
        } else if (!strcmp(argv[i],"help")) {
            usage(NULL);
        } else {
            usage(argv[i]);
        }
    }
    return i;
}

/* Return the specified INFO field from the INFO command output "info".
 * The result must be released calling sdsfree().
 *
 * If the field is not found NULL is returned. */
static sds getInfoField(char *info, char *field) {
    char *p = strstr(info,field);
    char *n1, *n2;

    if (!p) return NULL;
    p += strlen(field)+1;
    n1 = strchr(p,'\r');
    n2 = strchr(p,',');
    if (n2 && n2 < n1) n1 = n2;
    return sdsnewlen(p,(n1-p));
}

/* Like the above function but automatically convert the result into
 * a long. On error (missing field) LONG_MIN is returned. */
static long getLongInfoField(char *info, char *field) {
    sds val = getInfoField(info,field);
    long l;

    if (!val) return LONG_MIN;
    l = strtol(val,NULL,10);
    sdsfree(val);
    return l;
}

static void overview(int fd) {
    redisReply *r;
    int c = 0;
    long aux, requests = 0;

    while(1) {
        char buf[64];
        int j;

        r = redisCommand(fd,"INFO");
        if (r->type == REDIS_REPLY_ERROR) {
            printf("ERROR: %s\n", r->reply);
            exit(1);
        }

        if ((c % 20) == 0) {
            printf(
" ------- data ------ ------------ load ----------------------------- - childs -\n");
            printf(
" keys      used-mem  clients   requests            connections\n");
        }

        /* Keys */
        aux = 0;
        for (j = 0; j < 20; j++) {
            long k;

            sprintf(buf,"db%d:keys",j);
            k = getLongInfoField(r->reply,buf);
            if (k == LONG_MIN) continue;
            aux += k;
        }
        sprintf(buf,"%ld",aux);
        printf(" %-10s",buf);

        /* Used memory */
        aux = getLongInfoField(r->reply,"used_memory");
        bytesToHuman(buf,aux);
        printf("%-9s",buf);

        /* Clients */
        aux = getLongInfoField(r->reply,"connected_clients");
        sprintf(buf,"%ld",aux);
        printf(" %-10s",buf);

        /* Requets */
        aux = getLongInfoField(r->reply,"total_commands_processed");
        sprintf(buf,"%ld (+%ld)",aux,aux-requests);
        printf("%-19s",buf);
        requests = aux;

        /* Connections */
        aux = getLongInfoField(r->reply,"total_connections_received");
        sprintf(buf,"%ld",aux);
        printf(" %-18s",buf);

        /* Childs */
        aux = getLongInfoField(r->reply,"bgsave_in_progress");
        aux |= getLongInfoField(r->reply,"bgrewriteaof_in_progress") << 1;
        switch(aux) {
        case 0: break;
        case 1:
            printf("BGSAVE");
            break;
        case 2:
            printf("AOFREWRITE");
            break;
        case 3:
            printf("BGSAVE+AOF");
            break;
        }

        printf("\n");
        freeReplyObject(r);
        usleep(config.delay*1000);
        c++;
    }
}

static void latency(int fd) {
    redisReply *r;
    long long start;
    int seq = 1;

    while(1) {
        start = microseconds();
        r = redisCommand(fd,"PING");
        freeReplyObject(r);
        printf("%d: %.2f ms\n",seq,(double)(microseconds()-start)/1000);
        usleep(config.delay*1000);
        seq++;
    }
}

int main(int argc, char **argv) {
    redisReply *r;
    int fd;

    config.hostip = "127.0.0.1";
    config.hostport = 6379;
    config.stat = STAT_OVERVIEW;
    config.delay = 1000;
    config.samplesize = 10000;
    config.logscale = 0;

    parseOptions(argc,argv);

    r = redisConnect(&fd,config.hostip,config.hostport);
    if (r != NULL) {
        printf("Error connecting to Redis server: %s\n", r->reply);
        freeReplyObject(r);
        exit(1);
    }

    switch(config.stat) {
    case STAT_OVERVIEW:
        overview(fd);
        break;
    case STAT_LATENCY:
        latency(fd);
        break;
    }
    return 0;
}
