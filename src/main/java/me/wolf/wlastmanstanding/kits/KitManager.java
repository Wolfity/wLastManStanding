package me.wolf.wlastmanstanding.kits;

import com.cryptomorin.xseries.XMaterial;
import lombok.Getter;
import me.wolf.wlastmanstanding.LastManStandingPlugin;
import me.wolf.wlastmanstanding.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KitManager {

    private final LastManStandingPlugin plugin;
    public KitManager(final LastManStandingPlugin plugin) {
        this.plugin = plugin;
    }

    @Getter
    private final Set<Kit> kits = new HashSet<>();

    public void loadKits() {
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
            kits.add(new Kit(kit, display, icon, materials));
        }
    }



}
