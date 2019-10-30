package com.unca.android.uncacampusbreeze;

import java.util.Date;

public class User {

    private String username;
    private Date timeCreated;

    public User(String username, Date timeCreated) {
        this.username = username;
        this.timeCreated = timeCreated;
    }

    public String getUsername() {
        return username;
    }

    public Date getTimeCreated() {
        return timeCreated;
    }


}
