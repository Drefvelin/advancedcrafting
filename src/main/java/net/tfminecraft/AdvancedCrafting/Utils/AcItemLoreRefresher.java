package net.tfminecraft.AdvancedCrafting.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.tfminecraft.AdvancedCrafting.Database.AlloyDatabase;
import net.tfminecraft.AdvancedCrafting.Loaders.IngredientLoader;
import net.tfminecraft.AdvancedCrafting.Managers.AlloyManager;
import net.tfminecraft.AdvancedCrafting.Objects.Alloys.Alloy;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.Ingredient;
import net.tfminecraft.AdvancedCrafting.Utils.AcItemTags.Kind;

public final class AcItemLoreRefresher {
	private AcItemLoreRefresher() {
	}

	public static RefreshResult refreshIfOutdated(ItemStack item) {
		if (!AcItemTags.isManaged(item)) {
			return RefreshResult.unchanged();
		}
		if (!isOutdated(item)) {
			return RefreshResult.unchanged();
		}
		return refresh(item);
	}

	public static boolean isOutdated(ItemStack item) {
		if (!AcItemTags.isManaged(item)) {
			return false;
		}
		if (AcItemTags.getLoreStart(item) < 0) {
			return true;
		}
		return getLiveRevision(item) > AcItemTags.getStoredRevision(item);
	}

	public static RefreshResult refresh(ItemStack item) {
		if (item == null || item.getType().isAir() || !item.hasItemMeta()) {
			return RefreshResult.unchanged();
		}
		Kind kind = AcItemTags.getKind(item);
		String id = AcItemTags.getId(item);
		if (kind == null || id == null) {
			return RefreshResult.unchanged();
		}

		ItemStack copy = item.clone();
		ItemMeta meta = copy.getItemMeta();
		List<String> lore = new ArrayList<>(meta.getLore() != null ? meta.getLore() : List.of());
		int loreStart = AcItemTags.getLoreStart(copy);
		int liveRevision;

		if (kind == Kind.INGREDIENT) {
			Ingredient ingredient = IngredientLoader.getByString(id);
			if (ingredient == null) {
				return RefreshResult.failed("unknown ingredient: " + id);
			}
			liveRevision = ingredient.getRevision();
			if (loreStart < 0) {
				loreStart = IngredientLore.applyTypeAndRole(lore, ingredient.getIngredientData());
			} else {
				IngredientLore.updateTypeAndRole(lore, loreStart, ingredient.getIngredientData());
			}
		} else {
			Alloy alloy = AlloyManager.getAlloyById(id);
			if (alloy == null) {
				alloy = new AlloyDatabase().loadAlloy(id);
				if (alloy != null) {
					AlloyManager.addAlloy(alloy);
				}
			}
			if (alloy == null) {
				return RefreshResult.failed("unknown alloy: " + id);
			}
			liveRevision = alloy.getRevision();
			if (loreStart < 0) {
				loreStart = IngredientLore.applyAlloyLore(lore, alloy.getData().getType(), alloy.getData().getTier());
			} else {
				IngredientLore.updateAlloyLore(lore, loreStart, alloy.getData().getType(), alloy.getData().getTier());
			}
		}

		meta.setLore(lore);
		AcItemTags.write(meta, liveRevision, loreStart);
		copy.setItemMeta(meta);
		copy.setAmount(item.getAmount());
		return RefreshResult.updated(copy);
	}

	private static int getLiveRevision(ItemStack item) {
		Kind kind = AcItemTags.getKind(item);
		String id = AcItemTags.getId(item);
		if (kind == null || id == null) {
			return 0;
		}
		if (kind == Kind.INGREDIENT) {
			Ingredient ingredient = IngredientLoader.getByString(id);
			return ingredient != null ? ingredient.getRevision() : 0;
		}
		Alloy alloy = AlloyManager.getAlloyById(id);
		if (alloy == null) {
			alloy = new AlloyDatabase().loadAlloy(id);
		}
		return alloy != null ? alloy.getRevision() : 0;
	}

	public static final class RefreshResult {
		private final boolean changed;
		private final ItemStack item;
		private final String error;

		private RefreshResult(boolean changed, ItemStack item, String error) {
			this.changed = changed;
			this.item = item;
			this.error = error;
		}

		public static RefreshResult unchanged() {
			return new RefreshResult(false, null, null);
		}

		public static RefreshResult failed(String error) {
			return new RefreshResult(false, null, error);
		}

		public static RefreshResult updated(ItemStack item) {
			return new RefreshResult(true, item, null);
		}

		public boolean isChanged() {
			return changed;
		}

		public ItemStack getItem() {
			return item;
		}

		public String getError() {
			return error;
		}
	}
}
