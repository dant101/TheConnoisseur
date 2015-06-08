package com.theconnoisseur;

import android.test.AndroidTestCase;
import com.theconnoisseur.android.Model.Comment;
import java.util.ArrayList;
import java.util.HashMap;
import Database.CommentOnlineDBFormat;
import Util.CommentUtil;

/**
 * Tests the comments ordering functions
 */
public class CommentComparisonTest extends AndroidTestCase {


    /**
     * This test checks for correct ordering of comments in the following system:
     *
     *          (comment_id)    (parent_path) + (comment_id)    (score)
     *            |     |               |            |          |   |
     *
     *              27                     1.27                  10
     *              21                     1.21                   8
     *              25                     1.21.25                8
     *              22                     1.21.22                1
     *              24                     1.21.22.24             4
     *              23                     1.23                   5
     *              26                     1.26                  -4
     *
     *
     * The parent_path describes the hierarchical position of a comment.
     * Take for example: '1.21.22'
     * The '1' element is a starting place holder with no added meaning
     * The '21' element means that the comment exists within replies to the comment with id 21
     * The '22' element means that the comment is a direct reply to comment with id 22
     *
     * The score is a measure of the comment popularity - currently implemented
     * with a vote-up/vote-down system
     *
     */
    public void testComparesParentPathCorrectly() {
        CommentUtil c = new CommentUtil(new ArrayList<CommentOnlineDBFormat>());

        HashMap<Integer, Integer> scores = new HashMap<>();
        scores.put(1, 0);
        scores.put(27, 10);
        scores.put(21, 8);
        scores.put(25, 8);
        scores.put(22, 1);
        scores.put(24, 4);
        scores.put(23, 5);
        scores.put(26, -4);

        c.setScores(scores);

        assertEquals(1, c.compare("1.27", "1.21"));
        assertEquals(1, c.compare("1.21", "1.21.25"));
        assertEquals(1, c.compare("1.21.25", "1.21.22"));
        assertEquals(0, c.compare("1.21.22.24", "1.21.22.24"));
        assertEquals(-1, c.compare("1.23", "1.21.22.24"));
        assertEquals(-1, c.compare("1.26", "1.23"));
    }

    public void testCalculatesCorrectNestingFromParentPath() {
        assertEquals(0, Comment.getNesting(""));
        assertEquals(1, Comment.getNesting("1"));
        assertEquals(2, Comment.getNesting("1.12"));
        assertEquals(3, Comment.getNesting("1.12.13"));
        assertEquals(4, Comment.getNesting("1.12.13.14"));
    }
}
