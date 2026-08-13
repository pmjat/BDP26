package pmj.bdp.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.Arrays;

public class WordCount2 {

	public static void main(String[] args) throws Exception {
		
		SparkConf sparkConf = new SparkConf().setAppName("SparkWordCount").setMaster("local");
	    JavaSparkContext sc = new JavaSparkContext(sparkConf);

		JavaRDD<String> textFile = sc.textFile("data/text_file.txt");
		JavaPairRDD<String, Integer> counts = 
			textFile.flatMap(s -> Arrays.asList(s.split(" ")).iterator())
			.mapToPair(word -> new Tuple2<>(word, 1))
		    .reduceByKey((a, b) -> a + b);
		
		counts.saveAsTextFile("output/wordcount.txt");
	    
	    sc.stop();
	    sc.close();				
	}
}