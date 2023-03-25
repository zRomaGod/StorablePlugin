package br.net.rankup.storable.adapter;

import br.net.rankup.storable.model.drop.DropTypeModel;
import org.bukkit.configuration.*;
import org.bukkit.inventory.*;

public class DropTypeAdapter
{
    private final ItemStackAdapter adapter;
    
    public DropTypeAdapter() {
        this.adapter = new ItemStackAdapter();
    }
    
    public DropTypeModel read(final String key, final ConfigurationSection section) {
        final double price = section.getDouble("price");
        final ItemStack icon = this.adapter.read(section.getConfigurationSection("icon"));
        final String item = section.getString("item");
        return DropTypeModel.builder().name(key).item(item).price(price).icon(icon).build();
    }
}
