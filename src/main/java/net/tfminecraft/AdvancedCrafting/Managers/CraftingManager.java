package net.tfminecraft.AdvancedCrafting.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.HopperInventorySearchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.Plugins.TLibs.TLibs;
import me.Plugins.TLibs.Objects.API.ItemAPI;
import net.tfminecraft.AdvancedCrafting.AdvancedCrafting;
import net.tfminecraft.AdvancedCrafting.Cache.Cache;
import net.tfminecraft.AdvancedCrafting.Enums.StationFeedback;
import net.tfminecraft.AdvancedCrafting.Loaders.CategoryLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.RecipeLoader;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.CraftingRecipe;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.CraftingStation;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.RecipeCategory;

public class CraftingManager implements Listener{
	private HashMap<Player, Long> cooldown = new HashMap<>();
	private HashMap<Player, CraftingStation> currentStation = new HashMap<>();
	private HashMap<Location, CraftingStation> stations = new HashMap<>();

	private ItemAPI api = TLibs.getItemAPI();
	
	public boolean hasStation(Location loc) {
		if(stations.containsKey(loc)) return true;
		return false;
	}
	
	public CraftingStation get(Location loc) {
		if(stations.containsKey(loc)) return stations.get(loc);
		return null;
	}
	public void set(HashMap<Location, CraftingStation> map) {
		stations = map;
	}
	public List<CraftingStation> getStations() {
		List<CraftingStation> list = new ArrayList<>();
		for(Location l : stations.keySet()) {
			list.add(stations.get(l));
		}
		return list;
	}
	@EventHandler
	public void openStation(PlayerInteractEvent e) {
		if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		Block b = e.getClickedBlock();
		if(!b.getType().equals(Material.ANVIL)) return;
		e.setCancelled(true);
		Player p = e.getPlayer();
		if(cooldown.containsKey(p)) {
			if(cooldown.get(p) > System.currentTimeMillis()) {
				return;
			}
		}
		cooldown.put(p, System.currentTimeMillis() + (100));
		ItemStack i = p.getInventory().getItemInMainHand();
		if(hasStation(b.getLocation())) {
			CraftingStation station = stations.get(b.getLocation());
			if(!station.hasRecipe()) {
				currentStation.put(p, station);
				InventoryManager inv = new InventoryManager();
				inv.categoryView(p);
				return;
			}
			if(Cache.brandingTool != null) {
				if(i == null) return;
				if(i.getType().equals(Material.AIR)) return;
				if(api.getChecker().checkItemWithPath(i, Cache.brandingTool) && !station.hasRecipe()) {
					currentStation.put(p, station);
					InventoryManager inv = new InventoryManager();
					inv.categoryView(p);
					return;
				}
			}
			if(i == null) return;
			if(i.getType().equals(Material.AIR)) return;
			StationFeedback f = station.addMaterial(p, i);
			switch (f) {
				case NOT_INGREDIENT:
					p.sendMessage("§cThis item cannot be used for crafting");
					p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
					break;
				case WRONG_TYPE:
					p.sendMessage("§cThis item type is not needed for the recipe");
					p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
					break;
				case CAPACITY:
					p.sendMessage("§cYou already have the needed amount of this type");
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
					break;
				case NO_PERMS:
					p.sendMessage("§cYou lack permission to use this item in a recipe");
					p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
					break;
				default:
					p.playSound(p.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1f, 2f);
					p.spawnParticle(org.bukkit.Particle.CRIT_MAGIC, station.getLoc().clone().add(0.5, 1, 0.5), 10, 0.01, 0.01, 0.01);
					break;
			}
			return;
		}
		CraftingStation station = new CraftingStation(b.getLocation());
		stations.put(b.getLocation(), station);
		currentStation.put(p, station);
		InventoryManager inv = new InventoryManager();
		inv.categoryView(p);
	}
	
