package com.theconnoisseur.android.Controller;

import android.database.MatrixCursor;

import com.theconnoisseur.android.Model.FriendContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import Database.ConnoisseurDatabase;
import Database.FriendsOnlineDB;
import Database.FriendsOnlineDBFormat;
import Util.ResourceCache;

public class FriendsController {
    private final long TIME_TO_LIVE = 1000;// Small amount of data - quick updates
    private final int YOUR_FRIENDS_ID = 2;
    private final int PENDING_FRIENDS_ID = 3;

    private static FriendsController sInstance = null;

    private ResourceCache<Integer, List<FriendsOnlineDBFormat>> friendsCache;

    public static synchronized  FriendsController getsInstance() {
        if (sInstance == null) {
            sInstance = new FriendsController();
        }
        return sInstance;
    }

    private FriendsController() {
        friendsCache = new ResourceCache<Integer, List<FriendsOnlineDBFormat>>(TIME_TO_LIVE, 10);
    }

    private List<FriendsOnlineDBFormat> getFriendsList(String username) {
        friendsCache.cleanUp();
        List<FriendsOnlineDBFormat> yourFriends = friendsCache.get(YOUR_FRIENDS_ID);

        if (yourFriends == null) {
            yourFriends = ConnoisseurDatabase.getInstance().getFriendsTable().getUserByUsername(username);
            friendsCache.put(YOUR_FRIENDS_ID, yourFriends);
        }

        return yourFriends;
    }



    private List<FriendsOnlineDBFormat> getPendingFriendsList(String username) {
        friendsCache.cleanUp();
        List<FriendsOnlineDBFormat> pendingFriends = friendsCache.get(PENDING_FRIENDS_ID);

        if (pendingFriends == null) {
            //TODO: search all pending friends (others have created friend request)
            pendingFriends = ConnoisseurDatabase.getInstance().getFriendsTable().getUserByFriendUsername(username);
            friendsCache.put(PENDING_FRIENDS_ID, pendingFriends);
        }

        return pendingFriends;
    }

    public MatrixCursor getYourFriends(String username) {
        List<FriendsOnlineDBFormat> yourFriends = getFriendsList(username);

        MatrixCursor matrixCursor = new MatrixCursor(FriendContent.columns);
        for (FriendsOnlineDBFormat row : yourFriends) {
            matrixCursor.addRow(new Object[] {
                    row.getId(),
                    row.getUsername(),
                    row.getFriend_username(),
                    row.isConfirmed()
            });
        }

        return matrixCursor;
    }

    public MatrixCursor getPendingFriends(String username) {
        List<FriendsOnlineDBFormat> pendingFriends = getPendingFriendsList(username);

        MatrixCursor matrixCursor = new MatrixCursor(FriendContent.columns);
        for (FriendsOnlineDBFormat row : pendingFriends) {
            matrixCursor.addRow(new Object[] {
                    row.getId(),
                    row.getUsername(),
                    row.getFriend_username(),
                    row.isConfirmed()
            });
        }

        return matrixCursor;
    }

    public void confirmFriend(String username, String friend) {
        ConnoisseurDatabase.getInstance().getFriendsTable().confirmFriendRequest(friend, username);
        ConnoisseurDatabase.getInstance().getFriendsTable().createFriendRequest(username, friend);
    }

    public void declineFriend(String username, String friend) {
        ConnoisseurDatabase.getInstance().getFriendsTable().deleteFriend(friend, username);
    }

    public void removeFriend(String username, String friend) {
        ConnoisseurDatabase.getInstance().getFriendsTable().deleteFriend(username, friend);
    }

    public void addFriend(String username, String friend) {
        if (!alreadyFriendsWith(username, friend)) {
            ConnoisseurDatabase.getInstance().getFriendsTable().createFriendRequest(username, friend);
        }
    }

    public boolean alreadyFriendsWith(String username, String friend) {
        List<FriendsOnlineDBFormat> yourFriends = getFriendsList(username);
        for (Iterator<FriendsOnlineDBFormat> iter = yourFriends.iterator(); iter.hasNext();) {
            String friend_username = iter.next().getFriend_username();
            if (friend.equals(friend_username)) {
                return true;
            }
        }
        return false;
    }



    public void clearCache() {
        friendsCache.remove(YOUR_FRIENDS_ID);
        friendsCache.remove(PENDING_FRIENDS_ID);
    }
}
