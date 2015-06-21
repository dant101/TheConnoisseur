package com.theconnoisseur.android.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Activities.Interfaces.FriendListsInterface;
import com.theconnoisseur.android.Controller.FriendsController;
import com.theconnoisseur.android.Model.FriendContent;
import com.theconnoisseur.android.Model.GlobalPreferenceString;

public class FriendSearchActivity extends ActionBarActivity implements FriendListsInterface {
    public static final String TAG = FriendSearchActivity.class.getSimpleName();

    Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_friend_search);

        SearchView searchView = (SearchView) findViewById(R.id.friend_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        handleIntent(getIntent());
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Loads both cursors to populate friends and pending friends list
        new YourFriendsCursorPreparationTask(this).execute();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String query = intent.getStringExtra(SearchManager.QUERY);
        Log.d(TAG, "onNewIntent: " + query);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // Do work using string
            Log.d(TAG, "search activity (search): " + query);

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            //Users selects suggestion
            Uri uri = intent.getData();
            Log.d(TAG, "ACTIONVIEW: uri - " + uri.toString());
        } else {
            Log.d(TAG, "other intent");
        }
    }

    @Override
    public void friendsLoaded(Cursor c) {
        ListView friendsList = (ListView) findViewById(R.id.friends_list);

        String[] from = new String[] {FriendContent.friend, FriendContent.confirmed};
        int[] to = new int[] {R.id.friend, R.id.collection_list_item};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.your_friend_list_item, c, from, to, BIND_IMPORTANT);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.collection_list_item) {
                    //TODO: set background based on whether friendship is pending
                    return true;
                }
                return false;
            }
        });

        mAdapter = adapter;
        friendsList.setAdapter(adapter);
    }

    @Override
    public void pendingFriendsLoaded(Cursor c) {

    }

    private class YourFriendsCursorPreparationTask extends AsyncTask<Void, Void, Void> {

        FriendListsInterface mCallback;
        Cursor mCursor;

        public YourFriendsCursorPreparationTask(FriendListsInterface callback) {
            this.mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String username = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString(GlobalPreferenceString.USERNAME_PREF, GlobalPreferenceString.GUEST);
            Log.d(TAG, "getYourFriends for: " + username);
            mCursor = FriendsController.getsInstance().getYourFriends(username);

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "YourFriendsCursorPreparationTask onPostExecute called");

            mCallback.friendsLoaded(mCursor);
            super.onPostExecute(result);
        }

    }

}
