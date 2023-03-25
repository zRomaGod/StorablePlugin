package br.net.rankup.storable.hook;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.model.drop.DropModel;
import br.net.rankup.storable.model.user.UserModel;
import br.net.rankup.storable.utils.Format;
import br.net.rankup.storable.utils.Toolchain;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.*;

import java.text.DecimalFormat;
import java.util.Iterator;

public class PlaceHolderHook extends PlaceholderExpansion {

    private static StorablePlugin plugin;

    public PlaceHolderHook(StorablePlugin plugin) {
        this.plugin = plugin;
    }

    public String getName() {
        return this.plugin.getName();
    }

    public String getIdentifier() {
        return "storable";
    }

    public String getAuthor() {
        return "zRomaGod_";
    }

    public String getVersion() {
        return this.plugin.getDescription().getVersion();
    }

    DecimalFormat decimalFormat = new DecimalFormat("#.#");
    @Override
    public String onPlaceholderRequest(Player player, String identifier) {

        if (identifier.equalsIgnoreCase("limite")) {
            UserModel userModel = StorablePlugin.getInstance().getUserCache().getByName(player.getName());
            if (player == null) {
                return "§7Carregando...";
            }
            if (userModel == null) {
                return "§7Carregando...";
            }
            return Format.format(userModel.getLimite());
        }
        if (identifier.equalsIgnoreCase("amount")) {
            UserModel userModel = StorablePlugin.getInstance().getUserCache().getByName(player.getName());
            if (player == null) {
                return "§7Carregando...";
            }
            if (userModel == null) {
                return "§7Carregando...";
            }
            return Format.format(getDropAmount(userModel));
        }


        if (identifier.equalsIgnoreCase("progress")) {
            UserModel userModel = StorablePlugin.getInstance().getUserCache().getByName(player.getName());
            if (player == null) {
                return "§7Carregando...";
            }
            if(userModel == null) {
                return "§7Carregando...";
            }
            if(this.percentage(this.getDropAmount(userModel)
                    , userModel.getLimite()) >= 100) {
                return "§cArmazém lotado!";
            }
            return this.progressBar(this.getDropAmount(userModel), userModel.getLimite())
                    + " §7" + this.decimalFormat.format(this.percentage(this.getDropAmount(userModel)
                    , userModel.getLimite()))+"%";

        }
        return "Placeholder inválida";
    }


    public double getDropAmount(UserModel userModel) {
        double totalAmount = 0.0D;
        DropModel drop;
        for(Iterator var8 = userModel.getDrops().iterator(); var8.hasNext(); totalAmount += drop.getAmount()) {
            drop = (DropModel)var8.next();
        }
        return totalAmount;
    }

    private double percentage(double amount, double amountFull) {
        if(amount == 0 && amountFull == 0) return 0;
        return Math.min(amount * 100.0 / amountFull, 100.0);
    }

    private String progressBar(double amount, double amountFull) {
        final StringBuilder bar = new StringBuilder("§a");
        final int divide = (int)(this.percentage(amount, amountFull) / 10.0);
        for (int i = 0; i < 10; ++i) {
            bar.append((i == divide) ? "§8" : "").append("▎");
        }
        return bar.toString();
    }
}
