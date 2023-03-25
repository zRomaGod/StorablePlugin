package br.net.rankup.storable.controller;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.model.bonus.BonusModel;
import br.net.rankup.storable.model.user.UserModel;
import br.net.rankup.storable.utils.Toolchain;
import org.bukkit.entity.*;

public class BonusController
{
    private final StorablePlugin plugin;
    
    public BonusController(final StorablePlugin plugin) {
        this.plugin = plugin;
    }

    public double applyGroupBonus(final Player player, final double toApply) {
        for (final BonusModel element : this.plugin.getBonusCache().getElements()) {
            if (player.hasPermission(element.getPermission())) {
                return toApply + toApply / 100.0 * element.getBonus();
            }
        }
        return toApply;
    }

    public String getBonusMessage(final Player player) {
        for (final BonusModel element : this.plugin.getBonusCache().getElements()) {
            if (player.hasPermission(element.getPermission())) {
                return element.getFriendlyName() + " §8(" + Toolchain.formatPercentage(element.getBonus()) + "%)";
            }
        }
        return "§cNenhum";
    }

    public String getRank(final Player player) {
        for (final BonusModel element : this.plugin.getBonusCache().getElements()) {
            if (player.hasPermission(element.getPermission())) {
                return element.getFriendlyName().replace("&", "§");
            }
        }
        return "";
    }
    
    public StorablePlugin getPlugin() {
        return this.plugin;
    }
}
