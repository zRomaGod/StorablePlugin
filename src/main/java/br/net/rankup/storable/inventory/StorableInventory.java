package br.net.rankup.storable.inventory;

import br.net.rankup.logger.LogPlugin;
import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.model.drop.DropModel;
import br.net.rankup.storable.model.drop.DropTypeModel;
import br.net.rankup.storable.model.user.UserModel;
import br.net.rankup.storable.utils.InventoryUtils;
import br.net.rankup.storable.utils.ItemBuilder;
import br.net.rankup.storable.utils.SkullCreator;
import br.net.rankup.storable.utils.Toolchain;
import com.google.common.collect.ImmutableList;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class StorableInventory implements InventoryProvider {

    private final int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24};

    public RyseInventory build() {
        return RyseInventory.builder()
                .title("Armazém".replace("&", "§"))
                .rows(4)
                .provider(this)
                .disableUpdateTask()
                .build(StorablePlugin.getInstance());
    }

    @Override
    public void init(Player player, InventoryContents contents) {
        final UserModel userModel = StorablePlugin.getInstance().getUserCache().getByName(player.getName());
        if(userModel != null) {
            if (userModel.getDrops().isEmpty()) {
                ItemStack empty = new ItemBuilder(Material.WEB, 1, 0)
                        .owner(player.getName()).setName("§cVazio").setLore(ImmutableList.of(
                                "§7Você não possui nenhum.", "§7drop em seu armazém."
                        )).build();
                contents.set(13, empty);
            }
            int slot = 0;

            for(Iterator var5 = userModel.getDrops().iterator(); var5.hasNext(); ++slot) {
                final DropModel drop = (DropModel)var5.next();
                ItemStack itemStack = drop.getType().getIcon().clone();
                ItemMeta itemMeta = itemStack.getItemMeta();
                List<String> lore = new ArrayList();
                Iterator var10 = itemMeta.getLore().iterator();

                while(var10.hasNext()) {
                    String value = (String)var10.next();
                    lore.add(value.replace("<drop-amount>", Toolchain.format(drop.getAmount()))
                            .replace("<drop-price>", Toolchain.format(drop.getPrice(player)))
                            .replace("<total-price>", Toolchain.format(drop.getTotalValue(player))));
                }


                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);

                IntelligentItem intelligentItem = IntelligentItem.of(itemStack, event -> {
                    event.setCancelled(true);
                    if(event.isLeftClick()) {
                        double value = drop.sell(player);
                        LogPlugin.getInstance().getLogManager().registerStorable(player, 1, value);
                        player.sendMessage("§aYAY! Você vendeu §f" + Toolchain.format(drop.getAmount()) + " §ade drops por §2$§f" + Toolchain.format(value) + " §acoins.");
                        StorablePlugin.getInstance().getEconomy().depositPlayer(player, value);
                        StorablePlugin.getInstance().getUsersRepository().update(userModel);
                        userModel.getDrops().remove(drop);
                    }
                    if(event.isRightClick()) {
                        double amount = drop.getAmount();
                        if(amount <= 0) {
                            player.closeInventory();
                            player.sendMessage("§cVocê não tem drops suficientes para recolher!");
                            return;
                        }
                        drop.setAmount(amount-1);
                        if(drop.getAmount() <= 0) {
                            userModel.getDrops().remove(drop);
                        }
                        DropTypeModel dropTypeModel = StorablePlugin.getInstance().getDropTypeCache().getDrops().get(drop.getType().getItem());
                        ItemStack itemStack1 = new ItemStack(dropTypeModel.getIcon().getType());
                        player.getInventory().addItem(itemStack1);
                        player.sendMessage("§aYAY! Você recolheu §f1x drop(s) §ado seu armazém.");
                    }
                    this.reOpenInventory(player);
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 10.0f, 10f);
                });
                contents.set(this.slots[slot], intelligentItem);

                 }
            ItemBuilder itemBuilder =
                    new ItemBuilder(Material.HOPPER)
                            .setName("§eVender tudo")
                            .lore(new String[]{"§7Utilize essa função para", "§7vender todos os drops."
                                    , "",
                                    "§fMultiplicador: " + StorablePlugin.getInstance().getBonusController().getBonusMessage(player)
                                    , "§fDrops vendidos: §7" + Toolchain.format(userModel.getTotalDropsSold()) + " drops"
                            , "", "§aClique parar vender tudo"});

            IntelligentItem intelligentSellAll = IntelligentItem.of(itemBuilder.build(), event -> {
                event.setCancelled(true);

                if (!player.hasPermission("storable.sellall")) {
                    player.sendMessage("§cEsta opção é exclusiva §aVIP §cou superior.");
                } else if (userModel.getDrops().isEmpty()) {
                    player.sendMessage("§cVocê não tem drops para vender.");
                } else {
                    double totalValue = 0.0D;
                    double totalAmount = 0.0D;

                    DropModel drop;
                    for(Iterator var8 = userModel.getDrops().iterator(); var8.hasNext(); totalAmount += drop.getAmount()) {
                        drop = (DropModel)var8.next();
                        totalValue += drop.sell(player);
                    }

                    LogPlugin.getInstance().getLogManager().registerStorable(player, totalValue, totalValue);
                    player.sendMessage("§aYAY! Você vendeu §f" + Toolchain.format(totalAmount) + " §ade drops por §2$§f" + Toolchain.format(totalValue) + " §acoins.");
                    StorablePlugin.getInstance().getEconomy().depositPlayer(player, totalValue);
                    StorablePlugin.getInstance().getUsersRepository().update(userModel);
                    userModel.getDrops().clear();
                }

                player.closeInventory();
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 10.0f, 10f);
            });

            contents.set(32, intelligentSellAll);
            }


        ItemBuilder itemBuilder = new ItemBuilder(Material.getMaterial(342))
                .setName("§eInformações")
                .addLoreLine("§7Para você evoluir seu")
                .addLoreLine("§7limite de armazém precisa")
                .addLoreLine("§7usar o 'Limite de Armazém'")
                .addLoreLine(" ")
                .addLoreLine(" §fSeu limite: §7"+Toolchain.format(userModel.getLimite()));


        contents.set(30, itemBuilder.build());
        }


        public void reOpenInventory(Player player) {
                RyseInventory inventory = new StorableInventory().build();
                inventory.open(player);
        }

    }
