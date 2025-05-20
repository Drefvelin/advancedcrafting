package net.tfminecraft.AdvancedCrafting.Managers;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import dev.lone.itemsadder.api.CustomStack;
import io.lumine.mythic.lib.api.item.NBTItem;
import net.tfminecraft.AdvancedCrafting.Objects.CraftStack;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.Ingredient;

public class IngredientManager implements Listener{
	private HashMap<String, Ingredient> ingredients = new HashMap<>();
	
	public void set(HashMap<String, Ingredient> map) {
		ingredients = map;
	}
	
	public Ingredient get(String path) {
		if(!ingredients.containsKey(path)) return null;
		return ingredients.get(path);
	}
	
	@EventHandler
	public void convertItem(PlayerInteractEvent e) {
		if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		Block b = e.getClickedBlock();
		if(!b.getType().equals(Material.OBSERVER)) return;
		Player p = e.getPlayer();
		ItemStack i = p.getInventory().getItemInMainHand();
		CraftStack cs = new CraftStack(i);
		if(cs.isIngredient()) return;
		Ingredient ing = getFromItem(i);
		if(ing == null) return;
		ing.buildTo(i);
	}
	
	public Ingredient getFromItem(ItemStack i) {
		String path = "";
		NBTItem nbt = NBTItem.get(i);
		if(nbt.hasType()) {
			path = "m."+nbt.getType().toLowerCase()+"."+nbt.getString("MMOITEMS_ITEM_ID").toLowerCase();
			System.out.println(path);
			return get(path);
		}
		if(CustomStack.byItemStack(i) != null) {
			CustomStack c = CustomStack.byItemStack(i);
			return get("ia."+c.getNamespacedID());
		}
		return get("v."+i.getType().toString().toLowerCase());
	}
}
