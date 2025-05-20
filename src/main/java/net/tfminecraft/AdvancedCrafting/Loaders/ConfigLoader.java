package net.tfminecraft.AdvancedCrafting.Loaders;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.Plugins.TLibs.Interface.LoaderInterface;
import net.tfminecraft.AdvancedCrafting.Cache.Cache;

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
		
		Cache.alloyStation = config.getString("alloy-station");
	}

}
