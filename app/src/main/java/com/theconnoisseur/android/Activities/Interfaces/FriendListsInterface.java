package com.theconnoisseur.android.Activities.Interfaces;

import android.database.Cursor;

public interface FriendListsInterface {

    public void friendsLoaded(Cursor c);
    public void pendingFriendsLoaded(Cursor c);
}
