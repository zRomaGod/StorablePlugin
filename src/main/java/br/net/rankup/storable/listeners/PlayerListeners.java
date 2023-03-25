package br.net.rankup.storable.listeners;

import br.net.rankup.logger.LogPlugin;
import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.model.user.UserModel;
import br.net.rankup.storable.utils.BukkitUtils;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListeners implements Listener
{
    private final StorablePlugin plugin;
    
    public PlayerListeners() {
        this.plugin = StorablePlugin.getInstance();
    }
    
    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UserModel userModel = StorablePlugin.getInstance().getUserCache().getByName(player.getName());
        if(userModel == null) {
            userModel = new UserModel(player.getName(), 100.0, player.getUniqueId());
            StorablePlugin.getInstance().getUserCache().addElement(userModel);
            StorablePlugin.getInstance().getUsersRepository().update(userModel);
        }
    }
    
    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        UserModel userModel = StorablePlugin.getInstance().getUserCache().getByName(event.getPlayer().getName());
        if (userModel != null) {
            StorablePlugin.getInstance().getUsersRepository().update(userModel);
        }
    }

    @EventHandler
    public void oninteractLimite(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemStack = player.getItemInHand();
        if (itemStack != null && itemStack.getType() != Material.AIR && itemStack.getItemMeta().hasDisplayName()) {
            net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
            NBTTagCompound itemCompound = nmsItem.getTag();
            if (!event.getAction().toString().contains("RIGHT")) return;
            if (!BukkitUtils.hasNBT(itemStack, "storable_limite")) return;
            if(!itemStack.getItemMeta().getDisplayName().contains("Limite de Ar")) return;
            event.setCancelled(true);
            UserModel userModel = StorablePlugin.getInstance().getUserCache().getByName(player.getName());

            if(userModel == null) {
                BukkitUtils.sendMessage(player, "&cSeu usuário está sendo carregado.");
                return;
            }
            double inHandAmount = player.getItemInHand().getAmount();

            double amount = itemCompound.getDouble("storable_limite")*inHandAmount;
            player.setItemInHand(null);
            userModel.setLimite(userModel.getLimite()+amount);
            LogPlugin.getInstance().getLogManager().registerEconomy(player, "STORABLE_LIMITE_USED", amount);
            BukkitUtils.sendMessage(player, "&aYAY! Você ativou um limite de armazém!");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 1.0f);
        }
    }

}
