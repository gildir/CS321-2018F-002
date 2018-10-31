public class Coin{

  public double value;
  public String name;
  
  public Coin(){
    this.value = 0;
    this.name = "";
  }
  public Coin(double value, String name){
    this.value = value;
    this.name = name;
  }
  public String toString(){
    return "" + this.name;
  }
}