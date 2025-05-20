package net.tfminecraft.AdvancedCrafting.Objects.Ingredients;

import org.bukkit.configuration.ConfigurationSection;

import me.Plugins.TLibs.Objects.API.SubAPI.StringFormatter;

public class IngredientType {
	private String id;
	private String name;
	
	public IngredientType(String key, ConfigurationSection config) {
		id = key;
		name = config.getString("name");
		if(name.contains("#")) {
			name = StringFormatter.formatHex(name);
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