	@EventHandler
	public void applyHit(PlayerInteractEvent e) {
		if (!e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
		Block b = e.getClickedBlock();
		if (!b.getType().equals(Material.ANVIL)) return;
		Player p = e.getPlayer();
		if (!hasStation(b.getLocation())) return;
		ItemStack i = p.getInventory().getItemInMainHand();
		if (i == null || i.getType().equals(Material.AIR)) return;

		CraftingStation station = get(b.getLocation());

		if (api.getChecker().checkItemWithPath(i, Cache.brandingTool)) {
			if (p.isSneaking()) {
				station.cancel();
				p.sendMessage("§cProject cancelled");
				stations.remove(station.getLoc());
				p.getWorld().playSound(station.getLoc(), Sound.BLOCK_ANVIL_PLACE, 1f, 0.5f);
				p.spawnParticle(
					Particle.BLOCK_DUST,
					station.getLoc().clone().add(0.5, 1, 0.5),
					20,  // amount
					0.1, 0.2, 0.1,  // spread X,Y,Z
					Bukkit.createBlockData(Material.IRON_BLOCK)
				);
				return;
			} else {
				StationFeedback f = station.craft(p);
				if (f.equals(StationFeedback.SUCCESS)) {
					p.getWorld().playSound(station.getLoc(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
					p.getWorld().playSound(station.getLoc(), Sound.BLOCK_ANVIL_PLACE, 1f, 1f);
					p.spawnParticle(org.bukkit.Particle.LAVA, station.getLoc().clone().add(0.5, 1, 0.5), 50, 0.1, 0.2, 0.1);
					stations.remove(b.getLocation());
					currentStation.remove(p);
				} else {
					p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
					p.sendMessage("§c" + (f.equals(StationFeedback.LACKING_HITS) ? "You need to complete all the hits before finishing" :
							f.equals(StationFeedback.LACKING_ITEMS) ? "You have to add all the items before smithing" : ""));
				}
				return;
			}
		}

		StationFeedback f = station.hit(p, i);
		switch (f) {
			case LACKING_ITEMS:
				p.sendMessage("§cYou have to add all the items before smithing");
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
				break;
			case WRONG_TYPE:
				p.sendMessage("§cThis item cannot be used for crafting hits");
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
				break;
			case NONE:
				p.sendMessage("§cThis tool is not needed for this craft");
				p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1f, 1f);
				break;
			case CAPACITY:
				p.sendMessage("§cYou dont need more hits with this tool");
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1f, 0.5f);
				break;
			default:
				// Successful hit
				p.getWorld().playSound(station.getLoc(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
				p.spawnParticle(
					Particle.BLOCK_DUST,
					station.getLoc().clone().add(0.5, 1, 0.5),
					20,  // amount
					0.1, 0.2, 0.1,  // spread X,Y,Z
					Bukkit.createBlockData(Material.IRON_BLOCK)
				);
				break;
		}
	}

	
	@EventHandler
	public void invenClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(e.getView().getTitle().equalsIgnoreCase("§7Select Category")) {
			e.setCancelled(true);
			ItemStack i = e.getCurrentItem();
			if(i == null) return;
			ItemMeta m = i.getItemMeta();
			NamespacedKey key = new NamespacedKey(AdvancedCrafting.plugin, "ac_category");
			if(m.getPersistentDataContainer().get(key, PersistentDataType.STRING) == null) return;
			RecipeCategory c = CategoryLoader.getByString(m.getPersistentDataContainer().get(key, PersistentDataType.STRING));
			InventoryManager inv = new InventoryManager();
			inv.recipeView(p, c);
			return;
		} else if(e.getView().getTitle().equalsIgnoreCase("§7Select Recipe")) {
			e.setCancelled(true);
			ItemStack i = e.getCurrentItem();
			if(i == null) return;
			ItemMeta m = i.getItemMeta();
			NamespacedKey key = new NamespacedKey(AdvancedCrafting.plugin, "ac_recipe");
			if(m.getPersistentDataContainer().get(key, PersistentDataType.STRING) == null) return;
			CraftingRecipe recipe = RecipeLoader.getByString(m.getPersistentDataContainer().get(key, PersistentDataType.STRING));
			if(recipe.hasPermissions()) {
				boolean has = false;
				for(String s : recipe.getPermissions()) {
					if(p.hasPermission(s)) has = true;
				}
				if(!has) {
					p.sendMessage("§cYou dont have permission to use this recipe");
					return;
				}
			}
			CraftingStation station = currentStation.get(p);
			p.closeInventory();
			if(station.hasRecipe()) {
				p.sendMessage("§cStation already has a recipe selected");
				return;
			}
			station.setRecipe(recipe);
			p.sendMessage("§aRecipe "+recipe.getCleanedName()+ " §aselected!");
			p.getWorld().playSound(station.getLoc(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
			return;
		}
		
	}
	
	@EventHandler
	public void breakStation(BlockBreakEvent e) {
		Block b = e.getBlock();
		if(!hasStation(b.getLocation())) return;
		Player p = e.getPlayer();
		if(p != null) {
			p.getWorld().playSound(b.getLocation(), Sound.ENTITY_ARMOR_STAND_BREAK, 1f, 1f);
			p.getWorld().spawnParticle(Particle.SMOKE_LARGE, b.getLocation().add(0.5, 1, 0.5), 30, 0.3, 0.3, 0.3);
		}
		CraftingStation station = get(b.getLocation());
		station.drop();
		stations.remove(b.getLocation());
	}
}
