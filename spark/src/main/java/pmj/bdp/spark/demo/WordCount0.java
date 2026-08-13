package pmj.bdp.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.Arrays;

public class WordCount0 {

	public static void main(String[] args) throws Exception {
		
		
		//setup Spark Context
		SparkConf sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local");
	    JavaSparkContext sc = new JavaSparkContext(sparkConf);

	    JavaRDD<String> lines = sc.textFile("data/article1.txt");
	    JavaRDD<String> words = lines.flatMap(line -> Arrays.asList(line.split(" ")).iterator());

	    JavaPairRDD<String, Integer> counts = words.mapToPair(w -> new Tuple2<>(w, 1)).reduceByKey((x, y) -> x + y);
	    counts.saveAsTextFile("output/wordcount");  
	    
	    sc.stop();
	    sc.close();				
	}
}