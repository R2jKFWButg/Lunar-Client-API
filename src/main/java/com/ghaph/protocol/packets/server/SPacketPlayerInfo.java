package com.ghaph.protocol.packets.server;

import com.ghaph.protocol.LunarSocket;
import com.ghaph.protocol.packets.LunarPacket;
import com.ghaph.protocol.user.PlayerInfo;
import com.ghaph.utils.ByteBufWrapper;

import java.io.IOException;

public class SPacketPlayerInfo extends LunarPacket {

    private PlayerInfo playerInfo;

    @Override
    public void write(ByteBufWrapper buf) {

    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {
        this.playerInfo = new PlayerInfo(buf);
    }

    @Override
    public void handle(LunarSocket socket) {
        socket.getClient().setPlayerInfo(this.playerInfo);
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
