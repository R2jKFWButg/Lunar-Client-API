package com.ghaph.auth;

import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import javax.crypto.*;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.function.Consumer;

public class AuthWebsocket extends WebSocketClient {

    private final LunarSession session;
    private final Consumer<String> callback;

    public AuthWebsocket(final Map<String, String> map, LunarSession session, Consumer<String> callback) throws URISyntaxException {
        super(new URI("wss://authenticator.lunarclientprod.com"), new Draft_6455(), map, 5000);
        this.callback = callback;
        this.session = session;
    }

    @Override
    public void onMessage(ByteBuffer bytes) {
        final JSONObject object = new JSONObject(new String(bytes.array()));
        switch (object.getString("packetType")) {
            case "SPacketEncryptionRequest":
                handleEncryptionPacket(object);
                break;
            case "SPacketAuthenticatedRequest":
                this.close();
                this.callback.accept(object.getString("jwtKey"));
                break;
            default:
                System.err.println("Unknown packet type: " + object.getString("packetType"));
                break;
        }
    }

    private void handleEncryptionPacket(JSONObject packet) {
        final PublicKey publicKey = getPublicKey(Base64.getUrlDecoder().decode(packet.getString("publicKey")));
        final SecretKey secretKey = newSharedKey();
        byte[] byArray = publicKey == null ? new byte[] {} : getServerIdHash(publicKey, secretKey);
        if (byArray == null) {
            return;
        }

        String serverId = new BigInteger(byArray).toString(16);
        try {
            final YggdrasilUserAuthentication user = this.session.authenticate();
            LunarSession.authService.createMinecraftSessionService().joinServer(user.getSelectedProfile(), user.getAuthenticatedToken(), serverId);
        } catch (Exception e){
            e.printStackTrace();
        }

        final JSONObject response = new JSONObject();
        response.put("secretKey", new String(Base64.getUrlEncoder().encode(encryptData(publicKey, secretKey.getEncoded()))));
        response.put("publicKey", new String(Base64.getUrlEncoder().encode(
                encryptData(publicKey, Base64.getUrlDecoder().decode(packet.getString("randomBytes")))
        )));
        this.sendPacket("CPacketEncryptionResponse", response);
    }

    private void sendPacket(String name, JSONObject object) {
        if (!this.isOpen() || object == null) {
            return;
        }
        object.put("packetType", name);
        try {
            this.send(object.toString().getBytes(StandardCharsets.UTF_8));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    private byte[] getServerIdHash(PublicKey publicKey, SecretKey secretKey) {
        try {
            return digestOperation("SHA-1", "".getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private static byte[] encryptData(Key key, byte[] data) {
        try {
            Cipher cipher = Cipher.getInstance(key.getAlgorithm());
            cipher.init(1, key);
            return cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    private static byte[] digestOperation(String algorithm, byte[]... data) {
        try {
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            for (byte[] abyte : data) {
                digest.update(abyte);
            }

            return digest.digest();
        } catch (NoSuchAlgorithmException nosuchalgorithmexception) {
            nosuchalgorithmexception.printStackTrace();
            return null;
        }
    }

    private static SecretKey newSharedKey() {
        try {
            KeyGenerator keygenerator = KeyGenerator.getInstance("AES");
            keygenerator.init(128);
            return keygenerator.generateKey();
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
    }

    private PublicKey getPublicKey(byte[] key) {
        try {
            return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(key));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {}

    @Override
    public void onMessage(String message) {}

    @Override
    public void onClose(int code, String reason, boolean remote) {
        if (reason != null && reason.length() > 0) {
            System.out.println(reason);
        }
    }
}