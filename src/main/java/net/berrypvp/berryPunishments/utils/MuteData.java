package net.berrypvp.berryPunishments.utils;

import java.util.UUID;

public class MuteData {
    private final String muteId;

    private final UUID playerUUID;

    private final String playerName;

    private final UUID mutedBy;

    private final String mutedByName;

    private final String reason;

    private final long mutedAt;

    private final long duration;

    public MuteData(String muteId, UUID playerUUID, String playerName, UUID mutedBy, String mutedByName, String reason, long mutedAt, long duration) {
        this.muteId = muteId;
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.mutedBy = mutedBy;
        this.mutedByName = mutedByName;
        this.reason = reason;
        this.mutedAt = mutedAt;
        this.duration = duration;
    }

    public String getMuteId() {
        return this.muteId;
    }

    public UUID getPlayerUUID() {
        return this.playerUUID;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public UUID getMutedBy() {
        return this.mutedBy;
    }

    public String getMutedByName() {
        return this.mutedByName;
    }

    public String getReason() {
        return this.reason;
    }

    public long getMutedAt() {
        return this.mutedAt;
    }

    public long getDuration() {
        return this.duration;
    }

    public boolean isExpired() {
        if (this.duration == -1L)
            return false;
        return (System.currentTimeMillis() >= this.mutedAt + this.duration);
    }

    public long getTimeLeft() {
        if (this.duration == -1L)
            return -1L;
        long timeLeft = this.mutedAt + this.duration - System.currentTimeMillis();
        return (timeLeft > 0L) ? timeLeft : 0L;
    }
}