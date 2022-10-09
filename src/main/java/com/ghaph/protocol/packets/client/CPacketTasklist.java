package com.ghaph.protocol.packets.client;

import com.ghaph.protocol.LunarSocket;
import com.ghaph.protocol.packets.LunarPacket;
import com.ghaph.utils.ByteBufWrapper;

import java.io.IOException;

public class CPacketTasklist extends LunarPacket {

    private final String tasklistResult;

    public CPacketTasklist(String tasklistResult) {
        this.tasklistResult = tasklistResult;
    }

    @Override
    public void write(ByteBufWrapper buf) {
        final String[] lines = this.tasklistResult.split("\\n");
        buf.writeVarInt(lines.length);
        for (String str : lines) {
            buf.writeString(str);
        }
    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {}

    @Override
    public void handle(LunarSocket socket) {}
}
