package net.tfminecraft.AdvancedCrafting.Objects.Crafting;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

public class Quality {
	private String id;
	private double amount;
	private int value;
	private String name;
	private List<String> slots = new ArrayList<>();
	
	public Quality(String key, ConfigurationSection config) {
		id = key;
		amount = config.getDouble("amount");
		value = config.getInt("value");
		name = config.getString("name");
		if(config.contains("gem-slots")) {
			slots = config.getStringList("gem-slots");
		}
	}

	public String getId() {
		return id;
	}

	public double getAmount() {
		return amount;
	}

	public int getValue() {
		return value;
	}

	public String getName() {
		return name;
	}

	public List<String> getSlots() {
		return slots;
	}
	
	public boolean isValid(double d) {
		if(d >= amount) return true;
		return false;
	}
}
