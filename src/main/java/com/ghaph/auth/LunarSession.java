package com.ghaph.auth;

import com.mojang.authlib.Agent;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import com.ghaph.protocol.user.ClientVersion;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LunarSession {

    public static final YggdrasilAuthenticationService authService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, "");

    private String email;
    private String password;
    private String sessionId;
    private String uuid;
    private String username;
    private final ClientVersion version;

    private YggdrasilUserAuthentication auth;

    public LunarSession(String sessionId, UUID uuid, String username, ClientVersion version) {
        this(sessionId, uuid.toString(), username, version);
    }

    public LunarSession(String sessionId, String uuid, String username, ClientVersion version) {
        if (sessionId.contains(":")) {
            sessionId = sessionId.split(":")[1];
        }
        if (!uuid.contains("-")) {
            uuid = uuid.replaceFirst("(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)", "$1-$2-$3-$4-$5");
        }

        this.sessionId = sessionId;
        this.uuid = uuid;
        this.username = username;
        this.version = version;
    }

    public LunarSession(String email, String password, ClientVersion version) {
        this.version = version;
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUUID() {
        return uuid;
    }

    public String getUsername() {
        return username;
    }

    public ClientVersion getVersion() {
        return version;
    }


    public YggdrasilUserAuthentication authenticate() throws AuthenticationException {
        if (auth != null) {
            return auth;
        }

        final YggdrasilUserAuthentication auth = (YggdrasilUserAuthentication) authService.createUserAuthentication(Agent.MINECRAFT);

        if (sessionId != null) {
            final Map<String, Object> storage = new HashMap<>();
            storage.put("accessToken", sessionId);
            storage.put("displayName", getUsername());
            storage.put("uuid", getUUID());

            auth.loadFromStorage(storage);
        } else {
            auth.setUsername(getEmail());
            auth.setPassword(getPassword());
            auth.logIn();
            this.username = auth.getSelectedProfile().getName();
            this.uuid = auth.getSelectedProfile().getId().toString();
        }

        return auth;
    }
}