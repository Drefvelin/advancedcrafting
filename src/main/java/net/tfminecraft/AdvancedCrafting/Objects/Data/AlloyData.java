package net.tfminecraft.AdvancedCrafting.Objects.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.tfminecraft.AdvancedCrafting.Objects.Crafting.Hits.CraftingHit;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.Ingredient;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.IngredientType;
import net.tfminecraft.AdvancedCrafting.Objects.Schemes.ColourScheme;
import net.tfminecraft.AdvancedCrafting.Objects.Schemes.ModelScheme;

public class AlloyData {
	private ColourScheme colourScheme;
	private int model;
	private IngredientType type;
	
	private ModelScheme modelScheme;
	private StatData stats;
	
	private HashMap<CraftingHit, Integer> hits = new HashMap<>();

	private List<String> permissions = new ArrayList<>();
	private String xp;
	
	public AlloyData(Ingredient base, StatData stats, HashMap<CraftingHit, Integer> hits, String xp) {
		colourScheme = base.getIngredientData().getScheme().getColourScheme();
		model = colourScheme.randomModel();
		this.stats = stats;
		this.type = base.getIngredientData().getType();
		this.modelScheme = base.getIngredientData().getModelScheme();
		this.hits = hits;
		permissions = base.getIngredientData().getPermissions();
		this.xp = xp;
	}
	
	public AlloyData(ColourScheme colourScheme, int model, IngredientType type, ModelScheme scheme, StatData stats, HashMap<CraftingHit, Integer> hits, List<String> permissions, String xp) {
		this.colourScheme = colourScheme;
		this.model = model;
		this.type = type;
		this.stats = stats;
		this.modelScheme = scheme;
		this.hits = hits;
		this.permissions = permissions;
		this.xp = xp;
	}

	public boolean hasXP() {
		return xp != null;
	}

	public String getXP() {
		return xp;
	}

	public boolean hasPermissions() {
		return permissions.size() > 0;
	}

	public List<String> getPermissions() {
		return permissions;
	}

	public ColourScheme getColourScheme() {
		return colourScheme;
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
