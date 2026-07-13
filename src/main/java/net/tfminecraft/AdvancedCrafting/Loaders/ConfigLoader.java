package net.tfminecraft.AdvancedCrafting.Loaders;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.Plugins.TLibs.Interface.LoaderInterface;
import me.Plugins.TLibs.Objects.API.SubAPI.StringFormatter;
import net.tfminecraft.AdvancedCrafting.Cache.Cache;
import net.tfminecraft.AdvancedCrafting.Objects.Data.PermissionNamespace;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.IngredientType;
import net.tfminecraft.AdvancedCrafting.Utils.StatToString;

public class ConfigLoader implements LoaderInterface{

	@Override
	public void load(File configFile) {
		FileConfiguration config = new YamlConfiguration();
        try {
        	config.load(configFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
		
		Cache.scrap = config.getString("scrap-path");
		
		Cache.alloyStation = config.getString("alloy-station", "v(BLAST_FURNACE)");
		Cache.ingredientStation = config.getString("ingredient-station", "v(OBSERVER)");

		Cache.brandingTool = config.getString("branding-tool", null);

		Cache.maxFactor = config.getDouble("max-factor", 1.5);

		Cache.debugStatRefresh = config.getBoolean("debug-stat-refresh", false);

		Cache.globalStatOffsets.clear();
		if (config.isConfigurationSection("global-stat-offsets")) {
			for (String statKey : config.getConfigurationSection("global-stat-offsets").getKeys(false)) {
				double offset = config.getDouble("global-stat-offsets." + statKey);
				if (offset != 0) {
					Cache.globalStatOffsets.put(statKey.toLowerCase(), offset);
				}
			}
		} else if (config.contains("global-stat-offsets")) {
			for (String entry : config.getStringList("global-stat-offsets")) {
				parseGlobalStatOffset(entry);
			}
		}

		Cache.permissionPrefix = config.getString("permission-prefix", "professions.");
		Cache.alloyPermissionNamespace = config.getString("alloy-permission-namespace", "alloy");
		Cache.permissionNamespaces.clear();
		if (config.isConfigurationSection("permission-namespaces")) {
			for (String key : config.getConfigurationSection("permission-namespaces").getKeys(false)) {
				String display = config.getString("permission-namespaces." + key + ".display", key);
				Cache.permissionNamespaces.put(key.toLowerCase(),
						new PermissionNamespace(key, StringFormatter.formatHex(display)));
			}
		}

		if(config.contains("stat-aliases")) {
			for(String s : config.getStringList("stat-aliases")) {
				String type = s.split("\\->")[0];
				String alias = s.split("\\->")[1];
				StatToString.add(type, alias);
			}
		}

		if(config.isConfigurationSection("combinations")) {
			Set<String> set = config.getConfigurationSection("combinations").getKeys(false);

			List<String> list = new ArrayList<String>(set);
			
			for(String key : list) {
				IngredientType base = TypeLoader.getIngredientTypeByString(key);
				if(base == null) continue;
				List<IngredientType> combinations = new ArrayList<>();
				for(String s : config.getConfigurationSection("combinations").getStringList(key)) {
					IngredientType type = TypeLoader.getIngredientTypeByString(s);
					if(type == null) continue;
					combinations.add(type);
				}
				Cache.combinations.put(base, combinations);
			}
		}
	}

	private void parseGlobalStatOffset(String entry) {
		if (entry == null || entry.isBlank() || !entry.contains("(")) {
			return;
		}
		String statId = entry.substring(0, entry.indexOf('(')).trim().toLowerCase();
		String amountPart = entry.substring(entry.indexOf('(') + 1, entry.lastIndexOf(')')).trim();
		try {
			double offset = Double.parseDouble(amountPart);
			if (offset != 0) {
				Cache.globalStatOffsets.put(statId, offset);
			}
		} catch (NumberFormatException ex) {
			// skip malformed entries
		}
	}

}
