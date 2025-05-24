package net.tfminecraft.AdvancedCrafting.Objects.Alloys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.manager.ItemManager;
import net.tfminecraft.AdvancedCrafting.Cache.Cache;
import net.tfminecraft.AdvancedCrafting.Database.AlloyDatabase;
import net.tfminecraft.AdvancedCrafting.Managers.AlloyManager;
import net.tfminecraft.AdvancedCrafting.Objects.Crafting.Hits.CraftingHit;
import net.tfminecraft.AdvancedCrafting.Objects.Data.StatData;
import net.tfminecraft.AdvancedCrafting.Objects.Ingredients.Ingredient;
import net.tfminecraft.AdvancedCrafting.Objects.Stats.StatModifier;

public class AlloyForger {
	private int value;
	private AlloyStation station;
	private StatData stats;
	
	private HashMap<CraftingHit, Integer> hits = new HashMap<>();
	
	public AlloyForger(AlloyStation station) {
		this.station = station;
		this.value = station.getTotalValue();
	}
	
	@SuppressWarnings("deprecation")
	public NamableAlloy forge() {
		AlloyDatabase db = new AlloyDatabase();
		String result = db.getResult(station);
		Alloy a = null;
		boolean scrap = false;
		if(result == null) {
			if(isScrap()) {
				scrap = true;
				db.saveRecipe(station, "scrap");
			} else {
				mergeStats();
				generateHits();
				String name = getName();
				if(name.equalsIgnoreCase("full up")) {
					scrap = true;
				} else {
					a = new Alloy(name, station.getBaseItem(), stats, hits);
					db.saveAlloy(a);
					db.saveRecipe(station, a.getId());
					AlloyManager.addAlloy(a);
				}
			}
		} else if(result.equalsIgnoreCase("scrap")) {
			scrap = true;
		} else {
			a = AlloyManager.getAlloyById(result);
		}
		Location loc = station.getLocation().clone().add(0, 2, 0);
		if(scrap) {
			String scrapType = Cache.scrap.split("\\.")[0].toUpperCase();
			String scrapId = Cache.scrap.split("\\.")[1].toUpperCase();
			ItemManager itemManager = MMOItems.plugin.getItems();
			ItemStack template = itemManager.getMMOItem(MMOItems.plugin.getTypes().get(scrapType),scrapId).newBuilder().build();
			loc.getWorld().dropItem(loc, template);
			return null;
		}
		ItemStack item = loc.getWorld().dropItem(loc, a.build()).getItemStack();
		return new NamableAlloy(a, item);
	}
	private void generateHits() {
		for(Ingredient i : station.getIngredients()) {
			for(CraftingHit h : i.getIngredientData().getHits().keySet()) {
				if(!hits.containsKey(h)) {
					int r = (int) (Math.floor(Math.random()*3)+1);
					hits.put(h, r);
				}
			}
		}
		
	}

	private boolean isScrap() {
		if(Math.floor(Math.random()*100)+value > 80) return false;
		return true;
	}
	
	private String getName() {
		List<String> names = new ArrayList<>();
		for(String name : station.getBaseItem().getIngredientData().getScheme().getNames()) {
			names.add(name);
		}
		List<String> backups = new ArrayList<>();
		for(Ingredient i : station.getCatalysts()) {
			for(String name : i.getIngredientData().getScheme().getNames()) {
				backups.add(name);
			}
		}
		int i = (int) Math.round(Math.random()*(names.size()-1));
		String name = names.get(i);
		String id = name.replace(" ", "_").toLowerCase();
		while(AlloyManager.getAlloyById(id) != null && names.size() > 0) {
			i = (int) Math.round(Math.random()*(names.size()-1));
			name = names.get(i);
			id = new String(name.replace(" ", "_").toLowerCase());
			names.remove(i);
		}
		while(AlloyManager.getAlloyById(id) != null && backups.size() > 0) {
			i = (int) Math.round(Math.random()*(backups.size()-1));
			name = backups.get(i);
			id = new String(name.replace(" ", "_").toLowerCase());
			backups.remove(i);
		}
		if(AlloyManager.getAlloyById(id) != null) return "full up";
		return name;
	}
	
	public void mergeStats() {
		HashMap<String, StatModifier> base = new HashMap<>();
		HashMap<String, StatModifier> max = new HashMap<>();
		Ingredient baseItem = station.getBaseItem();
		for(StatModifier m : baseItem.getIngredientData().getStatData().getModifiers()) {
			base.put(m.getType(), m.copy());
			StatModifier copy = m.copy();
			copy.setAmount(copy.getAmount()*1.5);
			max.put(m.getType(), copy);
		}
		List<StatModifier> merge = new ArrayList<>();
		for(Ingredient i : station.getCatalysts()) {
			for(StatModifier m : i.getIngredientData().getStatData().getModifiers()) {
				if(baseItem.getIngredientData().statIsProtected(m)) continue;
				if(base.containsKey(m.getType())) {
					StatModifier n = new StatModifier(m.getType(), m.getAmount()-base.get(m.getType()).getAmount());
					merge.add(n);
				} else {
					if(Math.random()*100 < (20+i.getIngredientData().getValue()*3)) merge.add(m.copy());
				}
			}
		}
		stats = new StatData();
		merge(base, max, merge);
		for(String s : base.keySet()) {
			StatModifier mod = base.get(s);
			mod.setAmount(Math.round(mod.getAmount()*100.0)/100.0);
			stats.addModifier(mod);
		}
	}
	
	private void merge(HashMap<String, StatModifier> base, HashMap<String, StatModifier> max, List<StatModifier> list) {
		for(StatModifier m : list) {
			if(m.getAmount() < 0) {
				if(Math.floor(Math.random()*value) < 8) {
					if(base.containsKey(m.getType())) {
						base.get(m.getType()).modify(randomize(m.getType(), m.getAmount()));
						continue;
					}
				}
			}
			if(Math.floor(Math.random()*100)+value > 25) {
				if(base.containsKey(m.getType())) {
					base.get(m.getType()).modify(randomize(m.getType(), m.getAmount()));
					continue;
				}
				StatModifier add = m.copy();
				add.setAmount(randomize(add.getType(), add.getAmount()));
				base.put(m.getType(), add);
			}
		}
		for(String s : base.keySet()) {
			if(max.containsKey(s) && base.get(s).getAmount() > max.get(s).getAmount()) {
				base.put(s, max.get(s));
			}
		}
	}
	
	private double randomize(String t, double d) {
		double multiplier = 0.0;
		if(d < 0) {
			if(Math.floor(Math.random()*100)+value > 65) {
				d = d/2*-1;
			}
		}
		if(d > 0) {
			multiplier = (Math.floor(Math.random()*100)+10+(value*2))/100;
		} else {
			multiplier = (Math.floor(Math.random()*100)-(value*2))/100;
		}
		if(multiplier > 1.0) multiplier = 1.0;
		if(multiplier < 0.0) multiplier = 0.0;
		double amount = d*multiplier;
		amount = Math.round(amount*100);
		amount = amount/100;
		System.out.println(t + " amount: "+amount + "(original: "+d+")");
		return amount;
	}
}
