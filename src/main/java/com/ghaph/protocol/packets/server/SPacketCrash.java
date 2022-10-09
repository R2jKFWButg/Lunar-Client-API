package com.ghaph.protocol.packets.server;

import com.ghaph.protocol.LunarSocket;
import com.ghaph.protocol.packets.LunarPacket;
import com.ghaph.utils.ByteBufWrapper;

import java.io.IOException;

public class SPacketCrash extends LunarPacket {

    @Override
    public void write(ByteBufWrapper buf) {}

    @Override
    public void read(ByteBufWrapper buf) throws IOException {}

    @Override
    public void handle(LunarSocket socket) {
        // will force a reconnect to imitate a real crash
        socket.close();
    }
}
