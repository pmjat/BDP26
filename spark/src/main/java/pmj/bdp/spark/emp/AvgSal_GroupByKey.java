package pmj.bdp.spark.emp;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

import java.util.List;

//import org.apache.commons.lang3.StringUtils;

public class AvgSal_GroupByKey {

	//Computes the Avergae using Group By and Mapping
	public static void main(String[] args) throws Exception {
		
        SparkConf conf = new SparkConf().setAppName("AvgSal")
        		.setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("data/employee.csv");

	    JavaPairRDD<String, Tuple2<Integer, Integer>> dnoSalPairs 
	    	= lines.mapToPair( line ->
    			new Tuple2<>(line.split(",")[2], //dno
    					new Tuple2<>(Integer.parseInt(line.split(",")[3]),1))
    												//salary
	    	);                    

	    JavaPairRDD<String, Iterable<Tuple2<Integer, Integer>>> 
	    			groupedRecords = dnoSalPairs.groupByKey();
	    
	    JavaPairRDD<String, Double> average = groupedRecords.mapToPair( 
	    		new PairFunction<Tuple2<String, Iterable<Tuple2<Integer, Integer>>>, String, Double>() {
	    			@Override
	    			public Tuple2<String, Double> call(
	    					Tuple2<String, Iterable<Tuple2<Integer, Integer>>> tuple) 
	    							throws Exception {
			    		String key = tuple._1;
			    		Iterable<Tuple2<Integer, Integer>> values = tuple._2();
			    		int count = 0;
			    		double sum = 0;
			    		for( Tuple2<Integer, Integer> value : values) {
			    			sum += value._1;
			    			count += value._2;	    			
			    		}
			    		double avg = sum / count;
			    		return new Tuple2<String, Double>(key, avg);
	    			}
	    		});	    
	    
	    //collect and output on console
	    List<Tuple2<String, Double>> output = average.collect();	    
        output.forEach( tuple -> System.out.println(tuple._1 + ": " + tuple._2));
        
        sc.stop();        
        sc.close();        

	}
}