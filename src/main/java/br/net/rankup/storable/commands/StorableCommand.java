package br.net.rankup.storable.commands;

import br.net.rankup.logger.LogPlugin;
import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.model.drop.DropTypeModel;
import br.net.rankup.storable.model.user.UserModel;
import br.net.rankup.storable.utils.*;
import br.net.rankup.storable.inventory.StorableInventory;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import me.saiintbrisson.minecraft.command.annotation.Command;
import me.saiintbrisson.minecraft.command.command.Context;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagDouble;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


public class StorableCommand {
   private final StorablePlugin plugin = StorablePlugin.getInstance();

   @Command(
      name = "armazem",
      aliases = {"drops", "storable"}
   )
   public void handleDropsCommand(Context<CommandSender> context) {
      CommandSender execution = context.getSender();
      Player player = (Player) execution;
      if(!InventoryUtils.getList().contains(player.getName())) {
         RyseInventory inventory = new StorableInventory().build();
         inventory.open(player);
         InventoryUtils.addDelay(player);
      }
      player.playSound(player.getLocation(), Sound.WOOD_CLICK, 10.0F, 10.0F);
   }

   @Command(name = "armazem.help", aliases = {"ajuda"}, permission = "commands.storable")
   public void handlerHelpCommand(Context<CommandSender> context) {
      CommandSender sender = context.getSender();
      if(sender instanceof Player) {
         BukkitUtils.sendMessage(sender, "");
         BukkitUtils.sendMessage(sender, " &a/armazem &f- &7Para o armazém.");
         if(sender.hasPermission("commands.storable")) {
            BukkitUtils.sendMessage(sender, "");
            BukkitUtils.sendMessage(sender, " &a/armazem add <player> <drop> <amount> &f- &7Enviar um drop a um jogador.");
            BukkitUtils.sendMessage(sender, " &a/armazem limite <player> <amount> &f- &7Enviar cheques de limite a um jogador.");
            BukkitUtils.sendMessage(sender, " &a/spawner help &f- &7Ver mensagem de ajuda.");
         }
         BukkitUtils.sendMessage(sender, "");
      }
   }

   @Command(name = "armazem.limite", permission = "commands.storable")
   public void handlerStackMobCommand(Context<CommandSender> context, Player target, double amount) {
      CommandSender sender = context.getSender();
      Player player = (Player) sender;

      ItemBuilder itemBuilder = new ItemBuilder(SkullCreatorUtils.itemFromUrl("3431ae7dcd1e2dd36c33a0c9a116b56e14acdaf0dafb2a04986d65aea0e35314"))
              .setName("§aLimite de Armazém")
              .addLoreLine("§7Utilize esse item para aumentar")
              .addLoreLine("§7o limite de seu armazém.")
              .addLoreLine("")
              .addLoreLine(" §fQuantia: §7{amount}".replace("{amount}", Toolchain.format(amount)))
              .addLoreLine("")
              .addLoreLine(" §aClique para ativar.");

      ItemStack itemStack = itemBuilder.build();

      net.minecraft.server.v1_8_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(itemStack);
      NBTTagCompound itemCompound = (nmsItem.hasTag()) ? nmsItem.getTag() : new NBTTagCompound();
      itemCompound.set("storable_limite", new NBTTagDouble(amount));
      nmsItem.setTag(itemCompound);
      CraftItemStack.asBukkitCopy(nmsItem);
      itemStack = (CraftItemStack.asBukkitCopy(nmsItem));

      if(sender instanceof Player) {
         LogPlugin.getInstance().getLogManager().registerEconomy(target, "STORABLE_LIMITE_"+sender.getName(), amount);
      } else {
         LogPlugin.getInstance().getLogManager().registerEconomy(target, "STORABLE_LIMITE_CONSOLE", amount);
      }

      BukkitUtils.sendMessage(player, "&eVocê enviou ao jogador &f"+target.getName()+ " &eum limite de armazém.");
      target.getInventory().addItem(itemStack);
      player.playSound(player.getLocation(), Sound.LEVEL_UP, 5.0f, 1.0f);
   }

   @Command(
      name = "armazem.add",
      aliases = {"give", "givar", "adicionar"},
      usage = "armazem add <player> <drop> <amount>",
      permission = "commands.armazem"
   )
   public void handleGiveCommand(Context<CommandSender> context, Player target, String type, double amount) {
      CommandSender execution = context.getSender();
      Player player = (Player) execution;
      UserModel userModel = this.plugin.getUserCache().getByName(player.getName());
      if (userModel == null) {
         execution.sendMessage("§cEste jogador não possui um usuário criado.");
      } else {
         DropTypeModel dropTypeModel = this.plugin.getDropTypeCache().getByName(type);
         if (dropTypeModel == null) {
            execution.sendMessage("§cEste drop não existe.");
         } else {
            userModel.addDrop(dropTypeModel, amount);
            if (execution instanceof Player) {
               execution.sendMessage("§aAdicionados " + Toolchain.format(amount) + " drops de " + type + " para " + target.getName());
            }

         }
      }
   }
}