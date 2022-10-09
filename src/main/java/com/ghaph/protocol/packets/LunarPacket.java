package com.ghaph.protocol.packets;

import com.ghaph.protocol.LunarSocket;
import com.ghaph.utils.ByteBufWrapper;

import java.io.IOException;

public abstract class LunarPacket {

    public abstract void write(ByteBufWrapper buf);

    public abstract void read(ByteBufWrapper buf) throws IOException;

    public abstract void handle(LunarSocket socket);


}
