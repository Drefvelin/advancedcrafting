package net.tfminecraft.AdvancedCrafting.Cache;

import java.util.HashMap;
import java.util.List;

import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.IngredientType;

public class Cache {
	public static String scrap;
	
	public static String alloyStation;
	public static String ingredientStation;

	public static HashMap<IngredientType, List<IngredientType>> combinations = new HashMap<>();

	public static boolean canCombine(IngredientType base, IngredientType type){
		if(!combinations.containsKey(base)) return true;
		return combinations.get(base).contains(type);
	}
}
