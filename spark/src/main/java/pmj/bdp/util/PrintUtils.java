package pmj.bdp.util;

public class PrintUtils {
	
	public static void printOneLine(String[] strArray) {
		
		//Arrays.stream(strArray).forEach(str -> System.out.println(str));
		
    	System.out.print("<");    	
    	for(int i = 0; i < strArray.length; i++) 
    		System.out.print( strArray[i] + (i < strArray.length-1 ? "," : ""));
    	System.out.println(">");
	}
}
