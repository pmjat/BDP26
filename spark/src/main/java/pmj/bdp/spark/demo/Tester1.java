package pmj.bdp.spark.demo;

import java.util.Arrays;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class Tester1 {
	
	private static JavaSparkContext sc;
	private static SparkSession spark;
	
    private static void setup() {
		//SETUP: Create SparkSession and JavaSparkContext
        spark = SparkSession.builder()
                .appName("Salary SUM By Department")
                .master("local[*]")
                .getOrCreate();
        sc = new JavaSparkContext(spark.sparkContext());    	
    }

	
    public static void main(String[] args) {
    	
    	setup();

        String input = "..\\datasets\\mr\\article1.txt";

        test1(input);

        // Stop Spark
        sc.close();
        spark.stop();
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