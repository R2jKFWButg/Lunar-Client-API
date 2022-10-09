package com.ghaph.protocol;

import com.ghaph.LunarClient;
import com.ghaph.protocol.packets.client.CPacketKeepAlive;
import com.ghaph.protocol.packets.client.CPacketPlayerInfoRequest;
import com.ghaph.protocol.packets.server.*;
import io.netty.buffer.Unpooled;
import com.ghaph.protocol.packets.BPacketJoinServer;
import com.ghaph.protocol.packets.LunarPacket;
import com.ghaph.protocol.packets.client.CPacketTasklist;
import com.ghaph.protocol.user.PlayerStatus;
import com.ghaph.utils.ByteBufWrapper;
import com.ghaph.utils.TimeUtils;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class LunarSocket extends WebSocketClient {

    private static final Map<Integer, Class<? extends LunarPacket>> PACKETS = new ConcurrentHashMap<>();
    private static final Map<Class<? extends LunarPacket>, Integer> PACKETS_REVERSE = new ConcurrentHashMap<>();

    static {
        PACKETS.put(4, SPacketFriendsList.class);
        PACKETS.put(7, SPacketBulk.class);
        PACKETS.put(6, BPacketJoinServer.class);
        PACKETS.put(8, SPacketPlayerInfo.class);
        PACKETS.put(33, SPacketCrash.class);
        PACKETS.put(35, SPacketTasklist.class);
        PACKETS.put(36, CPacketTasklist.class);
        PACKETS.put(48, CPacketPlayerInfoRequest.class);
        PACKETS.put(57, SPacketGiveEmotes.class);
        PACKETS.put(64, CPacketKeepAlive.class);

        for (Map.Entry<Integer, Class<? extends LunarPacket>> entry : PACKETS.entrySet()) {
            PACKETS_REVERSE.put(entry.getValue(), entry.getKey());
        }
    }

    private LunarSettings lunarSettings = new LunarSettings();
    private final Consumer<?> closeCallback;
    private final Consumer<?> connectCallback;
    private final LunarClient client;

    private PlayerStatus playerStatus = null;

    private boolean firstMessage = true;

    public LunarSocket(final Map<String, String> map, String url, LunarClient client, final Consumer<?> connectCallback, Consumer<?> closeCallback) throws URISyntaxException {
        super(new URI(url), new Draft_6455(), map, 0);
        this.client = client;
        this.connectCallback = connectCallback;
        this.closeCallback = closeCallback;
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        if (firstMessage) {
            firstMessage = false;
            this.connectCallback.accept(null);
        }
        final ByteBufWrapper buf = new ByteBufWrapper(Unpooled.wrappedBuffer(bytes.array()));
        final int id = buf.readVarInt();

        final Class<? extends LunarPacket> packetClass = PACKETS.get(id);
        if (packetClass == null) {
            if (this.lunarSettings.isDebug()) {
                System.err.println("Unknown packet id: " + id);
            }
            return;
        }
        if (this.lunarSettings.isDebug()) {
            System.out.println("Packet id: " + id);
        }

        try {
            final LunarPacket packet = packetClass.getConstructor().newInstance();
            packet.read(buf);
            packet.handle(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendPacket(LunarPacket packet) {
        final ByteBufWrapper buf = new ByteBufWrapper(Unpooled.buffer());
        buf.writeVarInt(PACKETS_REVERSE.get(packet.getClass()));
        try {
            packet.write(buf);
            byte[] bytes = new byte[buf.buf().readableBytes()];
            buf.buf().readBytes(bytes);
            buf.buf().release();
            this.send(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Closed: " + reason);
        this.getClient().disconnect();

        TimeUtils.sleep(1500);

        closeCallback.accept(null);
    }

    @Override
    public void connect() {
        final boolean open = this.isOpen();
        super.connect();

        // check if socket wasnt open before
        if (!open) {
            final Thread thread = new Thread(() -> {
                int tries = 0;
                while (firstMessage) {
                    TimeUtils.sleep(1000);
                    tries++;
                    if (tries > 30) {
                        return;
                    }
                }
                while (this.isOpen()) {
                    if (getLunarSettings().doKeepAlive()) {
                        this.sendPacket(new CPacketKeepAlive());
                    }
                    TimeUtils.sleep(30000);
                }
            });
            thread.setName("Keep-Alive Thread");
            thread.start();
        }
    }

    public void setLunarSettings(LunarSettings settings) {
        this.lunarSettings = settings;
    }

    public LunarSettings getLunarSettings() {
        return lunarSettings;
    }

    public LunarClient getClient() {
        return client;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {}

    @Override
    public void onMessage(String message) {}

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }
}