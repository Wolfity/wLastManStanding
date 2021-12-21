package me.wolf.wlastmanstanding.player;

import lombok.Data;
import lombok.Setter;
import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.kits.Kit;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.UUID;

@Data
public class LMSPlayer implements Comparable<LMSPlayer> {


    private final UUID uuid;
    private int kills;
    @Setter
    private boolean isSpectator;
    @Setter private Kit kit;

    public LMSPlayer(final UUID uuid) {
        this.uuid = uuid;
        this.kills = 0;
        this.isSpectator = false;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LMSPlayer lmsPlayer = (LMSPlayer) o;
        return uuid.equals(lmsPlayer.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public int compareTo(@NotNull LMSPlayer o) {
        return 0;
    }
}
