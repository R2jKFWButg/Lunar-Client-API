package com.ghaph.protocol.user;

public class UserCosmetic {

    private final int id;
    private final boolean equipped;

    public UserCosmetic(int id, boolean equipped) {
        this.id = id;
        this.equipped = equipped;
    }

    public int getId() {
        return id;
    }

    public boolean isEquipped() {
        return equipped;
    }
}
