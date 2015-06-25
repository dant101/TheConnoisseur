package com.theconnoisseur.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Activities.Interfaces.CursorCallback;
import com.theconnoisseur.android.Model.Comment;
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.GlobalPreferenceString;
import com.theconnoisseur.android.Model.LanguageSelection;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import Database.ConnoisseurDatabase;
import Util.CommentController;
import Util.ContentDownloadHelper;
import Util.CursorHelper;
import Util.ToastHelper;

public class CommentActivity extends Activity implements CursorCallback {
    public static final String TAG = CommentActivity.class.getSimpleName();

    private final int upVoteScore = 1;
    private final int downVoteScore = -1;

    private ImageView mWordIllustration;
    private TextView mFlagText;
    private ImageView mFlag;
    private TextView mCommentsText;
    private ListView mComments;
    private EditText mCommentEditText;
    private Button mPost;
    private String mUsername;

    private int mCommentsFrequency;

    private Cursor mCursor;
    private SimpleCursorAdapter mAdapter;

    private Map<Integer, Integer> mVoteMap;
    private static final String VOTE = "vote_";

    private int mWordId;
    private String mWord;
    private String mImageUri;
    private String mFlagUri;
    private String mLanguageName;

    private boolean firstQuery = true;
    private boolean mReplying = false;
    private int mReplyingCommentId;
    private String mReplyingParentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent i = getIntent();
        mWordId = i.getIntExtra(ExerciseContent.WORD_ID, 0);
        mWord = i.getStringExtra(ExerciseContent.WORD);
        mImageUri = i.getStringExtra(ExerciseContent.IMAGE_URL);
        mFlagUri = i.getStringExtra(LanguageSelection.LANGUAGE_IMAGE_URL);
        mLanguageName = i.getStringExtra(LanguageSelection.LANGUAGE_NAME);

