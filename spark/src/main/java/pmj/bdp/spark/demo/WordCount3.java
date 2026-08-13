package pmj.bdp.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.Arrays;

public class WordCount3 {

	public static void main(String[] args) throws Exception {
		
		
		//setup Spark Context
		SparkConf sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local");
	    JavaSparkContext sc = new JavaSparkContext(sparkConf);

	    JavaRDD<String> lines = sc.textFile("data/text_file4.txt");
	    JavaRDD<String> lines1 = lines.map( line -> line.replaceAll("[-+.^:,&]",""));
	   
	    JavaRDD<String> words = lines1.flatMap(line -> Arrays.asList(line.split(" ")).iterator());

	    JavaPairRDD<String, Integer> counts = words.mapToPair(w -> new Tuple2<>(w, 1)).reduceByKey((x, y) -> x + y);
	    counts.saveAsTextFile("output/wordcount24.txt");  
	    
	    sc.stop();
	    sc.close();				
	}
}