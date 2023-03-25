package br.net.rankup.storable.repository;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.adapter.UserAdapter;
import br.net.rankup.storable.database.HikariDataBase;
import br.net.rankup.storable.model.user.UserModel;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsersRepository {

    private final HikariDataBase hikariDataBase = StorablePlugin.getInstance().getHikariDataBase();
    public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS storable_users (" +
            "id INTEGER NOT NULL AUTO_INCREMENT, " +
            "name CHAR(36) NOT NULL UNIQUE, " +
            "uuid CHAR(36) NOT NULL UNIQUE, " +
            "total_drops_sold DOUBLE NOT NULL, " +
            "drops TEXT NOT NULL, " +
            "limite DOUBLE NOT NULL, " +
            "PRIMARY KEY (id));";
    public static final String SELECT_QUERY = "SELECT * FROM storable_users WHERE name = ?;";
    public static final String SELECT_ALL_QUERY = "SELECT * FROM storable_users;";
    public static final String UPDATE_QUERY = "INSERT INTO storable_users " +
            "(name, uuid, total_drops_sold, drops, limite) VALUES (?, ?, ?, ?, ?) " +
            "ON DUPLICATE KEY UPDATE uuid = ?, total_drops_sold = ?, drops = ?, limite = ?;";


    public void createTable() {
        try (final Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(CREATE_TABLE)) {
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadAll() {
        try (Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_QUERY)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        StorablePlugin.getInstance().getUserCache().addElement(UserAdapter.read(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void load(Player player) {
        try (Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)) {
                statement.setString(1, String.valueOf(player.getName()));
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        StorablePlugin.getInstance().getUserCache().addElement(UserAdapter.read(resultSet));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(String name) {
        try (Connection connection = hikariDataBase.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(SELECT_QUERY)) {
                statement.setString(1, name);
                try (ResultSet resultSet = statement.executeQuery()) {
                    return resultSet.next();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void update(UserModel userModel) {
        Runnable runnable = () -> {
            try (Connection connection = hikariDataBase.getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement(UPDATE_QUERY)) {
                    UserAdapter.write(statement, userModel);
                    statement.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        };
        StorablePlugin.getInstance().getHikariDataBase().executeAsync(runnable);
    }

}
