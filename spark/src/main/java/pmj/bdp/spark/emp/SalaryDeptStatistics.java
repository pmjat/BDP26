package pmj.bdp.spark.emp;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;
import scala.Tuple3;
import java.util.List;

//import org.apache.commons.lang3.StringUtils;

public class SalaryDeptStatistics {

	public static void main(String[] args) throws Exception {
		
        SparkConf conf = new SparkConf().setAppName("SalarySummary").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("data/employee.csv");

	    JavaRDD<String[]> records = lines.map(line -> line.split(","));	    
	    JavaPairRDD<String, Tuple2<Integer, Integer>> dnoSalPairs 
	    	= records.mapToPair(rec -> new Tuple2<>(rec[6], new Tuple2<>(Integer.parseInt(rec[4]),1)));            

	    JavaPairRDD<String, Iterable<Tuple2<Integer, Integer>>> groupedRecords = dnoSalPairs.groupByKey();
	    JavaPairRDD<String, Tuple3<Integer, Double, Integer>> aggregate = groupedRecords.mapToPair(
	    		//type parameters T (type of source rdd), K, V
	    		new PairFunction<Tuple2<String, Iterable<Tuple2<Integer, Integer>>>, 
	    			String, Tuple3<Integer, Double, Integer>>() {
	    			@Override
	    			public Tuple2<String, Tuple3<Integer, Double, Integer>> call(Tuple2<String, Iterable<Tuple2<Integer, Integer>>> tuple) throws Exception {
			    		String key = tuple._1;
			    		Iterable<Tuple2<Integer, Integer>> values = tuple._2();
			    		int count = 0;
			    		double sum = 0;
			    		int min = Integer.MAX_VALUE;
			    		int max = Integer.MIN_VALUE;
			    		for( Tuple2<Integer, Integer> value : values) {
			    			sum += value._1;
			    			count += value._2;
			    			if ( min > value._1) 
			    				min = value._1;
			    			if ( max < value._1) 
			    				max = value._1;
			    		}
			    		double avg = sum / count;
			    		return new Tuple2<String, Tuple3<Integer, Double, Integer>>(key, 
			    				new Tuple3<Integer, Double, Integer>(min,avg,max));
	    			}
	    		});
	    
	    //collect and output on console
	    List<Tuple2<String, Tuple3<Integer, Double, Integer>>> output = aggregate.collect();	    
        output.forEach( tuple -> System.out.println(tuple._1 + ", " + tuple._2._1() + ", " + tuple._2._2() + ", " + tuple._2._3()));
        
        sc.stop();        
        sc.close();        

	}
}