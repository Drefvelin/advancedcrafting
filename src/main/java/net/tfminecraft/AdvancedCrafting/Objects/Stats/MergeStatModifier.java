package net.tfminecraft.AdvancedCrafting.Objects.Stats;

public class MergeStatModifier {
	private String type;
	private double value;
	private int amount;
	
	public MergeStatModifier(StatModifier m) {
		type = m.getType();
		value = m.getAmount();
		amount = 1;
	}
	
	public int getAmount() {
		return amount;
	}
	public String getType() {
		return type;
	}

	public double getValue() {
		return value;
	}
	
	public void modify(double d) {
		this.value = this.value+d;
		amount++;
	}
	
	public StatModifier create() {
		value = value/amount;
		return new StatModifier(type, value);
	}
}
