package net.tfminecraft.AdvancedCrafting.Managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
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

import net.tfminecraft.AdvancedCrafting.AdvancedCrafting;
import net.tfminecraft.AdvancedCrafting.Enums.StationFeedback;
import net.tfminecraft.AdvancedCrafting.Loaders.CategoryLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.RecipeLoader;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.CraftingRecipe;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.CraftingStation;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.RecipeCategory;

public class CraftingManager implements Listener{
	private HashMap<Player, CraftingStation> currentStation = new HashMap<>();
	private HashMap<Location, CraftingStation> stations = new HashMap<>();
	
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
		if(hasStation(b.getLocation())) {
			CraftingStation station = stations.get(b.getLocation());
			if(!station.hasRecipe()) {
				currentStation.put(p, station);
				InventoryManager inv = new InventoryManager();
				inv.categoryView(p);
				return;
			}
			if(p.isSneaking()) {
				StationFeedback f = station.craft(p);
				if(f.equals(StationFeedback.SUCCESS)) {
					p.getWorld().playSound(station.getLoc(), Sound.BLOCK_ANVIL_PLACE, 1f, 1f);
					stations.remove(b.getLocation());
					if(currentStation.containsKey(p)) {
						currentStation.remove(p, station);
					}
				}
				return;
			}
			ItemStack i = p.getInventory().getItemInMainHand();
			if(i == null) return;
			StationFeedback f = station.addMaterial(p, i);
			if(f.equals(StationFeedback.NOT_INGREDIENT)) {
				p.sendMessage("§cThis item cannot be used for crafting");
				return;
			}
			if(f.equals(StationFeedback.WRONG_TYPE)) {
				p.sendMessage("§cThis item type is not needed for the recipe");
				return;
			}
			if(f.equals(StationFeedback.CAPACITY)) {
				p.sendMessage("§cYou already have the needed amount of this type");
				return;
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
		if(!e.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;
		Block b = e.getClickedBlock();
		if(!b.getType().equals(Material.ANVIL)) return;
		Player p = e.getPlayer();
		if(!hasStation(b.getLocation())) return;
		ItemStack i = p.getInventory().getItemInMainHand();
		if(i == null) return;
		if(i.getType().equals(Material.AIR)) return;
		CraftingStation station = get(b.getLocation());
		StationFeedback f = station.hit(p, i);
		if(f.equals(StationFeedback.LACKING_ITEMS)) {
			p.sendMessage("§cYou have to add all the items before smithing");
			return;
		}
		if(f.equals(StationFeedback.WRONG_TYPE)) {
			p.sendMessage("§cThis item cannot be used for crafting hits");
			return;
		}
		if(f.equals(StationFeedback.NONE)) {
			p.sendMessage("§cThis tool is not needed for this craft");
			return;
		}
		if(f.equals(StationFeedback.CAPACITY)) {
			p.sendMessage("§cYou dont need more hits with this tool");
			return;
		}
		p.getWorld().playSound(station.getLoc(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
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
			CraftingStation station = currentStation.get(p);
			p.closeInventory();
			if(station.hasRecipe()) {
				p.sendMessage("§cStation already has a recipe selected");
				return;
			}
			station.setRecipe(recipe);
			p.sendMessage("§aRecipe "+recipe.getName()+ " §aselected!");
			return;
		}
		
	}
	
	@EventHandler
	public void breakStation(BlockBreakEvent e) {
		Block b = e.getBlock();
		if(!hasStation(b.getLocation())) return;
		CraftingStation station = get(b.getLocation());
		station.drop();
		stations.remove(b.getLocation());
	}
}
