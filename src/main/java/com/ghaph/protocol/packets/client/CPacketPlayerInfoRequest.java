package com.ghaph.protocol.packets.client;

import com.ghaph.protocol.LunarSocket;
import com.ghaph.protocol.packets.LunarPacket;
import com.ghaph.utils.ByteBufWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CPacketPlayerInfoRequest extends LunarPacket {

    private final List<UUID> uuids;

    public CPacketPlayerInfoRequest(List<UUID> uuids) {
        this.uuids = uuids;
    }

    public CPacketPlayerInfoRequest(UUID uuid) {
        this.uuids = new ArrayList<>();
        this.uuids.add(uuid);
    }

    @Override
    public void write(ByteBufWrapper buf) {
        buf.writeVarInt(this.uuids.size());
        for (UUID uuid : this.uuids) {
            buf.writeUUID(uuid);
        }
    }

    @Override
    public void read(ByteBufWrapper buf) throws IOException {}

    @Override
    public void handle(LunarSocket socket) {}
}
