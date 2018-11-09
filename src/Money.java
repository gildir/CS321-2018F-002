import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class Money{

  // dollars is broken down into 1 and 5 dollar bills
  // coins is broken down in quarters, dimes, pennies
  int numFives;
  int numOnes;
  int numQuarters;
  int numDimes;
  int numPennies;  
  
  public Money(){
    this.numFives = 0;
    this.numOnes = 0;
    this.numQuarters = 0;
    this.numDimes = 0;
    this.numPennies = 0;
  } 
  public Money(int ones){
    this.numFives = 0;
    this.numOnes = 20;
    this.numQuarters = 0;
    this.numDimes = 0;
    this.numPennies = 0;
  } 
  
  public double sum(){
    double sum = 0;
    sum += 5 * this.numFives;
    sum += this.numOnes;
    sum += 0.25 * this.numQuarters;
    sum += 0.10 * this.numDimes;
    sum += 0.01 * this.numPennies;
    return sum;
  }
  // print a line by line display of money with units 
  public String toString(){
    String s = "";
    // print dollars
    s = this.numFives + " fives\n" + 
        this.numOnes + " ones\n" + 
        this.numQuarters  + " quarters\n" + 
        this.numDimes + " dimes\n" + 
        this.numPennies + " pennies\n";
    double sum = sum();
    s += "Total:+ " + String.format("%1$,.2f", sum) + "\n";
    return s;
  }  
 }