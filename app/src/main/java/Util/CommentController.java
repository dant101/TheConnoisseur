package Util;

import android.database.MatrixCursor;
import android.util.Log;

import com.theconnoisseur.android.Model.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import Database.CommentOnlineDB;
import Database.CommentOnlineDBFormat;
import Database.ConnoisseurDatabase;

/**
 * Main utility class for requesting and posting comments. Caches comments. Posts
 * new comments separately from any updates to the UI listView of comments.
 */
public class CommentController {
    public static final String TAG = CommentController.class.getSimpleName();
    private final long COMMENTS_TIME_TO_LIVE = 2 * 60 * 1000; //2 minutes

    private static CommentController sInstance = null;

    private  ResourceCache<Integer, List<CommentOnlineDBFormat>> commentsCache;

    public static synchronized CommentController getInstance() {
        if (sInstance == null) {
            sInstance = new CommentController();
        }
        return sInstance;
    }

    private CommentController() {
        commentsCache = new ResourceCache<Integer, List<CommentOnlineDBFormat>>(COMMENTS_TIME_TO_LIVE, 10);
    }

    public MatrixCursor getComments(int word_id) {

        commentsCache.cleanUp();
        List<CommentOnlineDBFormat> comments = commentsCache.get(word_id);

        if (comments == null) {
            comments = ResourceDownloader.downloadComments(word_id);
            commentsCache.put(word_id, comments);
        }

        new CommentUtil(comments).sort();
        Collections.reverse(comments);

        MatrixCursor matrixCursor = new MatrixCursor(Comment.columns);
        for (CommentOnlineDBFormat row :comments) {
            matrixCursor.addRow(new Object[] {
                    row.getComment_id(),
                    row.getWord_id(),
                    row.getUsername(),
                    row.getComment(),
                    row.getTime(),
                    row.getScore(),
                    row.getParent_path(),
                    Comment.getNesting(row.getParent_path())});
        }

        return matrixCursor;
    }

    public void comment(final int word_id, final String username, final String comment) {
        comment(word_id, username, "0", comment);
    }

    public void vote(int word_id, int score) {
        CommentOnlineDB cDb = ConnoisseurDatabase.getInstance().getCommentTable();
        if (score > 0) {
            Log.d(TAG, "Voting up comment with id: " + String.valueOf(word_id));
            cDb.addOneToScore(word_id);
        } else {
            Log.d(TAG, "Voting down comment with id: " + String.valueOf(word_id));
            cDb.subtractOneFromScore(word_id);
            }

    }


    public void comment(final int word_id, final String username, final String parent_path, final String comment) {
        List<CommentOnlineDBFormat> comments = commentsCache.get(word_id);
        List<String> row = new ArrayList<>();
        row.add(0, "0");
        row.add(1, String.valueOf(word_id));
        row.add(2, username);
        row.add(3, comment);
        row.add(4, String.valueOf(0));
        row.add(5, parent_path);
        row.add(6, Util.Time.getCurrentTimestamp().toString());

        comments.add(new CommentOnlineDBFormat(row));

        Thread thread = new Thread() {
            public void run() {
                //TODO: Intelligent way of queuing comments and sending them off when internet access...
                ConnoisseurDatabase.getInstance().getCommentTable().createComment(word_id, username, comment);
            }
        };

        thread.start();
    }
}
