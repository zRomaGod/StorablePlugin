package br.net.rankup.storable.server;

import br.net.rankup.storable.utils.ClassCollector;
import org.bukkit.plugin.java.*;
import org.bukkit.event.*;
import org.bukkit.plugin.*;

import java.lang.reflect.*;

public abstract class ServerPlugin extends JavaPlugin
{
    protected final void registerListeners(final Listener... listeners) {
        final PluginManager manager = this.getServer().getPluginManager();
        for (final Listener listener : listeners) {
            manager.registerEvents(listener, (Plugin)this);
        }
    }
    
    protected final void registerListeners(final String packageName) {
        try {
            final ClassCollector<Listener> collector = new ClassCollector<Listener>(this.getClass(), Listener.class).filterByPackage(packageName).selectInterfaces(false);
            for (final Class<Listener> clazz : collector.collect()) {
                try {
                    final Constructor<Listener> constructor = clazz.getConstructor(this.getClass());
                    this.registerListeners(constructor.newInstance(this));
                }
                catch (NoSuchMethodException e) {
                    this.registerListeners(clazz.newInstance());
                }
            }
        }
        catch (Throwable $ex) {
        }
    }
}
