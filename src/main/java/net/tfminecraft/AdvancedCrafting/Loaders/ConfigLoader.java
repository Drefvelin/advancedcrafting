package net.tfminecraft.AdvancedCrafting.Loaders;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.Plugins.TLibs.Interface.LoaderInterface;
import net.tfminecraft.AdvancedCrafting.Cache.Cache;
import net.tfminecraft.AdvancedCrafting.Utils.StatFactors;
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

		if(config.contains("stat-aliases")) {
			for(String s : config.getStringList("stat-aliases")) {
				String type = s.split("\\->")[0];
				String alias = s.split("\\->")[1];
				StatToString.add(type, alias);
			}
		}

		if(config.contains("stat-factors")) {
			for(String s : config.getStringList("stat-factors")) {
				String type = s.split("\\(")[0];
				Integer factor = 1;
				try {
					factor = Integer.parseInt(s.split("\\(")[1].replace(")", ""));
				} catch (Exception e) {
					Bukkit.getLogger().info("Error trying to parse string to integer: "+s.split("\\(")[1].replace(")", ""));
					e.printStackTrace();
				}
				StatFactors.add(type, factor.doubleValue());
			}
		}
	}

}
