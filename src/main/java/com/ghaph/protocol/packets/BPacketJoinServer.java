package com.ghaph.protocol.packets;

import com.ghaph.protocol.LunarSocket;
import com.ghaph.utils.ByteBufWrapper;

import java.io.IOException;

public class BPacketJoinServer extends LunarPacket {

    private String uuid = "";
    private String server = "";

    public BPacketJoinServer() {

    }

    public BPacketJoinServer(String uuid, String server) {
        this.uuid = uuid;
        this.server = server;
    }

    @Override
    public void write(ByteBufWrapper buf) {
        buf.writeString(this.uuid);
        buf.writeString(this.server);
    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
        this.uuid = buf.readString();
        this.server = buf.readString();
    }

    @Override
    public void handle(LunarSocket socket) {}
}
