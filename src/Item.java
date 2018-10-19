import java.util.*;

public class Item{
	private String itemName;
	private double itemWeight;
	private double itemValue;

	// Constructor
	Item(String itemName, double itemWeight,double itemValue){
		this.itemName = itemName;
		this.itemWeight = itemWeight;
		this.itemValue = itemValue;
	}

	// Getter functions
	public String getItemName(){
		return this.itemName;
	}
	public double getItemWeight(){
		return this.itemWeight;
	}
	public double getItemValue(){
		return this.itemValue;
	}
	// Setter functions
	public void setItemValue(double newItemValue){
		this.itemValue = newItemValue;
	}
	// To String Method
	public String toString(){
		String weightString = String.format("%.2f", itemWeight);
		String valueString = String.format("%.2f", itemValue);
		return itemName + "(" + weightString + " lb, $" + valueString +")";
	}

}
