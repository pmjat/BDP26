package pmj.bdp.spark.wordcount;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import scala.Tuple2;

public class WordCount {
	
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

        // Read input file
        String input = "..\\datasets\\mr\\article1.txt";
        JavaRDD<String> lines = sc.textFile(input);

        // Split each line into words
        JavaRDD<String> words =
                lines.flatMap(line ->
                        java.util.Arrays.asList(line.split(" ")).iterator());

        // Create (word, 1) pairs
        JavaPairRDD<String, Integer> pairs =
                words.mapToPair(word -> new Tuple2<>(word, 1));

        // Count each word
        JavaPairRDD<String, Integer> result =
                pairs.reduceByKey((w1, w2) -> w1 + w2);

        // Print first 10 results
        result.take(10).forEach(
                x -> System.out.println(x._1() + "\t" + x._2())
        );

        // Stop Spark
        sc.close();
        spark.stop();
    }
}