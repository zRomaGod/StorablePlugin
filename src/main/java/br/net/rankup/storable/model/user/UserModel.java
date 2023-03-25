package br.net.rankup.storable.model.user;

import br.net.rankup.storable.model.drop.DropModel;
import br.net.rankup.storable.model.drop.DropTypeModel;
import br.net.rankup.storable.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class UserModel
{
    private final String name;
    private final UUID id;
    private double totalDropsSold;
    private double limite;
    private final List<DropModel> drops;
    
    public void addDrop(final DropTypeModel type, final double amount) {
        boolean contains = false;

        if(limite < this.getDropAmount(this)+amount) {
            Player player = Bukkit.getPlayer(name);
            if(player != null) {
                BukkitUtils.sendActionBar("§cOPS! O seu armazém está lotado.", player);
            }
            return;
        }

        for (final DropModel drop : this.drops) {
            if (drop.getType().getName().equals(type.getName())) {
                drop.setAmount(drop.getAmount() + amount);
                contains = true;
            }
        }
        if (!contains) {
            final DropModel dropModel = new DropModel(type);
            dropModel.setAmount(amount);
            this.drops.add(dropModel);
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

    public UserModel(final String name, double limite, UUID id) {
        this.drops = new ArrayList<DropModel>();
        this.name = name;
        this.limite = limite;
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    public UUID getID() {
        return this.id;
    }
    public Double getLimite() {
        return this.limite;
    }
    public double getTotalDropsSold() {
        return this.totalDropsSold;
    }
    public List<DropModel> getDrops() {
        return this.drops;
    }
    public void setLimite(final double limite) {
        this.limite = limite;
    }
    public void setTotalDropsSold(final double totalDropsSold) {
        this.totalDropsSold = totalDropsSold;
    }
}
