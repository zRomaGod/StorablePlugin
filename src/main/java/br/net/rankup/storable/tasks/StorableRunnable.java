package br.net.rankup.storable.tasks;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.model.user.UserModel;
import org.bukkit.scheduler.BukkitRunnable;
import java.util.Iterator;

public class StorableRunnable extends BukkitRunnable {
    @Override
    public void run() {
        for(Iterator iterator = StorablePlugin.getInstance().getUserCache().iterator(); iterator.hasNext();) {
            final UserModel userModel = (UserModel) iterator.next();
            if(userModel == null) return;
            StorablePlugin.getInstance().getUsersRepository().update(userModel);
        }
    }
}
