package pmj.bdp.spark.demo;

import java.util.Arrays;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class Tester1 {
	
	private static JavaSparkContext sc;
	
    private static void setup() {
		//Setup Spark Context
		SparkConf sparkConf = new SparkConf()
				.setAppName("SparkWordCount")
				.setMaster("local"); //Local mode, alternatively CLuster mode
	    sc = new JavaSparkContext(sparkConf);
    }

	
    public static void main(String[] args) {
    	
    	setup();

        String input = "..\\datasets\\mr\\article1.txt";

        test1(input);

        // Stop Spark
        sc.close();
    }
    
    //private static void printList(List<String> list) {
    private static void printList(String[] list) {
    	for(String str : list)
    		System.out.println(str);
    }    
    
    private static void test1(String input_file) {
    	
        // Read input file
        JavaRDD<String> lines = sc.textFile(input_file);
        
        // Split each line into words
        
        JavaRDD<String> words =
                lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());
        
        // Print first 10 results
        printList(words.take(10).toArray(new String[0]));

        // Split each line into comma separated parts
        
        JavaRDD<String[]> parts = lines.map(line -> line.split(","));
        
        // Print first 10 results
        parts.take(10).forEach(x -> printList(x));
    	
    }
    
}