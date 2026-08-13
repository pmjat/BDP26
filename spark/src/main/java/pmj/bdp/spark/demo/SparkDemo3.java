package pmj.bdp.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.Arrays;

public class SparkDemo3 {

	public static void main(String[] args) throws Exception {
		
        SparkConf conf = new SparkConf().setAppName("Demo")
        		.setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        JavaRDD<String> lines = sc.textFile("data/emp.csv");
	    JavaRDD<String[]> rdd1 = lines.map( line -> line.split(","));
	    JavaRDD<String> rdd2 = lines.flatMap( 
	    		line -> Arrays.asList(line.split(",")).iterator());

	    rdd1.collect().forEach( e -> System.out.println(e));
	    rdd2.collect().forEach( e -> System.out.println(e));
	    
/*	    
	    List<String> c1 = rdd1.collect();	    
        c1.forEach( e -> System.out.println(e));
	    
	    List<String[]> c2 = rdd2.collect();
        c2.forEach( e -> {
        	for (String x : e)
        		System.out.println(x);
        });
*/        
        sc.stop();        
        sc.close();

	}
}