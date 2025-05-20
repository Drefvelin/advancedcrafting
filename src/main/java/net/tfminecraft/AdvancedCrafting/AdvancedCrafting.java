package net.tfminecraft.AdvancedCrafting;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.tfminecraft.AdvancedCrafting.Database.AlloyDatabase;
import net.tfminecraft.AdvancedCrafting.Database.Database;
import net.tfminecraft.AdvancedCrafting.Loaders.CategoryLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.ConfigLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.IngredientLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.QualityLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.ConversionLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.HitLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.RecipeLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.SchemeLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.StationLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.TypeLoader;
import net.tfminecraft.AdvancedCrafting.Managers.AlloyManager;
import net.tfminecraft.AdvancedCrafting.Managers.CommandManager;
import net.tfminecraft.AdvancedCrafting.Managers.CraftingManager;
import net.tfminecraft.AdvancedCrafting.Managers.IngredientManager;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.CraftingStation;

public class AdvancedCrafting extends JavaPlugin{
	
	public static AdvancedCrafting plugin;
	private final CategoryLoader categoryLoader = new CategoryLoader();
	private final RecipeLoader recipeLoader = new RecipeLoader();
	private final StationLoader stationLoader = new StationLoader();
	private final ConfigLoader configLoader = new ConfigLoader();
	private final TypeLoader typeLoader = new TypeLoader();
	private final IngredientLoader ingredientLoader = new IngredientLoader();
	private final ConversionLoader conversionLoader = new ConversionLoader();
	private final SchemeLoader schemeLoader = new SchemeLoader();
	private final HitLoader hitLoader = new HitLoader();
	private final QualityLoader qualityLoader = new QualityLoader();
	
	private final CommandManager commandManager = new CommandManager();
	private final CraftingManager craftingManager = new CraftingManager();
	private final AlloyManager alloyManager = new AlloyManager();
	private final IngredientManager ingredientManager = new IngredientManager();
	
	private final AlloyDatabase alloyDatabase = new AlloyDatabase();
	private final Database db = new Database();
	
	
	@Override
	public void onEnable() {
		plugin = this;
		createFolders();
		createConfigs();
		loadConfigs();
		registerListeners();
		getCommand(commandManager.cmd1).setExecutor(commandManager);
		alloyDatabase.loadAlloys();
		startManagers();
	}
	
	@Override
	public void onDisable() {
		db.clear();
		for(CraftingStation s : craftingManager.getStations()) {
			db.saveStation(s);
		}
	}
	
	public void registerListeners() {
		getServer().getPluginManager().registerEvents(commandManager, this);
		getServer().getPluginManager().registerEvents(craftingManager, this);
		getServer().getPluginManager().registerEvents(alloyManager, this);
		getServer().getPluginManager().registerEvents(ingredientManager, this);
	}
	public void loadConfigs() {
		configLoader.load(new File(getDataFolder(), "config.yml"));
		/*
		stationLoader.load(new File(getDataFolder(), "stations.yml"));
		*/
		File folder = new File(getDataFolder(), "colour-schemes");
    	for (final File file : folder.listFiles()) {
    		if(!file.isDirectory()) {
    			schemeLoader.loadColourSchemes(file);
    		}
    	}
    	folder = new File(getDataFolder(), "naming-schemes");
    	for (final File file : folder.listFiles()) {
    		if(!file.isDirectory()) {
    			schemeLoader.loadNamingSchemes(file);
    		}
    	}
    	folder = new File(getDataFolder(), "model-schemes");
    	for (final File file : folder.listFiles()) {
    		if(!file.isDirectory()) {
    			schemeLoader.loadModelSchemes(file);
    		}
    	}
    	categoryLoader.load(new File(getDataFolder(), "recipe-categories.yml"));
    	folder = new File(getDataFolder(), "recipes");
    	for (final File file : folder.listFiles()) {
    		if(!file.isDirectory()) {
    			recipeLoader.load(file);
    		}
    	}
    	
		typeLoader.loadIngredientTypes(new File(getDataFolder(), "ingredient-types.yml"));
		typeLoader.loadHitTypes(new File(getDataFolder(), "hit-types.yml"));
		hitLoader.load(new File(getDataFolder(), "crafting-hits.yml"));
		qualityLoader.load(new File(getDataFolder(), "qualities.yml"));
		ingredientLoader.load(new File(getDataFolder(), "ingredients.yml"));
		
	}
	public void startManagers() {
		ingredientManager.set(conversionLoader.load(new File(getDataFolder(), "conversions.yml")));
		craftingManager.set(db.loadStations());
	}
	public void createFolders() {
		if (!getDataFolder().exists()) getDataFolder().mkdir();
		File subFolder = new File(getDataFolder(), "naming-schemes");
		if(!subFolder.exists()) subFolder.mkdir();
		subFolder = new File(getDataFolder(), "colour-schemes");
		if(!subFolder.exists()) subFolder.mkdir();
		subFolder = new File(getDataFolder(), "recipes");
		if(!subFolder.exists()) subFolder.mkdir();
		subFolder = new File(getDataFolder(), "data");
		if(!subFolder.exists()) subFolder.mkdir();
		subFolder = new File(getDataFolder(), "data/players");
		if(!subFolder.exists()) subFolder.mkdir();
		subFolder = new File(getDataFolder(), "data/stations");
		if(!subFolder.exists()) subFolder.mkdir();
		subFolder = new File(getDataFolder(), "data/alloys");
		if(!subFolder.exists()) subFolder.mkdir();
		subFolder = new File(getDataFolder(), "data/alloy-recipes");
		if(!subFolder.exists()) subFolder.mkdir();
	}
	
	public void createConfigs() {
		String[] files = {
				"recipe-categories.yml",
				"config.yml",
				"ingredients.yml",
				"ingredient-types.yml",
				"hit-types.yml",
				"crafting-hits.yml",
				"conversions.yml",
				"qualities.yml",
				};
		for(String s : files) {
			File newConfigFile = new File(getDataFolder(), s);
	        if (!newConfigFile.exists()) {
	        	newConfigFile.getParentFile().mkdirs();
	            saveResource(s, false);
	        }
		}
	}
	
	public void reload() {
		loadConfigs();
	}
	public void reloadMessage(Player p) {
		p.sendMessage(ChatColor.GREEN + "[AdvancedCrafting]" + ChatColor.YELLOW + " Reloading plugin...");
		reload();
		p.sendMessage(ChatColor.GREEN + "[AdvancedCrafting]" + ChatColor.YELLOW + " Reloading complete!");
	}
}
