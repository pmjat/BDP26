package pmj.bdp.spark.emp;

import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class SalarySums_verbose {

	public static void main(String[] args) throws Exception {
		
        SparkConf conf = new SparkConf().setAppName("SalarySums").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile("data/employee.csv");        
	    JavaRDD<String[]> records = lines.map(line -> line.split(","));
	    
	    JavaPairRDD<String, Integer> dnoSalPairs
	    	= records.mapToPair(rec 
	    		-> new Tuple2<>(rec[6], Integer.parseInt(rec[4])));	
	    
	    System.out.println("Mapped <DNO, Salary> Pairs:");	    
	    dnoSalPairs.foreach( tuple -> System.out.println( tuple._1 + ", " + tuple._2));

	    JavaPairRDD<String, Integer> sums 
	    	= dnoSalPairs.reduceByKey((x, y) -> x + y);        

	    //collect and output on console
	    List<Tuple2<String, Integer>> output = sums.collect();	    
        output.forEach( tuple -> System.out.println(tuple._1 + ": " + tuple._2));
	    
        sc.stop();        
        sc.close();

	}
}