package br.net.rankup.storable.adapter;

import br.net.rankup.storable.model.bonus.BonusModel;
import org.bukkit.configuration.*;
import org.bukkit.*;

public class BonusAdapter
{
    public BonusModel read(final ConfigurationSection section) {
        final String friendlyName = this.convert(section.getString("friendly-name"));
        final String permission = section.getString("permission");
        final double value = section.getDouble("bonus-value");
        return BonusModel.builder().friendlyName(friendlyName).permission(permission).bonus(value).build();
    }
    
    private String convert(final String data) {
        return ChatColor.translateAlternateColorCodes('&', data);
    }
}
