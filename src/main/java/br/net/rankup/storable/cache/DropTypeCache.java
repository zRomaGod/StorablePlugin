package br.net.rankup.storable.cache;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.adapter.DropTypeAdapter;
import br.net.rankup.storable.model.drop.DropTypeModel;
import br.net.rankup.storable.utils.Cache;

import java.util.HashMap;

public class DropTypeCache extends Cache<DropTypeModel>
{
    private final StorablePlugin plugin;
    private HashMap<String, DropTypeModel> drops;

    public HashMap<String, DropTypeModel> getDrops() {
        return drops;
    }

    public DropTypeCache(final StorablePlugin plugin) {
        this.plugin = plugin;
        drops = new HashMap<>();
        final DropTypeAdapter adapter = new DropTypeAdapter();
        for (final String key : plugin.getConfig().getConfigurationSection("drops").getKeys(false)) {
            DropTypeModel dropTypeModel = adapter.read(key, plugin.getConfig().getConfigurationSection("drops." + key));
            this.addElement(dropTypeModel);
            drops.put(dropTypeModel.getItem(), dropTypeModel);

        }
    }
    
    public DropTypeModel getByName(final String name) {
        return this.get(dropTypeModel -> dropTypeModel.getName().toLowerCase().equals(name.toLowerCase()));
    }

    public DropTypeModel getByID(final String name) {
            DropTypeModel dropTypeModela = this.get(dropTypeModel -> dropTypeModel.getItem().equals(name));
            System.out.println(dropTypeModela.getName());
            return dropTypeModela;
    }
    
    public StorablePlugin getPlugin() {
        return this.plugin;
    }
}
