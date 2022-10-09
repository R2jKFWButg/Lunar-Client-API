package com.ghaph.protocol.packets.client;

import com.ghaph.protocol.LunarSocket;
import com.ghaph.protocol.packets.LunarPacket;
import com.ghaph.utils.ByteBufWrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class CPacketKeepAlive extends LunarPacket {

    private final Map<String, Boolean> mods;
    private final String game;

    public CPacketKeepAlive() {
        this(new HashMap<>(), "");

        final ThreadLocalRandom random = ThreadLocalRandom.current();
        this.mods.put("scrollable_tooltips", random.nextBoolean());
        this.mods.put("tntCountdown", random.nextBoolean());
        this.mods.put("keystrokes", random.nextBoolean());
        this.mods.put("cooldowns", random.nextBoolean());
        this.mods.put("glint_colorizer", random.nextBoolean());
        this.mods.put("armorstatus", random.nextBoolean());
        this.mods.put("chunkborders", random.nextBoolean());
        this.mods.put("textHotKey", random.nextBoolean());
        this.mods.put("resource_display", random.nextBoolean());
        this.mods.put("nametag", random.nextBoolean());
        this.mods.put("skinLayers3D", random.nextBoolean());
        this.mods.put("items2d", random.nextBoolean());
        this.mods.put("bossbar", random.nextBoolean());
        this.mods.put("tab", random.nextBoolean());
        this.mods.put("hypixel_bedwars", random.nextBoolean());
        this.mods.put("hypixel_mod", random.nextBoolean());
        this.mods.put("uhc_overlay", random.nextBoolean());
        this.mods.put("colorsaturation", random.nextBoolean());
        this.mods.put("teamview", random.nextBoolean());
        this.mods.put("scoreboard", random.nextBoolean());
        this.mods.put("pack_organizer", random.nextBoolean());
        this.mods.put("saturation_mod", random.nextBoolean());
        this.mods.put("snaplook", random.nextBoolean());
        this.mods.put("'mumble-link'", random.nextBoolean());
        this.mods.put("momentum_mod", random.nextBoolean());
        this.mods.put("skyblockAddons", random.nextBoolean());
        this.mods.put("itemTrackerChild", random.nextBoolean());
        this.mods.put("cps", random.nextBoolean());
        this.mods.put("combo", random.nextBoolean());
        this.mods.put("clock", random.nextBoolean());
        this.mods.put("lighting", random.nextBoolean());
        this.mods.put("hitbox", random.nextBoolean());
        this.mods.put("hitColor", random.nextBoolean());
        this.mods.put("one_seven_visuals", random.nextBoolean());
        this.mods.put(" menu_blur", random.nextBoolean());
        this.mods.put("quickplay", random.nextBoolean());
        this.mods.put("potioneffects", random.nextBoolean());
        this.mods.put("fov_mod", random.nextBoolean());
        this.mods.put("motionBlur", random.nextBoolean());
        this.mods.put("daycounter", random.nextBoolean());
        this.mods.put("memory", random.nextBoolean());
        this.mods.put(" ping", random.nextBoolean());
        this.mods.put("nickHider", random.nextBoolean());
        this.mods.put("range", random.nextBoolean());
        this.mods.put("screenshot", random.nextBoolean());
        this.mods.put("waypoints", random.nextBoolean());
        this.mods.put("directionhud", random.nextBoolean());
        this.mods.put("freelook", random.nextBoolean());
        this.mods.put("potion_counter", random.nextBoolean());
        this.mods.put("coords", random.nextBoolean());
        this.mods.put(" sound_changer", random.nextBoolean());
        this.mods.put("'account-name'", random.nextBoolean());
        this.mods.put("fps", random.nextBoolean());
        this.mods.put("zoom", random.nextBoolean());
        this.mods.put("itemPhysic", random.nextBoolean());
        this.mods.put("worldedit_cui", random.nextBoolean());
        this.mods.put("weather_changer", random.nextBoolean());
        this.mods.put("crosshair", random.nextBoolean());
        this.mods.put("block_outline", random.nextBoolean());
        this.mods.put("chat", random.nextBoolean());
        this.mods.put("shinyPots", random.nextBoolean());
        this.mods.put("toggleSneak", random.nextBoolean());
        this.mods.put("time_changer", random.nextBoolean());
        this.mods.put("particleMod", random.nextBoolean());
        this.mods.put("stopwatch", random.nextBoolean());
        this.mods.put("serverAddressMod", random.nextBoolean());
    }

    public CPacketKeepAlive(Map<String, Boolean> mods, String game) {
        this.mods = mods;
        this.game = game;
    }

    @Override
    public void write(ByteBufWrapper buf) {
        buf.writeVarInt(mods.size());
        mods.forEach((s, bool) -> {
            buf.writeString(s);
            buf.buf().writeBoolean(bool);
        });
        buf.writeString(game);
    }

    @Override
    public void read(ByteBufWrapper wrapper) {}

    @Override
    public void handle(LunarSocket websocket) {

    }
}
