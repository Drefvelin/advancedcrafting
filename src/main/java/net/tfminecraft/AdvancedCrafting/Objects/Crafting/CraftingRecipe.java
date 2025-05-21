package net.tfminecraft.AdvancedCrafting.Objects.Crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import me.Plugins.TLibs.Objects.API.SubAPI.StringFormatter;
import net.tfminecraft.AdvancedCrafting.Loaders.CategoryLoader;

public class CraftingRecipe {
	private String id;
	private String name;
	private String template;
	
	private String type;
	
	private HashMap<String, Integer> recipe = new HashMap<>();
	
	private List<String> ignore = new ArrayList<>();
	
	public CraftingRecipe(String key, ConfigurationSection config) {
		this.id = key;
		this.template = config.getString("template");
		this.name = StringFormatter.formatHex(config.getString("name"));
		this.type = config.getString("type");
		CategoryLoader.getByString(config.getString("category")).addRecipe(this);
		for(String r : config.getStringList("recipe")) {
			String id = r.split("\\.")[0];
			int amount = Integer.parseInt(r.split("\\.")[1]);
			recipe.put(id, amount);
		}
		if(config.contains("ignore-stats")) {
			ignore = config.getStringList("ignore-stats");
		}
	}
	
	public boolean shouldIgnore(String s) {
		if(ignore.contains(s)) return true;
		return false;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public String getTemplate() {
		return template;
	}
	
	public String getType() {
		return type;
	}

	public HashMap<String, Integer> getRecipe() {
		return recipe;
	}
}
