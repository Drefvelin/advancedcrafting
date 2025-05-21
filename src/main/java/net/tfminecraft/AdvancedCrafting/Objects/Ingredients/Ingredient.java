package net.tfminecraft.AdvancedCrafting.Objects.Ingredients;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.Plugins.TLibs.TLibs;
import me.Plugins.TLibs.Enums.APIType;
import me.Plugins.TLibs.Objects.API.ItemAPI;
import me.Plugins.TLibs.Objects.API.SubAPI.StringFormatter;
import net.tfminecraft.AdvancedCrafting.AdvancedCrafting;
import net.tfminecraft.AdvancedCrafting.Objects.Data.IngredientData;
import net.tfminecraft.AdvancedCrafting.Objects.Stats.StatModifier;
import net.tfminecraft.AdvancedCrafting.Utils.StatToString;

public class Ingredient {
	private String id;
	private String path;
	
	private IngredientData data;
	
	public Ingredient(String key, ConfigurationSection config) {
		id = key;
		path = config.getString("path");
		data = new IngredientData(config);
	}

	public String getId() {
		return id;
	}

	public IngredientData getIngredientData() {
		return data;
	}
	
	public void buildTo(ItemStack i) {
		ItemMeta m = i.getItemMeta();
		NamespacedKey key = new NamespacedKey(AdvancedCrafting.plugin, "ac_ingredient_id");
		m.getPersistentDataContainer().set(key, PersistentDataType.STRING, id);
		List<String> lore = m.getLore();
		if(lore == null) {
			lore = new ArrayList<String>();
		}
		lore.add(" ");
		lore.add(StringFormatter.formatHex("#cf7c72Type: #d9bb93"+data.getType().getName()));
		lore.add(" ");
		lore.add(StringFormatter.formatHex("#c4b9a1Properties:"));
		for(StatModifier sm : data.getStatData().getModifiers()) {
			lore.add(StringFormatter.formatHex("§f- #acdb86"+StatToString.get(sm.getType())+" #e0e677+"+sm.getAmount()));
		}
		m.setLore(lore);
		i.setItemMeta(m);
	}
	
	public ItemStack build() {
		ItemAPI api = (ItemAPI) TLibs.getApiInstance(APIType.ITEM_API);
		ItemStack i = api.getCreator().getItemFromPath(path);
		buildTo(i);
		return i;
	}
}
