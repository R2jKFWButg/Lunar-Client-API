package com.ghaph;

import com.ghaph.auth.AuthWebsocket;
import com.ghaph.auth.LunarSession;
import com.ghaph.protocol.packets.client.CPacketPlayerInfoRequest;
import com.ghaph.protocol.packets.server.SPacketFriendsList;
import com.ghaph.protocol.packets.server.SPacketGiveEmotes;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.ghaph.protocol.LunarSettings;
import com.ghaph.protocol.LunarSocket;
import com.ghaph.protocol.packets.BPacketJoinServer;
import com.ghaph.protocol.user.PlayerInfo;
import com.ghaph.utils.TimeUtils;

import java.net.URISyntaxException;
import java.util.*;
import java.util.regex.Pattern;

public class LunarClient {

    public static final String version = "0.0.1";
    private static final Pattern numericIp = Pattern.compile("(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

    private final LunarSession session;
    private final LunarSettings lunarSettings = new LunarSettings();

    private String lunarAuthToken;
    private long lastAuthenticated;
    private LunarSocket lunarSocket;
    private boolean connected = false;
    private boolean connecting = false;

    private String server = "";

    // Player Info
    private final List<String> friends = new ArrayList<>();
    private final List<String> onlineFriends = new ArrayList<>();
    private final List<String> offlineFriends = new ArrayList<>();
    private final List<PlayerInfo> players = new ArrayList<>();
    private final Map<String, PlayerInfo> uuidPlayerMap = new HashMap<>();
    private final List<UUID> requestedUUIDs = new ArrayList<>();
    private boolean consoleAllowed;
    private boolean friendRequestsEnabled;
    private List<Integer> ownedEmotes = new ArrayList<>();
    private List<Integer> equippedEmotes = new ArrayList<>();

    public LunarClient(LunarSession session) {
        this.session = session;
    }

    private void connectToWebsocket() {
        final Map<String, String> headers = new HashMap<>();
        headers.put("accountType", "MICROSOFT");
        headers.put("arch", "AMD64");
        headers.put("Authorization", this.lunarAuthToken);
        headers.put("branch", "master");
        headers.put("clothCloak", "false");
        headers.put("gitCommit", "4d6392e7d63b4b883ba28f4cb7ca06387a221fe2");
        headers.put("hatHeightOffset", "[{\"id\":2424,\"height\":0.0},{\"id\":2490,\"height\":0.0},{\"id\":2491,\"height\":0.0},{\"id\":2492,\"height\":0.0},{\"id\":2493,\"height\":0.0},{\"id\":2494,\"height\":0.0}]");
        headers.put("hwid", "not supplied");
        headers.put("launcherVersion", "not supplied");
        headers.put("lunarPlusColor", "-1");
        headers.put("os", "Windows 10");
        headers.put("playerId", this.session.getUUID());
        headers.put("protocolVersion", "6");
        headers.put("server", server);
        headers.put("showHatsOverHelmet", "true");
        headers.put("showHatsOverSkinLayer", "true");
        headers.put("username", this.session.getUsername());
        headers.put("version", this.session.getVersion().code);

        try {
            this.lunarSocket = new LunarSocket(headers, lunarSettings.getWebsocketUrl(), this, (__) -> {
                connected = true;
                connecting = false;
            }, (__) -> {
                // the tokens expire after 5 minutes, so we need to reauthenticate
                if (System.currentTimeMillis() - lastAuthenticated < 300000) {
                    connectToWebsocket();
                } else {
                    this.connect();
                }
            });
            this.lunarSocket.setLunarSettings(lunarSettings);
            this.lunarSocket.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public void connect() {
        if (connected || connecting) {
            return;
        }
        connecting = true;

        // needs to attempt to update the username and uuid if only email and password are given to LunarSession
        try {
            this.session.authenticate();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        }

        final Map<String, String> headers = new HashMap<>();
        headers.put("username", session.getUsername());
        headers.put("playerId", session.getUUID());

        try {
            final AuthWebsocket authWebsocket = new AuthWebsocket(headers, this.session, (auth) -> {
                lunarAuthToken = auth;
                lastAuthenticated = System.currentTimeMillis();
                connectToWebsocket();
            });
            authWebsocket.connect();
        } catch (URISyntaxException e) {
            // will never happen
            e.printStackTrace();
        }
    }

    public void disconnect() {
        connected = false;
        connecting = false;
        if (this.lunarSocket != null) {
            this.lunarSocket.close();
        }
    }

    public boolean isConnecting() {
        return connecting;
    }

    public boolean isConnected() {
        return connected;
    }

    public LunarSocket getSocket() {
        return lunarSocket;
    }

    public LunarSettings getLunarSettings() {
        return lunarSettings;
    }

    public void clearRequestedUUIDsCache() {
        this.requestedUUIDs.clear();
    }

    public synchronized void requestUsers(List<UUID> uuids) {
        if (uuids == null || uuids.isEmpty()) {
            return;
        }
        final List<UUID> toSend = new ArrayList<>();
        for (UUID uuid : uuids) {
            if (requestedUUIDs.contains(uuid)) {
                continue;
            }

            requestedUUIDs.add(uuid);
            toSend.add(uuid);
        }

        if (toSend.size() > 0) {
            lunarSocket.sendPacket(new CPacketPlayerInfoRequest(toSend));
        }
    }

    public void setEmotes(SPacketGiveEmotes packet) {
        this.ownedEmotes = packet.getOwnedEmotes();
        this.equippedEmotes = packet.getEquippedEmotes();
    }

    public void setFriendsList(SPacketFriendsList packet) {
        this.consoleAllowed = packet.isConsoleAllowed();
        this.friendRequestsEnabled = packet.isFriendRequestsEnabled();
        this.friends.clear();
        this.onlineFriends.clear();
        this.offlineFriends.clear();

        for (String key : packet.getOnlineFriends().keySet()) {
            this.friends.add(key);
            this.onlineFriends.add(key);
            if (getLunarSettings().isDebug()) {
                System.out.println("Online: " + key);
            }
        }

        for (String key : packet.getOfflineFriends().keySet()) {
            this.friends.add(key);
            this.offlineFriends.add(key);
            if (getLunarSettings().isDebug()) {
                System.out.println("Offline: " + key);
            }
        }
    }

    public void setPlayerInfo(PlayerInfo info) {
        final String uuid = info.getUUID().toString();

        if (this.uuidPlayerMap.containsKey(uuid)) {
            final PlayerInfo old = this.uuidPlayerMap.remove(uuid);
            this.players.remove(old);
        }

        this.uuidPlayerMap.put(uuid, info);
    }

    public void setServer(String server) {
        if (!connected) {
            return;
        }

        if (numericIp.matcher(server).find()) {
            server = "_numeric_";
        }

        this.server = server;
        this.getSocket().sendPacket(new BPacketJoinServer("", server));
    }

    public List<PlayerInfo> getLunarPlayers() {
        return players;
    }

    public boolean isOnLunarClient(UUID uuid) {
        return isOnLunarClient(uuid.toString());
    }

    // Will only return true if user is on Lunar Client and you have used requestUsers() on them before
    public boolean isOnLunarClient(String uuid) {
        return this.uuidPlayerMap.containsKey(uuid);
    }

    public PlayerInfo getPlayerInfo(UUID uuid) {
        return getPlayerInfo(uuid.toString());
    }

    public PlayerInfo getPlayerInfo(String uuid) {
        return this.uuidPlayerMap.get(uuid);
    }

    public List<Integer> getOwnedEmotes() {
        return ownedEmotes;
    }

    public List<Integer> getEquippedEmotes() {
        return equippedEmotes;
    }

    public void waitForConnect() {
        while (!connected) {
            TimeUtils.sleep(100);
        }
    }

    public LunarSession getSession() {
        return session;
    }
}