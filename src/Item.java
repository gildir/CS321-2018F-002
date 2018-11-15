import java.util.*;
public class Item implements Cloneable {
 private String itemName;
 private String itemDescrip;
 private double itemWeight;
 private double itemValue;
 private String itemColor;
 private String itemSize;
 private static String color[] = {"Red", "Blue", "Green", "Purple", "Orange", "Black", "White", "Brown", "Yellow", "Silver", "Gold","Pink"};
 private static String size[] = {"Tiny", "Small", "Medium", "Large", "Huge"};

 // Constructor
 Item(String itemName, String itemDescrip, double itemWeight, double itemValue){
  this.itemName = itemName; 
  this.itemDescrip = itemDescrip;
  this.itemWeight = itemWeight;
  this.itemValue = itemValue;
  this.itemColor = getRandColor();
  this.itemSize = getRandSize();
 }

 // Getter functions
 public String getItemName(){
  return this.itemName;
 }
 
 public String getItemDescrip(){
  return this.itemDescrip;
 }
 
 public double getItemWeight(){
  return this.itemWeight;
 }
 public double getItemValue(){
  return this.itemValue;
 }
 public String getItemColor(){
     return this.itemColor;
 }
 public String getItemSize(){
     return this.itemSize;
 }
 // Setter functions
 public void setItemValue(double newItemValue){
  this.itemValue = newItemValue;
 }
 
 // To String Method
 public String toString(){
  String weightString = String.format("%.2f", itemWeight);
  String valueString = String.format("%.2f", itemValue);
  return "[" + itemSize + itemColor + "] " + itemName + "(" + weightString + " lb, $" + valueString +")";
 }
 // Method spits out a random color
 private String getRandColor(){
    Random rand = new Random();
    return Item.color[rand.nextInt(color.length)];
 }
 //Method spits out a random size
 private String getRandSize(){
     Random rand = new Random();
     return Item.size[rand.nextInt(size.length)] + " ";
 }

 public Object clone() {
     try {
        Item item = (Item)(super.clone());
        item.itemColor = getRandColor();
        item.itemSize = getRandSize();
        return item;
     } catch(CloneNotSupportedException e)
     {
         System.out.println("Unable to clone item");
     }
     //If the clone is not supported, return the original object
     return this;
 }

}
