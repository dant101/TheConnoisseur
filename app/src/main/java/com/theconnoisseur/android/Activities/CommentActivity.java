package com.theconnoisseur.android.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Activities.Interfaces.CursorCallback;
import com.theconnoisseur.android.Model.Comment;
import com.theconnoisseur.android.Model.ExerciseContent;
import com.theconnoisseur.android.Model.LanguageSelection;

import org.w3c.dom.Text;

import Database.CommentOnlineDB;
import Database.ConnoisseurDatabase;
import Util.CommentController;
import Util.ContentDownloadHelper;
import Util.CursorHelper;
import Util.ResourceDownloader;
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

    private int mCommentsFrequency;

    private Cursor mCursor;
    private SimpleCursorAdapter mAdapter;

    private int mWordId;
    private String mWord;
    private String mImageUri;
    private String mFlagUri;
    private String mLanguageName;

    private boolean firstQuery = true;

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

        ContentDownloadHelper.loadImage(getApplicationContext(), mFlag, mFlagUri);
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

                    view.findViewById(R.id.reply).setOnClickListener(new replyListener(cursor.getString(cursor.getColumnIndex(Comment.parent_path))));
                    view.findViewById(R.id.upvote).setOnClickListener(new voteListener(cursor.getInt(cursor.getColumnIndex(Comment.comment_id)), upVoteScore, view.findViewById(R.id.upvote)));
                    view.findViewById(R.id.downvote).setOnClickListener(new voteListener(cursor.getInt(cursor.getColumnIndex(Comment.comment_id)), downVoteScore, view.findViewById(R.id.downvote)));

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
        boolean appropriate = true;
        if (!appropriate) {
            ToastHelper.toast(this, "Sorry, we don't feel your comment is appropriate");
            return;
        }

        CommentController.getInstance().comment(1, "TestCommenter", comment);
        new CursorPreparationTask(this).execute(); //

        mCommentEditText.setText("");
        mCommentEditText.clearFocus();
        mComments.requestFocus();

    }

    private class replyListener implements View.OnClickListener {
        private String mParentpath;


        public replyListener(String parentpath) {
            this.mParentpath = parentpath;
        }

        @Override
        public void onClick(View v) {
            //TODO: reply - new activity?
            ToastHelper.toast(getApplicationContext(), "Reply: " + mParentpath);
        }
    }

    /**
     * Listener for voting. Updates the UI, makes online database vote request. (Score value doesn't change)
     */
    private class voteListener implements View.OnClickListener {
        private int mCommentId;
        private int mVote;
        private View mView;

        public voteListener(int comment_id, int vote, View v) {
            this.mCommentId = comment_id;
            this.mVote = vote;
            this.mView = v;
        }

        @Override
        public void onClick(View v) {
            ((ImageView)mView).setImageResource(R.drawable.arrow_green);
            CommentController.getInstance().vote(mCommentId, mVote);
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
            mCursor = CommentController.getInstance().getComments(1); //Should be mWord_Id when ready for proper test

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "CommentsActivity CursorPreparationTask onPostExecute Called");

            mCallback.CursorLoaded(mCursor);

            CursorHelper.toString(mCursor);

            super.onPostExecute(result);
        }
    }


}
