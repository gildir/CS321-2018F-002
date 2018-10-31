public class Dollar{

  public int value;
  
  public Dollar(){
    this.value = 0;
  }
  public Dollar(int value){
    this.value = value;
  }
  public String toString(){
    return "" + this.value + " dollar bills";
  }
}