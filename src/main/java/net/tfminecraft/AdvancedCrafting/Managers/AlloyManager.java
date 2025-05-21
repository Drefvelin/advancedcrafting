package net.tfminecraft.AdvancedCrafting.Managers;

import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.lib.api.item.NBTItem;
import me.Plugins.TLibs.TLibs;
import me.Plugins.TLibs.Enums.APIType;
import me.Plugins.TLibs.Objects.API.BlockAPI;
import net.tfminecraft.AdvancedCrafting.Cache.Cache;
import net.tfminecraft.AdvancedCrafting.Enums.StationFeedback;
import net.tfminecraft.AdvancedCrafting.Objects.CraftStack;
import net.tfminecraft.AdvancedCrafting.Objects.Alloys.Alloy;
import net.tfminecraft.AdvancedCrafting.Objects.Alloys.AlloyForger;
import net.tfminecraft.AdvancedCrafting.Objects.Alloys.AlloyStation;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.Ingredient;

public class AlloyManager implements Listener{
	
	private HashMap<Location, AlloyStation> stations = new HashMap<>();
	private HashMap<Player, Long> cooldown = new HashMap<>();
	private static HashMap<String, Alloy> alloys = new HashMap<>();
	
	public static Alloy getAlloyById(String s) {
		if(alloys.containsKey(s)) return alloys.get(s);
		return null;
	}
	public static void addAlloy(Alloy a) {
		alloys.put(a.getId(), a);
	}
	public boolean hasStation(Location loc) {
		return stations.containsKey(loc);
	}
	public AlloyStation get(Location loc) {
		if(!stations.containsKey(loc)) return null;
		return stations.get(loc);
	}
	public void removeStation(AlloyStation station) {
		stations.remove(station.getLocation());
	}
	
	public boolean isAlloyStation(Block b) {
		String path = Cache.alloyStation;
		BlockAPI api = (BlockAPI) TLibs.getApiInstance(APIType.BLOCK_API);
		return api.getChecker().checkBlock(b, path);
	}
	
	@EventHandler
	public void addIngredient(PlayerInteractEvent e) {
		if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		Block b = e.getClickedBlock();
		if(!isAlloyStation(b)) return;
		Player p = e.getPlayer();
		if(cooldown.containsKey(p)) {
			if(cooldown.get(p) > System.currentTimeMillis()) {
				return;
			}
		}
		cooldown.put(p, System.currentTimeMillis() + (100));
		if(hasLava(p)) {
			forgeAlloy(e);
			return;
		}
		ItemStack i = p.getInventory().getItemInMainHand();
		CraftStack cs = new CraftStack(i);
		if(!cs.isIngredient()) {
			p.sendMessage("§cThis item is not an ingredient");
			return;
		}
		Ingredient ing = cs.getIngredient();
		AlloyStation station = null;
		if(hasStation(b.getLocation())) {
			station = get(b.getLocation());
		} else {
			station = new AlloyStation(b.getLocation());
			stations.put(b.getLocation(), station);
		}
		StationFeedback fb = station.addIngredient(ing);
		if(fb.equals(StationFeedback.EXISTS)) {
			p.sendMessage("§cThis ingredient is already part of the recipe");
			return;
		} else if(fb.equals(StationFeedback.CAPACITY)) {
			p.sendMessage("§cThe alloy forge cannot fit any more ingredients");
			return;
		} else if(fb.equals(StationFeedback.WRONG_BASE)) {
			p.sendMessage("§cThis ingredient cannot be used as the base");
			return;
		}
		i.setAmount(i.getAmount()-1);
		p.getWorld().playSound(station.getLocation(), Sound.BLOCK_ANVIL_HIT, 1f, 1f);
		p.sendTitle("§aAdded "+i.getItemMeta().getDisplayName(), station.getStatus(), 5, 30, 5);
	}
	public void forgeAlloy(PlayerInteractEvent e) {
		Block b = e.getClickedBlock();
		Player p = e.getPlayer();
		e.setCancelled(true);
		if(!hasStation(b.getLocation())) return;
		AlloyStation station = get(b.getLocation());
		if(station.getIngredients().size() < 2) {
			p.sendMessage("§cYou need at least 2 ingredients to make an alloy");
			return;
		}
		AlloyForger forger = new AlloyForger(station);
		forger.forge();
		p.getWorld().playSound(station.getLocation(), Sound.BLOCK_ANVIL_USE, 1f, 1f);
		p.getInventory().getItemInMainHand().setType(Material.BUCKET);
		removeStation(station);
	}
	
	@EventHandler
	public void breakStation(BlockBreakEvent e) {
		Block b = e.getBlock();
		if(!isAlloyStation(b)) return;
		if(!hasStation(b.getLocation())) return;
		AlloyStation station = get(b.getLocation());
		station.drop();
		stations.remove(b.getLocation());
	}
	
	private boolean hasLava(Player p) {
		ItemStack i = p.getInventory().getItemInMainHand();
		if(!i.getType().equals(Material.LAVA_BUCKET)) return false;
		if(NBTItem.get(i).hasType()) return false;
		if(CustomStack.byItemStack(i) != null) return false;
		return true;
	}
}
