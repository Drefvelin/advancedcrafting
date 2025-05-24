package net.tfminecraft.AdvancedCrafting.Objects.Crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import me.Plugins.TLibs.Objects.API.SubAPI.StringFormatter;
import net.tfminecraft.AdvancedCrafting.Loaders.CategoryLoader;
import net.tfminecraft.AdvancedCrafting.Objects.Stats.StatModifier;

public class CraftingRecipe {
	private String id;
	private String name;
	private String template;
	
	private String type;
	
	private HashMap<String, Integer> recipe = new HashMap<>();
	
	private List<String> ignore = new ArrayList<>();

	private List<StatModifier> modify = new ArrayList<>();
	private List<StatModifier> base = new ArrayList<>();
	
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
		if(config.contains("modify-stats")) {
			for(String s : config.getStringList("modify-stats")) {
				modify.add(new StatModifier(s));
			}
		}
		if(config.contains("base-stats")) {
			for(String s : config.getStringList("base-stats")) {
				base.add(new StatModifier(s));
			}
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

	public String getCleanedName() {
		return new String(name).replace("%material% ", "");
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

	public List<StatModifier> getModifyStats() {
		return modify;
	}

	public List<StatModifier> getBaseStats() {
		return base;
	}
}
