package pmj.bdp.spark.emp;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import java.util.List;

public class AvgSal_GroupByKey_verbose {

	public static void main(String[] args) throws Exception {
		
        SparkConf conf = new SparkConf().setAppName("SalarySummary").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        //Load the data file as RDD of strings --> lines
        JavaRDD<String> lines = sc.textFile("data/employee.csv");

        //transform through map as RDD of tokens for each line
        JavaRDD<String[]> records = lines.map(line -> line.split(","));	    

        //transform through mapToPair as RDD of dno, <salary,1>
        // as key-value pairs. key: dno, value: tuple(salary,1)
        JavaPairRDD<String, Tuple2<Integer, Integer>> dnoSalPairs 
	    	= records.mapToPair(rec -> new Tuple2<>(rec[2], 
	    			new Tuple2<>(Integer.parseInt(rec[3]),1)));            

	    JavaPairRDD<String, Iterable<Tuple2<Integer, Integer>>> 
	    	groupedRecords = dnoSalPairs.groupByKey();

	    System.out.println("Appllied groupByKey");	    
	    groupedRecords.foreach( tuple -> {
	    	System.out.print( tuple._1 + ":");
	    	for( Tuple2<Integer, Integer> value : tuple._2()) 
	    		System.out.print( "<" + value._1 + "," + value._2 +">, ");
	    	System.out.println( "" );
	    });
	    
	    JavaPairRDD<String, Double> aggregate = groupedRecords.mapToPair( 
	    		new PairFunction<Tuple2<String, 
	    			Iterable<Tuple2<Integer, Integer>>>, String, Double>() {
	    			@Override
	    			public Tuple2<String, Double> call(Tuple2<String, 
	    					Iterable<Tuple2<Integer, Integer>>> tuple) 
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
	    List<Tuple2<String, Double>> output = aggregate.collect();	    
        output.forEach( tuple -> System.out.println(tuple._1 + ": " + tuple._2));
        
        sc.stop();        
        sc.close();        

	}
}