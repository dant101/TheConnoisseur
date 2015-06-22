package Database;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alexandre on 20/06/2015.
 */
public class FriendsOnlineDB extends OnlineDB {

    public FriendsOnlineDB(Postgresql database) {
        super(database);
        allArguments.add("id");
        allArguments.add("username");
        allArguments.add("friend_username");
        allArguments.add("confirmed");
    }

    /*Checks if entry is not already in DB and creates a friend request between 2 users
    username: user creating the request
    friend_username: friend to add
     */
    public void createFriendRequest(String username, String friend_username) {
        String query = "SELECT * " +
                "FROM friends " +
                "WHERE username = ? " +
                "AND friend_username = ?";
        List<List<String>> queryResult = database.selectFriendsQuery(query, this.allArguments,
                username, friend_username);

        if(queryResult.size() == 0) {

            query = "INSERT INTO friends(username, friend_username, confirmed) " +
                    "VALUES (?, ?, ?)";
            boolean confirmed = false;
            database.createFriendQuery(query, username, friend_username,
                    confirmed);
            database.createFriendQuery(query, friend_username, username,
                    confirmed);
        }
    }

    /*Confirms a friend request between two people
    * Sets confirmed to true in the database
    */
    public void confirmFriendRequest(String username, String friend_username) {
        boolean confirm = true;

        String query = "UPDATE friends" +
                        " SET confirmed = true" +
                        " WHERE username = ? " +
                        " AND friend_username = ?";
        database.createFriendQuery(query, username, friend_username, null);
        database.createFriendQuery(query, friend_username, username, null);
    }

    /*-Deletes a friend from the database
      -Cancels a friend request from the database
     */
    public void deleteFriend(String username, String friend_username) {
        String query = "DELETE FROM friends" +
                " WHERE username = ? " +
                " AND friend_username = ?";
        database.createFriendQuery(query, username, friend_username, null);
        database.createFriendQuery(query, friend_username, username, null);
    }

    /*Returns all the friends that a user has*/
    public List<FriendsOnlineDBFormat> getAllFriends(String username) {
        String query = "SELECT * " +
                "FROM friends " +
                "WHERE username = ?";
        List<List<String>> queryResult = database.selectFriendsQuery(query, this.allArguments, username, null);
        return format(queryResult, FriendsOnlineDBFormat.class);
    }

    /*Returns a list of all users who's username is or starts with the given string
    * User has to be present in friends table*/
    public List<FriendsOnlineDBFormat> getUserByUsername(String username) {
        //using a different selectQuery format here to prevent SQLInjections
        String query = "SELECT * " +
                "FROM friends " +
                "WHERE username = ?";
        List<List<String>> queryResult = database.loginAndFriendQuery(query, this.allArguments, username);
        return format(queryResult,FriendsOnlineDBFormat.class);
    }

    /*Returns a list of all users who's username is or starts with the given string
     * User has to be present in friends table*/
    public List<FriendsOnlineDBFormat> getUserByFriendUsername(String friend_username) {
        //using a different selectQuery format here to prevent SQLInjections
        String query = "SELECT * " +
                "FROM friends " +
                "WHERE friend_username = ?";
        List<List<String>> queryResult = database.loginAndFriendQuery(query, this.allArguments, friend_username);
        return format(queryResult,FriendsOnlineDBFormat.class);
    }

    @Override
    <FriendsOnlineDBFormat> List<FriendsOnlineDBFormat> format(
            List<List<String>> queryResult,
            Class<FriendsOnlineDBFormat> cls) {

        List<FriendsOnlineDBFormat> result = new ArrayList<>();

        for(List<String> strings : queryResult) {
            try {
                FriendsOnlineDBFormat current = cls.getDeclaredConstructor(List.class).newInstance(strings);
                result.add(current);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return result;
    }
}
