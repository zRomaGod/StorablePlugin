package br.net.rankup.storable.listeners;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.model.drop.DropModel;
import br.net.rankup.storable.model.drop.DropTypeModel;
import br.net.rankup.storable.model.user.UserModel;
import br.net.rankup.storable.utils.BukkitUtils;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;

import javax.sound.midi.SysexMessage;
import java.awt.*;
import java.util.Iterator;
import java.util.UUID;

public class PlotListener implements Listener {

    @EventHandler
    public void onDrop(final PlayerDropItemEvent event) {
        final Item item = event.getItemDrop();
        if (item == null) return;
        if(event.getPlayer().isSneaking()) return;
        final String plantItem = String.valueOf(new StringBuilder().append(item.getItemStack().getTypeId()).append(":").append(item.getItemStack().getData().getData()));
        DropTypeModel dropTypeModel = StorablePlugin.getInstance().getDropTypeCache().getDrops().get(plantItem);
        if (dropTypeModel == null) {
            return;
        }
        item.setMetadata("storable_item", new FixedMetadataValue(StorablePlugin.getInstance(),"storable_item"));
    }

    @EventHandler
    public void onMerge(final ItemMergeEvent event) {
        final Item item = event.getEntity();
        if (item == null) {
            return;
        }
        final String plantItem = String.valueOf(new StringBuilder().append(item.getItemStack().getTypeId()).append(":").append(item.getItemStack().getData().getData()));
        DropTypeModel dropTypeModel = StorablePlugin.getInstance().getDropTypeCache().getDrops().get(plantItem);
        if (dropTypeModel == null) {
            return;
        }
        event.setCancelled(true);
    }


    @EventHandler
    public void onSpawn(final ItemSpawnEvent event) {
        final Item item = event.getEntity();
        double amount = item.getItemStack().getAmount();
        String plantItem = String.valueOf(new StringBuilder().append(item.getItemStack().getTypeId()).append(":").append(item.getItemStack().getData().getData()));
        Plot plot = new PlotAPI().getPlot(item.getLocation());
        DropTypeModel dropTypeModel = StorablePlugin.getInstance().getDropTypeCache().getDrops().get(plantItem);
        if(plot == null) return;

            if (item != null) {
                if (dropTypeModel != null) {
                    if (!event.getEntity().hasMetadata("storable_item")) {
                        if(plot.hasOwner()) {
                            UUID owner = plot.getOwners().stream().findFirst().get();
                            UserModel userModel = StorablePlugin.getInstance().getUserCache().getById(owner);
                            if (userModel == null) return;
                            if(this.getDropAmount(userModel) < userModel.getLimite()) {
                                userModel.addDrop(dropTypeModel, amount);
                                event.setCancelled(true);
                                event.getEntity().remove();
                            } else {
                                Player player = Bukkit.getPlayer(owner);
                                if(player != null) {
                                    event.setCancelled(true);
                                    event.getEntity().remove();
                                    BukkitUtils.sendActionBar("§cOPS! O seu armazém está lotado.", player);
                                }
                            }
                        }
                    }
                }
            }
    }

    public double getDropAmount(UserModel userModel) {
        double totalAmount = 0.0D;
        DropModel drop;
        for(Iterator var8 = userModel.getDrops().iterator(); var8.hasNext(); totalAmount += drop.getAmount()) {
            drop = (DropModel)var8.next();
        }
        return totalAmount;
    }
}
