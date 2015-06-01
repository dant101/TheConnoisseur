package Database;

import java.util.List;

/**
 * Created by Alexandre on 28/05/2015.
 */
public class LoginOnlineDBFormat {
    private int id;
    private String username;
    private String password;
    private String email;
    private int salt;

    public LoginOnlineDBFormat(List<String> list) {
        this.id = Integer.parseInt(list.get(0));
        this.username = list.get(1);
        this.password = list.get(2);
        this.email = list.get(3);
        this.salt = Integer.parseInt(list.get(4));
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getSalt() {
        return salt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

}
