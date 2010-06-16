##############################################################################
#
# Copyright (c) 2010 Nicolaas Matthijs (nm417)
# All Rights Reserved.
#
##############################################################################
"""Interfaces

$Id: interfaces.py 100556 2009-05-30 15:42:14Z srichter $
"""
__docformat__ = "reStructuredText"
import zope.interface

class ITagger(zope.interface.Interface):
    """A utility to provide POS tag extractions from a given text."""

    def initialize():
        """Initializes the tagger.

        This method only needs to be called once. It should do any expensive
        initial computation, such as creating indices, loading the lexicon,
        etc.
        """

    def tokenize(text):
        """Tokenize the given text into single words."""

    def tag(terms):
        """Returns the tagged list of terms.

        Additionally, all terms are normalized.

        The ouput format is a list of: (term, tag, normalized-term)
        """

    def __call__(text):
        """Get a tagged list of words."""


class ITermExtractor(zope.interface.Interface):
    """Extract important terms from a given text."""

    def __call__(text):
        """Returns a list of extracted terms, the amount of occurences and
        their search strength."""
