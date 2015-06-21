package com.theconnoisseur.android.Controller;

import android.database.MatrixCursor;

import com.theconnoisseur.android.Activities.FriendSearch;
import com.theconnoisseur.android.Model.FriendContent;

import java.util.List;

import Database.ConnoisseurDatabase;
import Database.FriendsOnlineDBFormat;
import Util.ResourceCache;

public class FriendsController {
    private final long TIME_TO_LIVE = 1 * 60 * 60 * 1000; //1 hour
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

    public MatrixCursor getYourFriends(String username) {
        List<FriendsOnlineDBFormat> yourFriends = friendsCache.get(YOUR_FRIENDS_ID);

        if (yourFriends == null) {
            yourFriends = ConnoisseurDatabase.getInstance().getFriendsTable().getAllFriends(username);
            friendsCache.put(YOUR_FRIENDS_ID, yourFriends);
        }

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
        List<FriendsOnlineDBFormat> pendingFriends = friendsCache.get(YOUR_FRIENDS_ID);

        if (pendingFriends == null) {
            //TODO: search all pending friends (others have created friend request)
            //yourFriends = ConnoisseurDatabase.getInstance().getFriendsTable().getAllFriends(username);
            friendsCache.put(PENDING_FRIENDS_ID, pendingFriends);
        }

        MatrixCursor matrixCursor = new MatrixCursor(FriendContent.columns);
        for (FriendsOnlineDBFormat row : pendingFriends) {
            matrixCursor.addRow(new Object[] {
                    row.getUsername(),
                    row.getFriend_username(),
                    row.isConfirmed()
            });
        }

        return matrixCursor;
    }
}
