package com.theconnoisseur.android.Activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.theconnoisseur.R;
import com.theconnoisseur.android.Activities.Interfaces.FriendListsInterface;
import com.theconnoisseur.android.Controller.FriendsController;
import com.theconnoisseur.android.Model.FriendContent;
import com.theconnoisseur.android.Model.GlobalPreferenceString;

import Database.ConnoisseurDatabase;
import Database.FriendsOnlineDBInterface;

public class FriendSearchActivity extends ActionBarActivity implements FriendListsInterface, FriendsOnlineDBInterface {
    public static final String TAG = FriendSearchActivity.class.getSimpleName();

    Adapter mFriendsAdapter;
    Adapter mPendingFriendsAdapter;
    String mUsername;

    ListView mFriends;
    ListView mPendingFriends;

    Typeface roboto_regular;
    Typeface roboto_bold;
    Typeface roboto_italic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_friend_search);

        mUsername = PreferenceManager.getDefaultSharedPreferences(this).getString(GlobalPreferenceString.USERNAME_PREF, "");
        mFriends = (ListView) findViewById(R.id.friends_list);
        mPendingFriends = (ListView) findViewById(R.id.pending_friends_list);

        SearchView searchView = (SearchView) findViewById(R.id.friend_search);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        handleIntent(getIntent());

        Typeface roboto_regular = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Regular.ttf");
        Typeface roboto_bold = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Bold.ttf");
        Typeface roboto_italic = Typeface.createFromAsset(getAssets(), "fonts/RobotoCondensed-Italic.ttf");

        ((TextView)findViewById(R.id.heading1)).setTypeface(roboto_bold);
        ((TextView)findViewById(R.id.heading2)).setTypeface(roboto_bold);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Loads both cursors to populate friends and pending friends list
        setListeners();
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
            addFriend(query);

        } else if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            //Users selects suggestion
            Uri uri = intent.getData();
            Log.d(TAG, "ACTIONVIEW: uri - " + uri.toString());
            addFriend(uri.toString());
        } else {
            Log.d(TAG, "other intent");
        }
    }

    private void setListeners() {

    }

    /**
     * Adds to user's friends: @param friend
     * @param friend
     */
    private void addFriend(final String friend) {
        final FriendsOnlineDBInterface activity = this;
        if (mUsername != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // adds new friends (preventing duplicated friendships)
                    FriendsController.getsInstance().addFriend(activity, mUsername, friend);
                }
            }).start();
        }

    }

    /**
     * Removes a friend from list of friends - UI update and online database change but item not immediately removed
     * @param friend
     */
    private void removeFriend(final String friend) {
        Log.d(TAG, "removing friend: " + friend);
        final FriendsOnlineDBInterface activity = this;
        if(friend != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FriendsController.getsInstance().removeFriend(activity, mUsername, friend);
                }
            }).start();
        }
    }

    /**
     * Confirms a friend request - removes item from pending list and adds to friends)
     * @param friend String username of friend
     */
    private void confirmFriend(final String friend) {
        Log.d(TAG, "confirming friend: " + friend);
        final FriendsOnlineDBInterface activity = this;
        if(friend != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FriendsController.getsInstance().confirmFriend(activity, mUsername, friend);
                }
            }).start();
        }
    }

    /**
     * Declines a friend request - removes it from the database
     * @param friend
     */
    private void declineFriend(final String friend) {
        Log.d(TAG, "declining friend: " +friend);
        final FriendsOnlineDBInterface activity = this;
        if(friend != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    FriendsController.getsInstance().declineFriend(activity, mUsername, friend);
                }
            }).start();
        }
    }

    //TODO: Refactoring here - same cursor callback methods, swap in different layout file
    @Override
    public void friendsLoaded(Cursor c) {
        ListView friendsList = (ListView) findViewById(R.id.friends_list);

        String[] from = new String[] {FriendContent.friend, FriendContent.confirmed};
        int[] to = new int[] {R.id.friend, R.id.friend_item};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.your_friend_list_item, c, from, to, BIND_IMPORTANT);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.friend_item) {

                    TextView friend_textView = (TextView)view.findViewById(R.id.friend);
                    friend_textView.setTypeface(roboto_regular);
                    final String friend = friend_textView.getText().toString();
                    view.findViewById(R.id.delete_friend).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "Selected friend: " + friend);
                            removeFriend(friend);
                        }
                    });
                    return true;
                }
                return false;
            }
        });

        mFriendsAdapter = adapter;
        friendsList.setAdapter(adapter);
    }

    @Override
    public void pendingFriendsLoaded(Cursor c) {
        ListView pendingList = (ListView) findViewById(R.id.pending_friends_list);

        String[] from = new String[] {FriendContent.username, FriendContent.confirmed};
        int[] to = new int[] {R.id.friend, R.id.pending_friend_item};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.pending_friend_list_item, c, from, to, BIND_IMPORTANT);

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.pending_friend_item) {
                    TextView friend_textView = (TextView)view.findViewById(R.id.friend);
                    final String friend = friend_textView.getText().toString();
                    view.findViewById(R.id.confirm_friend).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmFriend(friend);
                        }
                    });

                    view.findViewById(R.id.decline_friend).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            declineFriend(friend);
                        }
                    });
                    return true; //Other visuals?
                }
                return false;
            }
        });

        mPendingFriendsAdapter = adapter;
        pendingList.setAdapter(adapter);
    }

    @Override
    public void onUpdatedFriends() {
        new YourFriendsCursorPreparationTask(this).execute();
    }

    @Override
    public void onUpdatedPendingFriends() {
        new PendingFriendsCursorPreparationTask(this).execute();
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

    private class PendingFriendsCursorPreparationTask extends AsyncTask<Void, Void, Void> {

        FriendListsInterface mCallback;
        Cursor mCursor;

        public PendingFriendsCursorPreparationTask(FriendListsInterface callback) {
            this.mCallback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            String username = PreferenceManager.getDefaultSharedPreferences(getApplication()).getString(GlobalPreferenceString.USERNAME_PREF, GlobalPreferenceString.GUEST);
            Log.d(TAG, "getPendingFriends for: " + username);
            mCursor = FriendsController.getsInstance().getPendingFriends(username);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Log.d(TAG, "PendingFriendsCursorPreparationTask onPostExecute called");

            mCallback.pendingFriendsLoaded(mCursor);
            super.onPostExecute(result);
        }
    }

}
