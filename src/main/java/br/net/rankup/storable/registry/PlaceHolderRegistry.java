package br.net.rankup.storable.registry;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.hook.PlaceHolderHook;
import br.net.rankup.storable.utils.BukkitUtils;
import org.bukkit.Bukkit;

public final class PlaceHolderRegistry {

    public static void init() {
        if (!StorablePlugin.getInstance().getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")){
        	BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&cPlaceholderAPI não foi encontrado no servidor.");
            return;
        }
        BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&aPalaceholder registrado com sucesso.");
        new PlaceHolderHook(StorablePlugin.getInstance()).register();
    }

}
