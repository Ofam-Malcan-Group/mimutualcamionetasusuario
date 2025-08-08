package com.ofam.mimutualcamionetasusuario.entities;

import java.io.Serializable;

public class User implements Serializable {
    private final String userID;
    private final String passID;

    public User(String userID, String passID) {
        this.userID = userID;
        this.passID = passID;
    }

    public String getUserID() {
        return userID;
    }

    public String getPassID() {
        return passID;
    }
}