        mVoteMap = new HashMap<Integer, Integer>();

    }

    @Override
    protected void onStart() {
        super.onStart();

        new CursorPreparationTask(this).execute();

        mWordIllustration = (ImageView) findViewById(R.id.word_illustration);
        mCommentsText = (TextView) findViewById(R.id.comments_text);
        mComments = (ListView) findViewById(R.id.comments);
        mCommentEditText = (EditText) findViewById(R.id.comment_editText);
        mPost = (Button) findViewById(R.id.post);

        ContentDownloadHelper.loadImage(getApplicationContext(), mWordIllustration, mImageUri);

        mUsername = PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalPreferenceString.USERNAME_PREF, "Guest");
    }

    @Override
    protected void onStop() {
        // Iterates through set of voted upon comments and updates global shared preferences (enforcing single vote rule)
        Iterator it = mVoteMap.entrySet().iterator();
        int entry;

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        while (it.hasNext()) {
            entry = Integer.valueOf(it.next().toString().split("=")[0]);

            Log.d(TAG, "Entry: " + String.valueOf(entry));
            //Sends voting request to online db if not voted before
            if (!prefs.getBoolean(VOTE + String.valueOf(entry), false)) {
                int vote_score = mVoteMap.get(entry);
                CommentController.getInstance().vote(entry, vote_score);
                editor.putBoolean(VOTE + String.valueOf(entry), true);
            }
        }
        editor.apply();

        super.onStop();
    }

    @Override
    public void CursorLoaded(Cursor c) {
        if (!firstQuery) { mAdapter.changeCursor(c); return; }
        firstQuery = false;

        this.mCursor = c;
        mCommentsFrequency = c.getCount(); mCommentsText.setText(String.valueOf(mCommentsFrequency) + " Comments");

        String[] from = new String[] {Comment.username, Comment.time, Comment.comment, Comment.score, Comment.nesting};
        int[] to = new int[] {R.id.username, R.id.date, R.id.comment, R.id.score, R.id.comment_item};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.comment_item, mCursor, from, to, BIND_IMPORTANT);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.comment_item) {
                    int nesting = cursor.getInt(columnIndex);
                    view.setPadding((int) getResources().getDimension(R.dimen.comment_nesting) * nesting, 0, 0, 0);

                    int comment_id = cursor.getInt(cursor.getColumnIndex(Comment.comment_id));
                    int known_score = mVoteMap.containsKey(comment_id) ? mVoteMap.get(comment_id) : 0;

                    view.findViewById(R.id.reply).setOnClickListener(new replyListener(cursor.getInt(cursor.getColumnIndex(Comment.comment_id)), cursor.getString(cursor.getColumnIndex(Comment.parent_path)), view.findViewById(R.id.reply)));
                    view.findViewById(R.id.upvote).setOnClickListener(new voteListener(cursor.getInt(cursor.getColumnIndex(Comment.comment_id)), upVoteScore, view.findViewById(R.id.upvote), view.findViewById(R.id.downvote), known_score));
                    view.findViewById(R.id.downvote).setOnClickListener(new voteListener(cursor.getInt(cursor.getColumnIndex(Comment.comment_id)), downVoteScore, view.findViewById(R.id.downvote), view.findViewById(R.id.upvote), known_score));

                    return true;
                }

                return false;
            }
        });

        mAdapter = adapter;
        mComments.setAdapter(mAdapter);

        setListeners();
    }

    private void setListeners() {
        mPost.setOnClickListener(new commentPost());
        mCommentEditText.setOnEditorActionListener(new commentPost());

    }

    // Posts the user comment locally
    private void postMessage() {
        String comment = mCommentEditText.getText().toString();
        // Process comment for swearing,etc
        boolean appropriate = ConnoisseurDatabase.getInstance().getCommentTable().isCommentSafe(comment);
        if (!appropriate) {
            ToastHelper.toast(this, "Sorry, we don't feel your comment was appropriate");
        } else {
            if(mReplying && mReplyingCommentId != 0) {
                CommentController.getInstance().comment(mWordId, mUsername, mReplyingParentPath + "." + mReplyingCommentId, comment);
            } else {
                Log.d(TAG, "Posting a comment");
                CommentController.getInstance().comment(mWordId, mUsername, comment);
            }
            new CursorPreparationTask(this).execute();
        }

        mCommentEditText.setText("");
        mCommentEditText.clearFocus();
        mComments.requestFocus();
    }

    private class replyListener implements View.OnClickListener {
        private int mCommentId;
        private String mParentPath;
        private View mReply;


        public replyListener(int comment_id, String parent_path, View reply) {
            this.mParentPath = parent_path;
            this.mCommentId = comment_id;
            this.mReply = reply;

            mReply.setBackgroundColor(getResources().getColor(R.color.transparent));
        }

        @Override
        public void onClick(View v) {
            if (mReplying) {
                mReply.setBackgroundColor(getResources().getColor(R.color.transparent));
                mCommentEditText.setHint(getResources().getString(R.string.comment_hint_normal));
                mReplying = false;
            } else {
                mReply.setBackgroundColor(getResources().getColor(R.color.comment_blue));
                mReplyingParentPath = mParentPath;
                mReplyingCommentId = mCommentId;
                mCommentEditText.setHint(getResources().getString(R.string.comment_hint_reply));
                mReplying = true;
            }

            //ToastHelper.toast(getApplicationContext(), "Reply: " + mParentPath);
        }
    }

    /**
     * Listener for voting. Updates the UI, makes online database vote request. (Score value doesn't change)
     * User can only vote once per comment and only first vote counts
     */
    private class voteListener implements View.OnClickListener {
        private int mCommentId;
        private int mVote;
        private View mView;
        private View mAlternate;
        private int mKnownScore;

        public voteListener(int comment_id, int vote, View v, View alternate, int known_score) {
            this.mCommentId = comment_id;
            this.mVote = vote;
            this.mView = v;
            this.mAlternate = alternate;
            this.mKnownScore = known_score;

            setVisuals();
        }

        private void setVisuals() {
            if (mKnownScore == 0) {
                ((ImageView)mView).setImageResource(R.drawable.arrow_black);
                ((ImageView)mAlternate).setImageResource(R.drawable.arrow_black);
            } else  if (mVote == mKnownScore) {
                ((ImageView)mView).setImageResource(R.drawable.arrow_green);
                ((ImageView)mAlternate).setImageResource(R.drawable.arrow_black);
            } else {
                ((ImageView)mView).setImageResource(R.drawable.arrow_black);
                ((ImageView)mAlternate).setImageResource(R.drawable.arrow_green);
            }
        }

        @Override
        public void onClick(View v) {
            mKnownScore = mVote;
            setVisuals();

            mVoteMap.put(mCommentId, mVote);
        }
    }

    /**
     * Listener that responds to comment action. Updates visual comments and sends comment request to remote db
     */
    private class commentPost implements TextView.OnEditorActionListener, View.OnClickListener {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_DOWN) {
                postMessage();
                return true;
            }
            return false;
        }

        @Override
        public void onClick(View v) {
            postMessage();
        }
    }

    // Async task that gets all the available comments for given word id
    private class CursorPreparationTask extends AsyncTask<Void, Void, Void> {
        CursorCallback mCallback;
        Cursor mCursor;

        public CursorPreparationTask(CursorCallback callback) { this.mCallback = callback; }

        @Override
        protected Void doInBackground(Void... params) {
            mCursor = CommentController.getInstance().getComments(mWordId); //Should be mWord_Id when ready for proper test

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "CommentsActivity CursorPreparationTask onPostExecute Called");

            mCallback.CursorLoaded(mCursor);

            //CursorHelper.toString(mCursor);

            super.onPostExecute(result);
        }
    }

}
