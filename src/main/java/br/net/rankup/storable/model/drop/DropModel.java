package br.net.rankup.storable.model.drop;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.model.user.UserModel;
import org.bukkit.entity.*;

public class DropModel
{
    private final StorablePlugin plugin;
    private final DropTypeModel type;
    private double amount;
    
    public double getTotalValue(final Player player) {
        final UserModel userModel = this.plugin.getUserCache().getByName(player.getName());
        if (userModel == null) {
            return 0.0;
        }
        double totalPrice = this.plugin.getBonusController().applyGroupBonus(player, this.type.getPrice() * this.amount);
        return totalPrice;
    }
    
    public double getPrice(final Player player) {
        final UserModel userModel = this.plugin.getUserCache().getByName(player.getName());
        if (userModel == null) {
            return 0.0;
        }
        double totalPrice = this.plugin.getBonusController().applyGroupBonus(player, this.type.getPrice());
        return totalPrice;
    }
    
    public double sell(final Player player) {
        final UserModel userModel = this.plugin.getUserCache().getByName(player.getName());
        if (userModel == null) {
            return 0.0;
        }
        double totalPrice = this.plugin.getBonusController().applyGroupBonus(player, this.type.getPrice() * this.amount);
        userModel.setTotalDropsSold(userModel.getTotalDropsSold() + this.amount);
        return totalPrice;
    }
    
    public DropModel(final DropTypeModel type) {
        this.plugin = StorablePlugin.getInstance();
        this.type = type;
    }
    
    public StorablePlugin getPlugin() {
        return this.plugin;
    }
    
    public DropTypeModel getType() {
        return this.type;
    }
    
    public double getAmount() {
        return this.amount;
    }
    
    public void setAmount(final double amount) {
        this.amount = amount;
    }
}
