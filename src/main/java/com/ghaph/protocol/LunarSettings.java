package com.ghaph.protocol;

public class LunarSettings {

    private boolean spoofTasksList;
    private boolean spoofHostsFile;
    private String websocketUrl = "wss://assetserver.lunarclientprod.com/connect";
    private boolean keepalive = true;
    private boolean debug;

    public boolean spoofTasksList() {
        return spoofTasksList;
    }

    public void setSpoofTasksList(boolean spoofTasksList) {
        this.spoofTasksList = spoofTasksList;
    }

    public boolean spoofHostsFile() {
        return spoofHostsFile;
    }

    public void setSpoofHostsFile(boolean spoofHostsFile) {
        this.spoofHostsFile = spoofHostsFile;
    }

    public String getWebsocketUrl() {
        return websocketUrl;
    }

    public void setWebsocketUrl(String websocketUrl) {
        this.websocketUrl = websocketUrl;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean doKeepAlive() {
        return keepalive;
    }

    public void setKeepAlive(boolean keepalive) {
        this.keepalive = keepalive;
    }
}
