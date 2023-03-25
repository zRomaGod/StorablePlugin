package br.net.rankup.storable.cache;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.model.user.UserModel;
import br.net.rankup.storable.utils.Cache;
import java.util.*;

public class UserCache extends Cache<UserModel>
{
    private final StorablePlugin plugin;
    
    public UserCache(final StorablePlugin plugin) {
        this.plugin = plugin;
    }
    
    public UserModel getByName(final String name) {
        return this.get(userModel -> userModel.getName().equals(name));
    }
    public UserModel getById(final UUID uuid) {
        return this.get(userModel -> userModel.getID().equals(uuid));
    }
    
    public StorablePlugin getPlugin() {
        return this.plugin;
    }
}
