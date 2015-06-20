package Database;

import java.util.List;

import Database.Database;

/**
 * Created by Alexandre on 20/06/2015.
 */
public class FriendsOnlineDBFormat {
    private int id;
    private String username;
    private String friend_username;
    private boolean confirmed;

    public FriendsOnlineDBFormat(List<String> list) {
        this.id = Integer.parseInt(list.get(0));
        this.username = list.get(1);
        this.friend_username = list.get(2);
        this.confirmed = Boolean.parseBoolean(list.get(3));
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFriend_username() {
        return friend_username;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
