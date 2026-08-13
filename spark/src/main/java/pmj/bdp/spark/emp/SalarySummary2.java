package pmj.bdp.spark.emp;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;

import scala.Tuple2;

public class SalarySummary2 {
	
	//seems demonstrating/doing nothing?	
	
	public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("firstSparkProject")
        		.setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("data/employee.csv");

	    JavaRDD<String[]> records = lines.map(line -> line.split(","));	    
	    JavaPairRDD<String, Integer> dnoSalPairs 
	    	= records.mapToPair(rec -> new Tuple2<>(rec[6], Integer.parseInt(rec[4])));	            
        
        JavaPairRDD<String, Integer> aggregate = dnoSalPairs.aggregateByKey(0,
        		new Function2<Integer, Integer, Integer>() {
					private static final long serialVersionUID = -9193256894160862119L;
					@Override
					public Integer call(Integer v1, Integer v2) throws Exception {
						return v1 + v2;
					}
				}, new Function2<Integer, Integer, Integer>() {
					@Override
					public Integer call(Integer v1, Integer v2) throws Exception {
						return v1 + v2;
					}
				});        
        
        aggregate.foreach( pair -> System.out.println(pair));                
        
        sc.stop();        
        sc.close();        
        
	}
	
}

