package net.tfminecraft.AdvancedCrafting.Managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import me.Plugins.TLibs.TLibs;
import me.Plugins.TLibs.Enums.APIType;
import me.Plugins.TLibs.Objects.API.ItemAPI;
import me.Plugins.TLibs.Objects.API.SubAPI.StringFormatter;
import net.tfminecraft.AdvancedCrafting.AdvancedCrafting;
import net.tfminecraft.AdvancedCrafting.Loaders.CategoryLoader;
import net.tfminecraft.AdvancedCrafting.Loaders.TypeLoader;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.CraftingRecipe;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.RecipeCategory;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.IngredientType;


public class InventoryManager {
	public void categoryView(Player p) {
		Inventory i = AdvancedCrafting.plugin.getServer().createInventory(null, 27, "§7Select Category");
		int x = 0;
		for(String key : CategoryLoader.get().keySet()) {
			i.setItem(x, getCategoryItem(CategoryLoader.getByString(key)));
			x++;
		}
		int slotn = 0;
		while(slotn < i.getSize()) {
			if(i.getItem(slotn) == null) {
				ItemStack fill = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
				ItemMeta fm = fill.getItemMeta();
				fm.setDisplayName("§8 ");
				fill.setItemMeta(fm);
				i.setItem(slotn, fill);
			}
			slotn++;
		}
		p.openInventory(i);
	}
	public void recipeView(Player p, RecipeCategory c) {
		Inventory i = AdvancedCrafting.plugin.getServer().createInventory(null, 27, "§7Select Recipe");
		int x = 0;
		for(CraftingRecipe recipe : c.getRecipes()) {
			i.setItem(x, getRecipeItem(recipe));
			x++;
		}
		int slotn = 0;
		while(slotn < i.getSize()) {
			if(i.getItem(slotn) == null) {
				ItemStack fill = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
				ItemMeta fm = fill.getItemMeta();
				fm.setDisplayName("§8 ");
				fill.setItemMeta(fm);
				i.setItem(slotn, fill);
			}
			slotn++;
		}
		p.openInventory(i);
	}
	
	private ItemStack getCategoryItem(RecipeCategory c) {
		ItemStack i = new ItemStack(Material.BARRIER, 1);
		if(c.getRecipes().size() == 0) {
			ItemMeta m = i.getItemMeta();
			m.setDisplayName(c.getName());
			List<String> lore = new ArrayList<>();
			lore.add("§7No entries");
			m.setLore(lore);
			i.setItemMeta(m);
			return i;
		}
		ItemAPI api = (ItemAPI) TLibs.getApiInstance(APIType.ITEM_API);
		ItemStack template = api.getCreator().getItemFromPath("m."+c.getRecipes().get(0).getTemplate());
		if(template != null) {
			i.setType(template.getType());
		} 
		ItemMeta m = i.getItemMeta();
		if(template != null && template.getItemMeta().hasCustomModelData()) {
			m.setCustomModelData(template.getItemMeta().getCustomModelData());
		}
		m.setDisplayName(c.getName());
		List<String> lore = new ArrayList<>();
		lore.add(StringFormatter.formatHex("#e0e677"+c.getRecipes().size()+" #b2db93Entries"));
		m.setLore(lore);
		NamespacedKey key = new NamespacedKey(AdvancedCrafting.plugin, "ac_category");
		m.getPersistentDataContainer().set(key, PersistentDataType.STRING, c.getId());
		i.setItemMeta(m);
		return i;
	}
	
	private ItemStack getRecipeItem(CraftingRecipe r) {
		ItemStack i = new ItemStack(Material.BARRIER, 1);
		ItemAPI api = (ItemAPI) TLibs.getApiInstance(APIType.ITEM_API);
		ItemStack template = api.getCreator().getItemFromPath("m."+r.getTemplate());
		if(template == null) {
			return i;
		}
		i.setType(template.getType());
		ItemMeta m = i.getItemMeta();
		if(template.getItemMeta().hasCustomModelData()) {
			m.setCustomModelData(template.getItemMeta().getCustomModelData());
		}
		m.setDisplayName("§7"+r.getCleanedName());
		List<String> lore = new ArrayList<>();
		lore.add(StringFormatter.formatHex("#d1a566Recipe:"));
		for(String s : r.getRecipe().keySet()) {
			IngredientType t = TypeLoader.getIngredientTypeByString(s);
			lore.add(StringFormatter.formatHex(t.getName()+"§7: #6dd695x"+r.getRecipe().get(s)));
		}
		m.setLore(lore);
		NamespacedKey key = new NamespacedKey(AdvancedCrafting.plugin, "ac_recipe");
		m.getPersistentDataContainer().set(key, PersistentDataType.STRING, r.getId());
		i.setItemMeta(m);
		return i;
	}
}
