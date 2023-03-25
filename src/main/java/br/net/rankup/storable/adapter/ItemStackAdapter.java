package br.net.rankup.storable.adapter;

import br.net.rankup.storable.utils.ItemBuilder;
import br.net.rankup.storable.utils.SkullCreator;
import org.bukkit.configuration.*;
import org.bukkit.inventory.*;
import java.util.*;
import java.util.stream.*;
import org.bukkit.*;

public class ItemStackAdapter
{
    public ItemStack read(final ConfigurationSection section) {
        final String[] strippedMaterial = section.getString("id").split(":");
        if (strippedMaterial.length != 2) {
            return null;
        }
        final String rawMaterial = strippedMaterial[0];
        Material material;
        try {
            material = Material.getMaterial(Integer.parseInt(rawMaterial));
        }
        catch (NumberFormatException e) {
            material = Material.valueOf(rawMaterial);
        }
        final short data = Short.parseShort(strippedMaterial[1]);
        final String headTexture = section.getString("head-texture");
        final String displayName = this.convert(section.getString("display-name"));
        final List<String> lore = (List<String>)section.getStringList("lore").stream().map(this::convert).collect(Collectors.toList());
        if (headTexture.isEmpty()) {
            return new ItemBuilder(material, 1, data).setName(displayName).setLore(lore).build();
        }
        return new ItemBuilder(SkullCreator.itemFromUrl(headTexture)).setName(displayName).setLore(lore).build();
    }
    
    private String convert(final String data) {
        return ChatColor.translateAlternateColorCodes('&', data);
    }
}
