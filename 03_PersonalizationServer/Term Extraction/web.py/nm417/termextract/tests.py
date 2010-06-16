##############################################################################
#
# Copyright (c) 2010 Nicolaas Matthijs (nm417)
# All Rights Reserved.
#
##############################################################################
"""Test Setup

$Id: tests.py 100552 2009-05-30 15:16:11Z srichter $
"""
__docformat__ = "reStructuredText"
import unittest
from zope.testing import doctest
from zope.testing.doctestunit import DocFileSuite

def printTaggedTerms(terms):
    for term, tag, norm in terms:
        print (
            term + ' '*(16-len(term)) +
            tag + ' '*(6-len(tag)) +
            norm )

def test_suite():
    return unittest.TestSuite((
        DocFileSuite(
            'README.txt',
            optionflags=doctest.NORMALIZE_WHITESPACE|doctest.ELLIPSIS,
            ),
        DocFileSuite(
            'example.txt',
            globs={'printTaggedTerms': printTaggedTerms},
            optionflags=doctest.NORMALIZE_WHITESPACE|doctest.ELLIPSIS,
            ),
        ))
