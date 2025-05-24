package net.tfminecraft.AdvancedCrafting.Objects.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import net.tfminecraft.AdvancedCrafting.Loaders.HitLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.SchemeLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.TypeLoader;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.Hits.CraftingHit;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.IngredientType;
import net.tfminecraft.AdvancedCrafting.Objects.Schemes.ModelScheme;
import net.tfminecraft.AdvancedCrafting.Objects.Schemes.NamingScheme;
import net.tfminecraft.AdvancedCrafting.Objects.Stats.StatModifier;

public class IngredientData {
	private int weight;
	private int value;
	private boolean base;
	
	private IngredientType type;
	private NamingScheme scheme;
	private ModelScheme modelScheme;
	private StatData statData;
	
	private HashMap<CraftingHit, Integer> hits = new HashMap<>();
	private List<String> protectedStats = new ArrayList<>();
	public IngredientData(ConfigurationSection config) {
		if(config.contains("weight")) {
			weight = config.getInt("weight");
		} else {
			weight = 1;
		}
		if(config.contains("value")) {
			value = config.getInt("value");
		} else {
			value = 1;
		}
		if(config.contains("base")) {
			base = config.getBoolean("base");
		} else {
			base = false;
		}
		type = TypeLoader.getIngredientTypeByString(config.getString("type"));
		scheme = SchemeLoader.getNamingSchemeByString(config.getString("scheme", "default"));
		modelScheme = SchemeLoader.getModelSchemeByString(config.getString("model-scheme", "default"));
		statData = new StatData(config.getStringList("stats"));
		for(String s : config.getStringList("hits")) {
			String hit = s.split("\\.")[0];
			int a = Integer.parseInt(s.split("\\.")[1]);
			hits.put(HitLoader.getByString(hit), a);
		}
		if(config.contains("protected-stats")) {
			protectedStats = config.getStringList("protected-stats");
		}
	}

	public boolean statIsProtected(StatModifier mod) {
		return protectedStats.contains(mod.getType());
	}
	
	public boolean canBeBase() {
		return base;
	}
	public IngredientType getType() {
		return type;
	}

	public int getWeight() {
		return weight;
	}
	public int getValue() {
		return value;
	}
	public NamingScheme getScheme() {
		return scheme;
	}
	public ModelScheme getModelScheme() {
		return modelScheme;
	}

	public StatData getStatData() {
		return statData;
	}

	public HashMap<CraftingHit, Integer> getHits() {
		return hits;
	}
	
	
}
