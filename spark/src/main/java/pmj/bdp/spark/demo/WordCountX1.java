package pmj.bdp.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.Arrays;
import java.util.List;

public class WordCountX1 {

	public static void main(String[] args) throws Exception {
		
		SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount").setMaster("local[*]");
	    JavaSparkContext sc = new JavaSparkContext(sparkConf);

		JavaRDD<String> textFile = sc.textFile("data/text_file.txt");
		JavaPairRDD<String, Integer> counts = 
			textFile.flatMap(s -> Arrays.asList(s.split(" ")).iterator())
			.mapToPair(word -> new Tuple2<>(word, 1))
		    .reduceByKey((a, b) -> a + b);
		
		//Uncomment this for outputting in a file
		counts.saveAsTextFile("output/wordcount.txt");

		//outputs on console
	    List<Tuple2<String, Integer>> output = counts.collect();	    
	    for (Tuple2<?, ?> tuple : output)
	        System.out.println(tuple._1() + ": " + tuple._2());		
	    
	    sc.stop();
	    sc.close();		
		
	}
		
}