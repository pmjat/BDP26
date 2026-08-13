package pmj.bdp.spark.wordcount;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.ArrayList;
import java.util.List;

public class LineCounter {

	private static JavaSparkContext sc;
	
    private static void setup() {
		//Setup Spark Context
		SparkConf sparkConf = new SparkConf()
				.setAppName("SparkWordCount")
				.setMaster("local"); //Local mode, alternatively CLuster mode
	    sc = new JavaSparkContext(sparkConf);
    }

    static List<Tuple2<String, Integer>> getLineStats(String line) {

        List<Tuple2<String, Integer>> info = new ArrayList<>();

        info.add(new Tuple2<>("Lines#", 1));
        info.add(new Tuple2<>("Words#", line.split(" ").length));
        info.add(new Tuple2<>("Char#", line.length()));

        return info;
    }

    public static void main(String[] args) {

    	setup();
        // Read input
    	String input_file = "..\\datasets\\mr\\article1.txt";
        JavaRDD<String> lines = sc.textFile(input_file);

        // Generate statistics for each line
        JavaPairRDD<String, Integer> stats = lines.flatMapToPair(
                        line -> getLineStats(line).iterator()
                );

        // Aggregate statistics
        JavaPairRDD<String, Integer> result = stats.reduceByKey((c1, c2) -> c1 + c2);

        // Print result
        result.collect().forEach(
                x -> System.out.println(x._1() + "\t" + x._2())
        );

        sc.close();
    }
}