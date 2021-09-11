package me.wolf.wlastmanstanding.player;

import com.cryptomorin.xseries.XMaterial;
import lombok.Data;
import lombok.Setter;
import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.kits.Kit;
import me.wolf.wlastmanstanding.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("ConstantConditions")
@Data
public class LMSPlayer implements Comparable<LMSPlayer> {

    private final LastManStandingPlugin plugin;

    private final UUID uuid;
    private int kills;
    @Setter
    private boolean isSpectator;
    private final List<Kit> kitList;

    public LMSPlayer(final UUID uuid, final LastManStandingPlugin plugin) {
        this.plugin = plugin;
        this.uuid = uuid;
        this.kills = 0;
        this.isSpectator = false;
        this.kitList = new ArrayList<>();

        for (final String kit : plugin.getConfig().getConfigurationSection("kits").getKeys(false)) {
            final Material icon = Material.valueOf(plugin.getConfig().getString("kits." + kit + ".icon"));
            final String display = plugin.getConfig().getString(Utils.colorize("kits." + kit + ".icon-name"));
            final List<ItemStack> materials = new ArrayList<>();

            for (final String item : plugin.getConfig().getConfigurationSection("kits." + kit + ".items").getKeys(false)) {
                final int amount = plugin.getConfig().getConfigurationSection("kits." + kit + ".items." + item).getInt("amount");
                final String name = plugin.getConfig().getConfigurationSection("kits." + kit + ".items." + item).getString("name");
                materials.add(Utils.createItem
                        (XMaterial.valueOf(plugin.getConfig().getConfigurationSection("kits." + kit + ".items." + item).getString("material")).parseMaterial()
                                , name, amount));
            }
            kitList.add(new Kit(kit, display, icon, materials));
        }
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
