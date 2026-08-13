package pmj.bdp.spark.wordcount;

import java.util.ArrayList;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class LineCounter1 {

	public static void main(String[] args) throws Exception {

	    SparkConf sparkConf = new SparkConf().setAppName("JavaWordCount").setMaster("local[*]");
	    JavaSparkContext ctx = new JavaSparkContext(sparkConf);
		String inputFile = "data/article1.txt";  

		JavaRDD<String> lines = ctx.textFile(inputFile, 1);
		JavaPairRDD<String, Integer> xtract 
	      = lines.flatMapToPair(line -> {
		  	  ArrayList<Tuple2<String, Integer>> values = new ArrayList<>();
		  	  int wc = line.split(" ").length;
		  	  values.add(new Tuple2<>("Lines#", 1));
		  	  values.add(new Tuple2<>("Words#", wc));
		  	  values.add(new Tuple2<>("char#", line.length()));
		  	  return values.iterator();
	      });

	    JavaPairRDD<String, Integer> counts 
	      = xtract.reduceByKey((Integer i1, Integer i2) -> i1 + i2);

	    List<Tuple2<String, Integer>> output = counts.collect();
	    for (Tuple2<?, ?> tuple : output) {
	        System.out.println(tuple._1() + ": " + tuple._2());
	    }
	    ctx.stop();
	    ctx.close();
	}	
	
	
}
