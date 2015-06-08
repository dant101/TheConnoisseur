package Util;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import Database.CommentOnlineDBFormat;

/**
 * Utility class for ordering comments
 */
public class CommentUtil implements Comparator<CommentOnlineDBFormat> {
    public static final String TAG = CommentUtil.class.getSimpleName();

    List<CommentOnlineDBFormat> mRows;
    HashMap<Integer, Integer> mScores;

    public CommentUtil(List<CommentOnlineDBFormat> rows) {
        mRows = rows;
    }

    // For testing
    public void setScores(HashMap<Integer, Integer> map) {
        mScores = map;
        mScores.put(0, 0);
    }

    @Override
    public int compare(CommentOnlineDBFormat lhs, CommentOnlineDBFormat rhs) {
        String lPath = lhs.getParent_path() + "." + String.valueOf(lhs.getComment_id());
        String rPath = rhs.getParent_path() + "." + String.valueOf(rhs.getComment_id());
        return compare(lPath, rPath);
    }

    public void sort() {
        if (mRows == null) { return; }

        HashMap<Integer, Integer> scores = new HashMap<>();

        for (CommentOnlineDBFormat row : mRows) {
            scores.put(row.getComment_id(), row.getScore());
        }
        mScores = scores;

        Collections.sort(mRows, this);
    }

    public int compare(String lhs, String rhs) {
        if (mRows == null || mScores == null) { return 0; }

        String[] lhsPath = lhs.split("\\.");
        String[] rhsPath = rhs.split("\\.");

        int lhsPathLength = lhsPath.length;
        int rhsPathLength = rhsPath.length;
        int min = Math.min(lhsPathLength, rhsPathLength);

        // Compares each matching path element by their respective scores
        for (int i = 0; i < min; i++) {
            int lhsItem = Integer.parseInt(lhsPath[i]);
            int rhsItem = Integer.parseInt(rhsPath[i]);

            if (lhsItem == rhsItem) {
                // Check next time of parent path hierarchy
                continue;
            } else {
                return integerCompare(mScores.get(lhsItem), mScores.get(rhsItem));
            }
        }

        // Comment with shorter path comes first (less nested)
        return integerCompare(rhsPathLength, lhsPathLength);
    }

    private int integerCompare(int l, int r) {
        if (l < r) {
            return -1;
        } else if (l == r) {
            return 0;
        } else {
            return 1;
        }
    }
}
