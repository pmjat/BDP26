package pmj.bdp.spark.emp;

import java.util.List;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class AvgSalary {
	
	//Verbose version of AvgSal
	//outputs intermediate values

	public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("SalarySummary")
        		.setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> lines = sc.textFile("data/employee.csv");
	    JavaRDD<String[]> records = lines.map(line -> line.split(","));
	    
	    JavaPairRDD<String, Tuple2<Integer, Integer>> dnoSals 
	    	= records.mapToPair(rec -> new Tuple2<>(rec[2], 
	    			new Tuple2<>(Integer.parseInt(rec[3]),1)));            

	    //Mapped <DNO, Salary> Pairs
	    //uncomment following two lines to see result of above "mapToPair"
	    //System.out.println("Mapped <DNO, Salary> Pairs:");	    
	    //dnoSalPairs.foreach( tuple -> System.out.println( tuple._1 + ", <" + tuple._2()._1 + "," + tuple._2()._2 +">"));

	    JavaPairRDD<String, Tuple2<Integer, Integer>> sums
	    	= dnoSals.reduceByKey((v1,v2) 
	    			-> new Tuple2<>(v1._1 + v2._1, v1._2 + v2._2));    

	    //uncomment following two lines to see result of "reduce by key"
	    //System.out.println("Appllied reduceByKey");
	    //sums.foreach( tuple -> System.out.println( tuple._1 + ", <" + tuple._2()._1 + "," + tuple._2()._2 +">"));
	    
	    JavaPairRDD<String, Double> avgs = sums.mapToPair( 
	    		v -> new Tuple2<String, Double>
	    				(v._1, v._2()._1/(double) v._2()._2));

	    //collect and output on console
	    System.out.println("Final Output:");
	    List<Tuple2<String, Double>> output = avgs.collect();	    
        output.forEach( tuple -> System.out.println(tuple._1 + ": " + tuple._2));
        
        sc.stop();        
        sc.close();        
        
	}	

}

