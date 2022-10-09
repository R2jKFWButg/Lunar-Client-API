package com.ghaph.protocol.packets.server;

import com.ghaph.protocol.LunarSocket;
import com.ghaph.protocol.packets.LunarPacket;
import com.ghaph.utils.ByteBufWrapper;

import java.util.ArrayList;
import java.util.List;

public class SPacketGiveEmotes extends LunarPacket {

    private List<Integer> owned;
    private List<Integer> equipped;

    @Override
    public void write(ByteBufWrapper buf) {}

    @Override
    public void read(ByteBufWrapper wrapper) {
        int n = wrapper.readVarInt();
        this.owned = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            this.owned.add(wrapper.readVarInt());
        }
        n = wrapper.readVarInt();
        this.equipped = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            this.equipped.add(wrapper.readVarInt());
        }
    }

    @Override
    public void handle(LunarSocket websocket) {
        websocket.getClient().setEmotes(this);
    }

    public List<Integer> getOwnedEmotes() {
        return owned;
    }

    public List<Integer> getEquippedEmotes() {
        return equipped;
    }
}
