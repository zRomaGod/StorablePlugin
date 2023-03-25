package br.net.rankup.storable;

import br.net.rankup.storable.cache.BonusCache;
import br.net.rankup.storable.cache.DropTypeCache;
import br.net.rankup.storable.cache.UserCache;
import br.net.rankup.storable.commands.StorableCommand;
import br.net.rankup.storable.controller.BonusController;
import br.net.rankup.storable.database.HikariDataBase;
import br.net.rankup.storable.hook.PlaceHolderHook;
import br.net.rankup.storable.model.user.UserModel;
import br.net.rankup.storable.registry.PlaceHolderRegistry;
import br.net.rankup.storable.repository.UsersRepository;
import br.net.rankup.storable.server.ServerPlugin;
import br.net.rankup.storable.tasks.StorableRunnable;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import lombok.Getter;
import lombok.Setter;
import me.saiintbrisson.bukkit.command.BukkitFrame;
import me.saiintbrisson.minecraft.command.message.MessageHolder;
import me.saiintbrisson.minecraft.command.message.MessageType;
import net.milkbowl.vault.economy.*;
import org.bukkit.plugin.*;
import org.bukkit.*;
import org.bukkit.entity.*;

@Getter
@Setter
public final class StorablePlugin extends ServerPlugin
{

    private HikariDataBase hikariDataBase;
    private UsersRepository usersRepository;
    private UserCache userCache;
    private DropTypeCache dropTypeCache;
    private BonusCache bonusCache;
    private BonusController bonusController;
    private Economy economy;
    @Getter
    private InventoryManager inventoryManager;
    @Getter
    private BukkitFrame bukkitFrame;
    
    public static StorablePlugin getInstance() {
        return (StorablePlugin)getPlugin((Class) StorablePlugin.class);
    }
    
    public void onEnable() {
        this.saveDefaultConfig();
        this.userCache = new UserCache(this);
        this.dropTypeCache = new DropTypeCache(this);
        this.bonusCache = new BonusCache(this);
        this.bonusController = new BonusController(this);

        new StorableRunnable().runTaskTimer((Plugin)this, 20L*60*30, 20L*60*30);

        HikariDataBase.prepareDatabase();

        PlaceHolderRegistry.init();

        final RegisteredServiceProvider<Economy> registration = (RegisteredServiceProvider<Economy>)this.getServer().getServicesManager().getRegistration((Class)Economy.class);
        if (registration != null) {
            this.economy = (Economy)registration.getProvider();
        }
        this.registerListeners("br.net.rankup.storable.listeners");

        this.loadCommands();
        bukkitFrame.registerCommands(new StorableCommand());
        inventoryManager = new InventoryManager(this);
        inventoryManager.invoke();
        this.usersRepository.loadAll();
    }
    
    public void onDisable() {
        for (final Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            final UserModel userModel = this.userCache.getByName(onlinePlayer.getName());
            if (userModel != null) {
                this.usersRepository.update(userModel);
            }
        }
    }

    private void loadCommands() {
        bukkitFrame = new BukkitFrame(this);
        MessageHolder messageHolder = getBukkitFrame().getMessageHolder();
        messageHolder.setMessage(MessageType.ERROR, "§cOcorreu um erro durante a execução deste comando, erro: §7{error}§c.");
        messageHolder.setMessage(MessageType.INCORRECT_USAGE, "§cUtilize: /{usage}");
        messageHolder.setMessage(MessageType.NO_PERMISSION, "§cVocê não tem permissão para executar esse comando.");
        messageHolder.setMessage(MessageType.INCORRECT_TARGET, "§cVocê não pode utilizar este comando pois ele é direcionado apenas para {target}.");
    }

    public UserCache getUserCache() {
        return this.userCache;
    }
    
    public DropTypeCache getDropTypeCache() {
        return this.dropTypeCache;
    }
    
    public BonusCache getBonusCache() {
        return this.bonusCache;
    }
    
    public BonusController getBonusController() {
        return this.bonusController;
    }
    
    public Economy getEconomy() {
        return this.economy;
    }
}
