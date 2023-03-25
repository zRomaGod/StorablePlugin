package br.net.rankup.storable.cache;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.adapter.BonusAdapter;
import br.net.rankup.storable.model.bonus.BonusModel;
import br.net.rankup.storable.utils.Cache;

public class BonusCache extends Cache<BonusModel>
{
    private final StorablePlugin plugin;
    
    public BonusCache(final StorablePlugin plugin) {
        this.plugin = plugin;
        final BonusAdapter adapter = new BonusAdapter();
        for (final String key : plugin.getConfig().getConfigurationSection("bonus").getKeys(false)) {
            this.addElement(adapter.read(plugin.getConfig().getConfigurationSection("bonus." + key)));
        }
    }
    
    public BonusModel getByPermission(final String permission) {
        return this.get(bonusModel -> bonusModel.getPermission().equals(permission));
    }
    
    public StorablePlugin getPlugin() {
        return this.plugin;
    }
}
