package net.berrypvp.berryPunishments.utils;

import java.util.UUID;

public class BanData {
    private final String banId;

    private final UUID playerUUID;

    private final String playerName;

    private final UUID bannedBy;

    private final String bannedByName;

    private final String reason;

    private final long bannedAt;

    private final long duration;

    private final boolean ipBan;

    private final String ip;

    public BanData(String banId, UUID playerUUID, String playerName, UUID bannedBy, String bannedByName, String reason, long bannedAt, long duration, boolean ipBan, String ip) {
        this.banId = banId;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.bannedBy = bannedBy;
        this.bannedByName = bannedByName;
        this.reason = reason;
        this.bannedAt = bannedAt;
        this.duration = duration;
        this.ipBan = ipBan;
        this.ip = ip;
    }

    public String getBanId() {
        return this.banId;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public UUID getBannedBy() {
        return this.bannedBy;
    }

    public String getBannedByName() {
        return this.bannedByName;
    }

    public String getReason() {
        return this.reason;
    }

    public long getBannedAt() {
        return this.bannedAt;
    }

    public long getDuration() {
        return this.duration;
    }

    public boolean isIpBan() {
        return this.ipBan;
    }

    public String getIp() {
        return this.ip;
    }

    public boolean isExpired() {
        if (this.duration == -1L)
            return false;
        return (System.currentTimeMillis() >= this.bannedAt + this.duration);
    }

    public long getTimeLeft() {
        if (this.duration == -1L)
            return -1L;
        long timeLeft = this.bannedAt + this.duration - System.currentTimeMillis();
        return (timeLeft > 0L) ? timeLeft : 0L;
    }
}
