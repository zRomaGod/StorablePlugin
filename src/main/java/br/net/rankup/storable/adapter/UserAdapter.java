package br.net.rankup.storable.adapter;

import java.lang.reflect.*;

import br.net.rankup.storable.model.drop.DropModel;
import br.net.rankup.storable.model.user.UserModel;
import br.net.rankup.storable.utils.DropsSerializer;
import com.google.gson.reflect.*;
import java.util.*;
import java.sql.*;

public class UserAdapter
{
    private final Type type;
    
    public UserAdapter() {
        this.type = new TypeToken<List<DropModel>>() {}.getType();
    }
    
    public static UserModel read(final ResultSet resultSet) throws SQLException {
        final UserModel userModel = new UserModel(resultSet.getString("name"), resultSet.getDouble("limite")
                , UUID.fromString(resultSet.getString("uuid")));
        userModel.setTotalDropsSold(resultSet.getDouble("total_drops_sold"));
        userModel.getDrops().addAll(DropsSerializer.deserialize(resultSet.getString("drops")));
        return userModel;
    }
    
    public static void write(final PreparedStatement statement, final UserModel model) throws SQLException {
        statement.setString(1, model.getName());

        statement.setString(2, model.getID().toString());
        statement.setDouble(3, model.getTotalDropsSold());
        statement.setString(4, DropsSerializer.serialize(model));
        statement.setDouble(5, model.getLimite());

        statement.setString(6, model.getID().toString());
        statement.setDouble(7, model.getTotalDropsSold());
        statement.setString(8, DropsSerializer.serialize(model));
        statement.setDouble(9, model.getLimite());
    }
}
