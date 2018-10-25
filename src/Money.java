import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Money{

  // dollars is broken down into 1,5,10,etc bills
  // coins is broken down in quarters, dimes, etc
  public ArrayList<Dollar> dollars;
  public ArrayList<Coin> coins;
  
  public Money(){
    this.dollars = new ArrayList<Dollar>();
    this.coins = new ArrayList<Coin>();
  } 
  public Money(int ones){
    this.dollars = new ArrayList<Dollar>();
    for(int i = 0; i < ones; i++){
      dollars.add(new oneDollar());
    }
    this.coins = new ArrayList<Coin>();
  } 
  
  public double sum(){
    double sum = 0;
    for(Dollar d : this.dollars){
      sum += d.value;
    }
    for(Coin c : this.coins){
      sum += c.value;
    }
    return sum;
  }
  // print a line by line display of money with units 
  public String toString(){
    String s = "";
    // print dollars
    int[] dollarCount = new int[6];
    for(Dollar d : this.dollars){
      switch(d.value){
        case(1): dollarCount[0]++;
                 break;
        case(5): dollarCount[1]++;
                 break;
        case(10):dollarCount[2]++;
                 break;
        case(20):dollarCount[3]++;
                 break;
        case(50):dollarCount[4]++;
                 break;
        case(100):dollarCount[5]++;
                 break;
      }
    }
    ArrayList<Dollar> dollarTypes = new ArrayList<Dollar>();
    dollarTypes.addAll(Arrays.asList(new oneDollar(), new fiveDollar(), new tenDollar(), new twentyDollar(), new fiftyDollar(), new hundredDollar()));
    for(int i = 0; i < 6; i++){
      s += dollarCount[i] + " " + dollarTypes.get(i).toString() + "\n";
    }
    // print coins 
    int[] coinCount = new int[4];
    for(Coin c : this.coins){
      switch(c.name){
        case("pennies"):coinCount[0]++; break;
        case("nickels"):coinCount[1]++; break;
        case("dimes"):coinCount[2]++; break;
        case("quarters"):coinCount[3]++; break;  
      }
    }
    ArrayList<Coin> coinTypes = new ArrayList<Coin>();
    coinTypes.addAll(Arrays.asList(new Penny(), new Nickel(), new Dime(), new Quarter()));
    for(int i = 0; i < 4; i++){
      s += coinCount[i] + " " + coinTypes.get(i).toString() + "\n";
    }
    double sum = sum();
    s += "Total:$" + sum + "\n";
    return s;
  }
  
  public ArrayList<Dollar> getDollars(){
    return dollars;
  }
  public ArrayList<Coin> getCoins(){
    return coins;
  }
}