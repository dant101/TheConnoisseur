package com.theconnoisseur;

import android.test.AndroidTestCase;

import Util.CommentUtil;

/**
 * Tests the comments ordering functions
 */
public class CommentComparisonTest extends AndroidTestCase {

    public void testComparesParentPathCorrectly() {
        CommentUtil c = new CommentUtil();

        assertEquals(-1, c.compare("2.4", "2.5", 0, 0));
        assertEquals(-1, c.compare("-1.11", "-1.11.12", 0, 0));
        assertEquals(-1, c.compare("-1.11", "-1.11", 5, 10));
        assertEquals(0, c.compare("-1.11.12.13", "-1.11.12.13", 0, 0));
        assertEquals(1, c.compare("2", "1", 0, 0));
        assertEquals(1, c.compare("1.15.16", "1.9.25", 0, 0));
    }
}
