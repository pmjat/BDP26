package pmj.bdp.spark.emp;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;

public class SumSalary {

	public static void main(String[] args) throws Exception {
		
        SparkConf conf = new SparkConf().setAppName("SalarySums")
        		.setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile("data/employee.csv");        
	    JavaPairRDD<String, Integer> dnoSals 
    	= lines.mapToPair( line ->
			new Tuple2<>(line.split(",")[2], Integer.parseInt(line.split(",")[3]))
    	);
	    
	    JavaPairRDD<String, Integer> sums 
	    	= dnoSals.reduceByKey((x, y) -> x + y);        

	    //collect and output on console
	    List<Tuple2<String, Integer>> output = sums.collect();	    
        output.forEach( tuple -> System.out.println(tuple._1 + ": " + tuple._2));
	    
        sc.stop();        
        sc.close();

	}
}