package br.net.rankup.storable.database;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.repository.UsersRepository;
import br.net.rankup.storable.utils.BukkitUtils;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.bukkit.Bukkit;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HikariDataBase {

    @Getter
    public HikariDataSource dataSource;

    public HikariDataBase(String ip, String database, String user, String password) throws Exception {
        openConnection(ip, database, user, password);
    }

    private void openConnection(String ip, String database, String user, String password) throws Exception {
        if (dataSource != null) return;

        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setUsername(user);
            hikariConfig.setPassword(password);
            hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
            hikariConfig.setJdbcUrl(String.format("jdbc:mariadb://%s/%s", ip, database));

            dataSource = new HikariDataSource(hikariConfig);

            Logger.getLogger("com.zaxxer.hikari").setLevel(Level.OFF);
        } catch (Exception e) {
            throw new Exception("Não foi possivel iniciar a conexão com banco de dados MySQL Hikari.", e);
        }
    }

    public void executeAsync(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }

    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException exception) {
            return null;
        }
    }


    public static void prepareDatabase() {
        try {
            String ip = StorablePlugin.getInstance().getConfig().getString("Database.IP");
            String database = StorablePlugin.getInstance().getConfig().getString("Database.Database");
            String user = StorablePlugin.getInstance().getConfig().getString("Database.User");
            String password = StorablePlugin.getInstance().getConfig().getString("Database.Password");

            StorablePlugin.getInstance().setHikariDataBase(new HikariDataBase(ip, database, user, password));

            StorablePlugin.getInstance().setUsersRepository(new UsersRepository());
            StorablePlugin.getInstance().getUsersRepository().createTable();

            BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&aConexão com o banco de dados estabelecida com sucesso.");
        } catch (Exception e) {
            BukkitUtils.sendMessage(Bukkit.getConsoleSender(), "&cOcorreu um erro ao inicializar a conexão com o banco de dados.");
            e.printStackTrace();
            Bukkit.getServer().shutdown();
        }
    }

}