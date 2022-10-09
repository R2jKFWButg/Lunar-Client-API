package com.ghaph.protocol.packets.server;

import com.ghaph.protocol.LunarSocket;
import com.ghaph.protocol.packets.LunarPacket;
import com.ghaph.protocol.packets.client.CPacketTasklist;
import com.ghaph.utils.ByteBufWrapper;

import java.io.IOException;

public class SPacketTasklist extends LunarPacket {

    @Override
    public void write(ByteBufWrapper buf) {}

    @Override
    public void read(ByteBufWrapper buf) throws IOException {}

    @Override
    public void handle(LunarSocket socket) {
        if (socket.getLunarSettings().spoofTasksList()) {
            socket.sendPacket(new CPacketTasklist("\n" +
                    "Image Name                     PID Session Name        Session#    Mem Usage\n" +
                    "========================= ======== ================ =========== ============\n" +
                    "System Idle Process              0 Services                   0          8 K\n" +
                    "System                           4 Services                   0      7,244 K\n" +
                    "Registry                       172 Services                   0     60,972 K"));
        }
    }
}
