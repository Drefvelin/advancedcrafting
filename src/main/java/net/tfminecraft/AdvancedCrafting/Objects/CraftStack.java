package net.tfminecraft.AdvancedCrafting.Objects;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import net.tfminecraft.AdvancedCrafting.AdvancedCrafting;
import net.tfminecraft.AdvancedCrafting.Loaders.IngredientLoader;
import net.tfminecraft.AdvancedCrafting.Managers.AlloyManager;
import net.tfminecraft.AdvancedCrafting.Objects.Alloys.Alloy;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.Ingredient;

public class CraftStack {
	private ItemStack item;
	
	public CraftStack(ItemStack i) {
		item = i;
	}
	
	public boolean isIngredient() {
		if(item == null) return false;
		ItemMeta m = item.getItemMeta();
		if(m == null) return false;
		NamespacedKey key = new NamespacedKey(AdvancedCrafting.plugin, "ac_ingredient_id");
		String id = m.getPersistentDataContainer().get(key, PersistentDataType.STRING);
		if(id != null) {
			return true;
		}
		return false;
	}
	public boolean isAlloy() {
		if(item == null) return false;
		ItemMeta m = item.getItemMeta();
		if(m == null) return false;
		NamespacedKey key = new NamespacedKey(AdvancedCrafting.plugin, "ac_alloy_id");
		String id = m.getPersistentDataContainer().get(key, PersistentDataType.STRING);
		if(id != null) {
			return true;
		}
		return false;
	}
	public Ingredient getIngredient() {
		if(!isIngredient()) {
			return null;
		}
		ItemMeta m = item.getItemMeta();
		NamespacedKey key = new NamespacedKey(AdvancedCrafting.plugin, "ac_ingredient_id");
		String id = m.getPersistentDataContainer().get(key, PersistentDataType.STRING);
		Ingredient i = IngredientLoader.getByString(id);
		return i;
	}
	public Alloy getAlloy() {
		if(!isAlloy()) {
			return null;
		}
		ItemMeta m = item.getItemMeta();
		NamespacedKey key = new NamespacedKey(AdvancedCrafting.plugin, "ac_alloy_id");
		String id = m.getPersistentDataContainer().get(key, PersistentDataType.STRING);
		Alloy a = AlloyManager.getAlloyById(id);
		return a;
	}
}
