package net.tfminecraft.AdvancedCrafting.Objects.Data;

import java.util.HashMap;

import net.tfminecraft.AdvancedCrafting.Objects.Crafting.Hits.CraftingHit;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.Ingredient;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.IngredientType;
import net.tfminecraft.AdvancedCrafting.Objects.Schemes.ModelScheme;

public class AlloyData {
	private int model;
	private IngredientType type;
	
	private ModelScheme modelScheme;
	private StatData stats;
	
	private HashMap<CraftingHit, Integer> hits = new HashMap<>();
	
	public AlloyData(Ingredient base, StatData stats, HashMap<CraftingHit, Integer> hits) {
		model = base.getIngredientData().getScheme().getColourScheme().randomModel();
		this.stats = stats;
		this.type = base.getIngredientData().getType();
		this.modelScheme = base.getIngredientData().getModelScheme();
		this.hits = hits;
	}
	
	public AlloyData(int model, IngredientType type, ModelScheme scheme, StatData stats, HashMap<CraftingHit, Integer> hits) {
		this.model = model;
		this.type = type;
		this.stats = stats;
		this.modelScheme = scheme;
		this.hits = hits;
	}

	public int getModel() {
		return model;
	}

	public ModelScheme getModelScheme() {
		return modelScheme;
	}

	public StatData getStatData() {
		return stats;
	}

	public IngredientType getType() {
		return type;
	}

	public HashMap<CraftingHit, Integer> getHits() {
		return hits;
	}
}
