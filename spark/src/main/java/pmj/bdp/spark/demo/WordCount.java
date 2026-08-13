package pmj.bdp.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.Arrays;
import java.util.List;

public class WordCount {

	public static void main(String[] args) throws Exception {
		
		//setup Spark Context
		SparkConf sparkConf = new SparkConf()
				.setAppName("SparkWordCount")
				.setMaster("local"); //Local mode, alternatively CLuster mode
	    JavaSparkContext sc = new JavaSparkContext(sparkConf);

	    JavaRDD<String> lines = sc.textFile("data/article1.txt");

	    //following work is done in map function of MR job
	    JavaRDD<String> words = lines.flatMap(
	    		line -> Arrays.asList(line.split(" ")).iterator());	    
	    
	    JavaPairRDD<String, Integer> word_maps 
	    		= words.mapToPair(w -> new Tuple2<>(w, 1));
	    
	    //following work is done in reduce function of MR job
	    JavaPairRDD<String, Integer> counts 
	    		= word_maps.reduceByKey((x, y) -> x + y);

	    //output on file system
	    counts.saveAsTextFile("output/wordcount");  

		//outputs on console
	    List<Tuple2<String, Integer>> output = counts.collect();
        output.forEach( pair -> System.out.println(pair));
	    
	    sc.stop();
	    sc.close();				
	
	}

}