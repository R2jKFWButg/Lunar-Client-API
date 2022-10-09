package com.ghaph.protocol.user;

public enum PlayerStatus {

    ONLINE( "Online"),
    AWAY("Away"),
    BUSY("Busy"),
    OFFLINE( "Offline");

    public final String name;

    PlayerStatus(final String name) {
        this.name = name;
    }
}
