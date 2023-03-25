package br.net.rankup.storable.utils;

import br.net.rankup.storable.StorablePlugin;
import br.net.rankup.storable.model.drop.DropModel;
import br.net.rankup.storable.model.drop.DropTypeModel;
import br.net.rankup.storable.model.user.UserModel;
import java.util.*;

public class DropsSerializer
{
    private static final StorablePlugin PLUGIN;
    
    public static String serialize(final UserModel userModel) {
        final StringJoiner joiner = new StringJoiner(",");
        for (final DropModel drop : userModel.getDrops()) {
            joiner.add(drop.getType().getName() + ":" + drop.getAmount());
        }
        return joiner.toString();
    }
    
    public static List<DropModel> deserialize(final String data) {
        final List<DropModel> list = new ArrayList<DropModel>();
        if (data.contains(",")) {
            for (final String value : data.split(":")) {
                formatAndPut(list, value);
            }
        }
        else {
            formatAndPut(list, data);
        }
        return list;
    }
    
    private static void formatAndPut(final List<DropModel> list, final String content) {
        if (!content.contains(":")) {
            return;
        }
        final String[] split = content.split(":");
        final String name = split[0];
        final double amount = Double.parseDouble(split[1]);
        final DropTypeModel typeModel = DropsSerializer.PLUGIN.getDropTypeCache().getByName(name);
        if (typeModel != null) {
            final DropModel dropModel = new DropModel(typeModel);
            dropModel.setAmount(amount);
            list.add(dropModel);
        }
    }
    
    static {
        PLUGIN = StorablePlugin.getInstance();
    }
}
