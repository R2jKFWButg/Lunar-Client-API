package com.ghaph.protocol.packets.server;

import com.google.common.collect.ImmutableList;
import com.ghaph.protocol.LunarSocket;
import com.ghaph.protocol.packets.LunarPacket;
import com.ghaph.utils.ByteBufWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SPacketFriendsList extends LunarPacket {

    private boolean consoleAllowed;
    private boolean friendRequestsEnabled;
    private Map<String, List<String>> onlineFriends;
    private Map<String, List<String>> offlineFriends;

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
        this.consoleAllowed = buf.buf().readBoolean();
        this.friendRequestsEnabled = buf.buf().readBoolean();

        final int onlineSize = buf.buf().readInt();
        final int offlineSize = buf.buf().readInt();

        this.onlineFriends = new HashMap<>();
        for (int i = 0; i < onlineSize; i++) {
            this.onlineFriends.put(buf.readStringFromBuffer(52), ImmutableList.of(buf.readStringFromBuffer(32), String.valueOf(buf.buf().readInt()), buf.readStringFromBuffer(256)));
        }

        this.offlineFriends = new HashMap<>();
        for (int i = 0; i < offlineSize; i++) {
            this.offlineFriends.put(buf.readStringFromBuffer(52), ImmutableList.of(buf.readStringFromBuffer(32), String.valueOf(buf.buf().readLong())));
        }
    }

    public boolean isConsoleAllowed() {
        return consoleAllowed;
    }

    public boolean isFriendRequestsEnabled() {
        return friendRequestsEnabled;
    }

    public Map<String, List<String>> getOnlineFriends() {
        return onlineFriends;
    }

    public Map<String, List<String>> getOfflineFriends() {
        return offlineFriends;
    }

    @Override
    public void handle(LunarSocket socket) {
        socket.getClient().setFriendsList(this);
    }

    @Override
    public void write(ByteBufWrapper buf) {}
}
