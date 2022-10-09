package com.ghaph.protocol.user;

import com.ghaph.utils.ByteBufWrapper;

import java.util.*;

public class PlayerInfo {

    private final UUID uuid;
    private final List<UserCosmetic> cosmetics;
    private final int color;
    private final boolean unknownA;
    private final boolean premium;
    private final boolean clothCloak;
    private final boolean showHatAboveHelmet;
    private final boolean scaleHatWithHeadwear;
    private final Map<Integer, Float> adjustableHeightCosmetics;
    private final int plusColor;
    private final boolean unknownB;
    private final boolean online;

    public PlayerInfo(ByteBufWrapper buf) {
        this.uuid = buf.readUUID();

        final int cosmeticsLength = buf.readVarInt();
        this.cosmetics = new ArrayList<>();
        if (cosmeticsLength < 65535) {
            online = true;
            for (int i = 0; i < cosmeticsLength; i++) {
                this.cosmetics.add(new UserCosmetic(buf.readVarInt(), buf.readBool()));
            }
        } else {
            online = false;
        }

        this.color = buf.readVarInt();
        this.unknownA = buf.readBool();
        this.premium = buf.readBool();
        this.clothCloak = buf.readBool();
        this.showHatAboveHelmet = buf.readBool();
        this.scaleHatWithHeadwear = buf.readBool();

        final int adjustableHeightCosmeticsLength = buf.readVarInt();
        this.adjustableHeightCosmetics = new HashMap<>();
        for (int i = 0; i < adjustableHeightCosmeticsLength; i++) {
            this.adjustableHeightCosmetics.put(buf.readVarInt(), Math.round(buf.readFloat() * 100f) / 100f);
        }

        this.plusColor = buf.readVarInt();
        this.unknownB = buf.readBool();
    }

    public PlayerInfo(UUID uuid, List<UserCosmetic> cosmetics, int color, boolean unknownA, boolean premium, boolean clothCloak,
                      boolean showHatAboveHelmet, boolean scaleHatWithHeadwear, Map<Integer, Float> adjustableHeightCosmetics,
                      int plusColor, boolean unknownB, boolean online) {
        this.uuid = uuid;
        this.cosmetics = cosmetics;
        this.color = color;
        this.unknownA = unknownA;
        this.premium = premium;
        this.clothCloak = clothCloak;
        this.showHatAboveHelmet = showHatAboveHelmet;
        this.scaleHatWithHeadwear = scaleHatWithHeadwear;
        this.adjustableHeightCosmetics = adjustableHeightCosmetics;
        this.plusColor = plusColor;
        this.unknownB = unknownB;
        this.online = online;
    }

    public UUID getUUID() {
        return uuid;
    }

    public List<UserCosmetic> getCosmetics() {
        return cosmetics;
    }

    public int getColor() {
        return color;
    }

    public boolean unknownA() {
        return unknownA;
    }

    public boolean isPremium() {
        return premium;
    }

    public boolean hasClothCloak() {
        return clothCloak;
    }

    public boolean showHatAboveHelmet() {
        return showHatAboveHelmet;
    }

    public boolean scaleHatWithHeadwear() {
        return scaleHatWithHeadwear;
    }

    public Map<Integer, Float> getAdjustableHeightCosmetics() {
        return adjustableHeightCosmetics;
    }

    public int getPlusColor() {
        return plusColor;
    }

    public boolean unknownB() {
        return unknownB;
    }

    @Override
    public String toString() {
        return "PlayerInfo{uuid=" + uuid + ", cosmetics=" + cosmetics.size() + ", color=" + color + ", unknownA=" + unknownA + ", premium=" + premium + ", clothCloak=" + clothCloak + ", showHatAboveHelmet=" + showHatAboveHelmet + ", scaleHatWithHeadwear=" + scaleHatWithHeadwear + ", adjustableHeightCosmetics=" + adjustableHeightCosmetics + ", plusColor=" + plusColor + ", unknownB=" + unknownB + "}";
    }
}
