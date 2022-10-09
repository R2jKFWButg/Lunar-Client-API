package com.ghaph.protocol.packets.server;

import com.ghaph.protocol.LunarSocket;
import com.ghaph.protocol.packets.LunarPacket;
import com.ghaph.utils.ByteBufWrapper;

import java.io.IOException;

// TODO Implement
public class SPacketBulk extends LunarPacket {

    @Override
    public void write(ByteBufWrapper buf) {

    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
        final String result = buf.readString();
        if (!result.equals("{\"bulk\":[]}")) {
            System.out.println("NEW OUTPUT FOR THIS UNKNOWN PACKET!!");
            System.out.println(result);
        }
    }

    @Override
    public void handle(LunarSocket socket) {

    }
}
