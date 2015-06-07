package com.theconnoisseur.android.Activities;

import android.app.Activity;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Activities.Interfaces.CursorCallback;
import com.theconnoisseur.android.Model.Comment;

import Util.CursorHelper;
import Util.ResourceDownloader;

public class CommentActivity extends Activity implements CursorCallback {
    public static final String TAG = CommentActivity.class.getSimpleName();

    private ImageView mWordIllustration;
    private TextView mFlagText;
    private ImageView mFlag;
    private TextView mCommentsText;
    private ListView mComments;

    private int mCommentsFrequency;

    private Cursor mCursor;
    private Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
    }

    @Override
    protected void onStart() {
        super.onStart();

        new CursorPreparationTask(this).execute();

        mWordIllustration = (ImageView) findViewById(R.id.word_illustration);
        mFlagText = (TextView) findViewById(R.id.flag_text);
        mFlag = (ImageView) findViewById(R.id.flag);
        mCommentsText = (TextView) findViewById(R.id.comments_text);
        mComments = (ListView) findViewById(R.id.comments);

    }

    @Override
    public void CursorLoaded(Cursor c) {
        this.mCursor = c;
        mCommentsFrequency = c.getCount(); mCommentsText.setText(String.valueOf(mCommentsFrequency) + " Comments");

        String[] from = new String[] {Comment.username, Comment.time, Comment.comment, Comment.score};
        int[] to = new int[] {R.id.username, R.id.date, R.id.comment, R.id.score};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.comment_item, mCursor, from, to, BIND_IMPORTANT);
        mComments.setAdapter(adapter);

    }

    // Async task that gets all the available comments for given word id
    private class CursorPreparationTask extends AsyncTask<Void, Void, Void> {
        CursorCallback mCallback;
        Cursor mCursor;

        public CursorPreparationTask(CursorCallback callback) { this.mCallback = callback; }

        @Override
        protected Void doInBackground(Void... params) {
            mCursor = ResourceDownloader.downloadComments(getApplicationContext(), 1);

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
