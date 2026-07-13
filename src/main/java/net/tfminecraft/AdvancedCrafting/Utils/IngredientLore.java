package net.tfminecraft.AdvancedCrafting.Utils;

import java.util.List;

import me.Plugins.TLibs.Objects.API.SubAPI.StringFormatter;
import net.tfminecraft.AdvancedCrafting.Loaders.IngredientLoader;
import net.tfminecraft.AdvancedCrafting.Objects.Data.AlloyRecipe;
import net.tfminecraft.AdvancedCrafting.Objects.Data.IngredientData;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.Ingredient;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.IngredientType;

public final class IngredientLore {
	private IngredientLore() {
	}

	public static String formatTypeLine(IngredientData data) {
		return formatTypeLine(data.getType());
	}

	public static String formatTypeLine(IngredientType type) {
		return StringFormatter.formatHex("#cf7c72Type: #d9bb93" + type.getName());
	}

	public static String formatTierLine(int tier) {
		return StringFormatter.formatHex("§e[#ebd05bTier " + toRoman(tier) + "§e]");
	}

	public static String formatCatalystLine() {
		return StringFormatter.formatHex("§e[#d190deCatalyst§e]");
	}

	public static int applyTypeAndRole(List<String> lore, IngredientData data) {
		int start;
		String typeLine = formatTypeLine(data);
		if (lore.isEmpty() || isBlankLoreLine(lore.get(0))) {
			start = 0;
			if (lore.isEmpty()) {
				lore.add(typeLine);
			} else {
				lore.set(0, typeLine);
			}
		} else {
			lore.add(" ");
			lore.add(typeLine);
			start = lore.size() - 1;
		}
		lore.add(roleLine(data));
		return start;
	}

	public static void updateTypeAndRole(List<String> lore, int start, IngredientData data) {
		ensureSize(lore, start + 2);
		lore.set(start, formatTypeLine(data));
		lore.set(start + 1, roleLine(data));
	}

	public static int applyAlloyLore(List<String> lore, IngredientType type, int tier) {
		int start = lore.size();
		lore.add(formatTypeLine(type));
		lore.add(formatTierLine(tier));
		return start;
	}

	public static void updateAlloyLore(List<String> lore, int start, IngredientType type, int tier) {
		ensureSize(lore, start + 2);
		lore.set(start, formatTypeLine(type));
		lore.set(start + 1, formatTierLine(tier));
	}

	public static void appendTypeAndRole(List<String> lore, IngredientData data) {
		applyTypeAndRole(lore, data);
	}

	private static String roleLine(IngredientData data) {
		if (data.canBeBase() && data.hasTier()) {
			return formatTierLine(data.getTier());
		}
		return formatCatalystLine();
	}

	private static void ensureSize(List<String> lore, int size) {
		while (lore.size() < size) {
			lore.add("");
		}
	}

	public static int resolveAlloyTier(AlloyRecipe recipe, String alloyId) {
		if (recipe == null) {
			org.bukkit.Bukkit.getLogger().warning(
					"AC: Alloy " + alloyId + " has no recipe; defaulting tier to I.");
			return 1;
		}
		Ingredient base = IngredientLoader.getByString(recipe.getBaseId());
		if (base == null || !base.getIngredientData().hasTier()) {
			org.bukkit.Bukkit.getLogger().warning(
					"AC: Alloy " + alloyId + " base '" + recipe.getBaseId()
							+ "' has no tier; defaulting tier to I.");
			return 1;
		}
		return base.getIngredientData().getTier();
	}

	private static String toRoman(int tier) {
		return switch (tier) {
			case 1 -> "I";
			case 2 -> "II";
			case 3 -> "III";
			case 4 -> "IV";
			default -> String.valueOf(tier);
		};
	}

	private static boolean isBlankLoreLine(String line) {
		if (line == null) {
			return true;
		}
		return line.replaceAll("§.", "").trim().isEmpty();
	}
}
