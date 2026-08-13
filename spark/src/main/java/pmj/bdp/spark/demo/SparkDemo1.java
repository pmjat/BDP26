package pmj.bdp.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import java.util.List;

public class SparkDemo1 {

	public static void main(String[] args) throws Exception {
		
        SparkConf conf = new SparkConf().setAppName("firstSparkProject").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        String data_file = "..\\datasets\\spark\\log.txt";


        JavaRDD<String> lines = sc.textFile( data_file );
        JavaRDD<String> debugLines = lines.filter( line -> line.contains("DEBUG") );
        System.out.println( "Count - Debug Line: " + debugLines.count() );
        List<String> top3 = debugLines.take(3);
        top3.forEach( line -> System.out.println(line));        
        
        debugLines.saveAsTextFile("output/log_debug.txt");  
        
        sc.stop();        
        sc.close();

	}
}