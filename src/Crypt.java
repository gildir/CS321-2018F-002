public class Crypt{
	
    public static String encrypt(String password, String key){
    	
    	//password String to array of chars
    	char[] computing = password.toCharArray();
    	
    	//convert char to bin to do bit manipulation, int array to do bit manipulations
    	int[] bin = charsToBin(computing);
    	
    	//minor bit manipulate
    	for(int i=0;i<bin.length;i++){
    	    bin[i] = bin[i]+2;
    	}
    	
    	//scramble based off username
    	char[] keyC = key.toCharArray();
    	int boost = (keyC[0] - 0)%25;
    	//edit first and last value of password
    	bin[0] = bin[0] + boost;
    	
    	//return encrpyted password
    	return binToString(bin);
    	
    }
    
    
    public static String decrypt(String encrypted, String key){
    	
    	//encrypted password to char
    	char[] computing = encrypted.toCharArray();
    	
    	//convert char to bin to do bit manipulation, int array to do bit manipulations
    	int[] bin = charsToBin(computing);
    	
    	//unscramble
    	char[] keyC = key.toCharArray();
    	int boost = (keyC[0] - 0)%25;
    	//edit first and last value of password
    	bin[0] = bin[0] - boost;
    	
    	//bit manipulate
    	for(int i=0;i<bin.length;i++){
    	    bin[i] = bin[i]-2;
    	}
    	
    	//return decrypted password
    	return binToString(bin);
    	
    }
  
    //convert array of bin to chars
    private static String binToString(int[] bin){
    	
    	char[] chars = new char[bin.length];
    	
    	//bin to chars
    	for(int i=0;i<bin.length;i++){
   	       chars[i] = (char)bin[i];
     	}
    	
    	//chars to String
    	String returning = String.valueOf(chars);
    	
    	return returning;
    	
    }
    
    //convert array of chars to array of bin
    private static int[] charsToBin(char[] chars){
    	
    	int[] bin = new int[chars.length];
    	
    	//convert each char to a binary
    	for(int i=0;i<chars.length;i++){
    	    bin[i] = chars[i]-0;
    	}
    	
    	return bin;
    	
    }
    	
	
}