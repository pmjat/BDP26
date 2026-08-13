package pmj.bdp.spark.emp;

import java.io.Serializable;
import java.util.List;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function2;

import scala.Tuple2;

public class AvgSal_AggregateByKey {
	//Compute Average Using AGGREGATE
	public static void main(String[] args) {

        SparkConf conf = new SparkConf().setAppName("SalarySummary").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile("data/employee.csv");

	    JavaRDD<String[]> records = lines.map(line -> line.split(","));	    
	    JavaPairRDD<String, Integer> dnoSalPairs 
	    	= records.mapToPair(rec 
	    		-> new Tuple2<>(rec[6], Integer.parseInt(rec[4])));	            
	    
	    Function2<AvgCounter, Integer, AvgCounter> addAndCount 
	    		= new Function2<AvgCounter, Integer, AvgCounter>() {
	        @Override
	        public AvgCounter call(AvgCounter a, Integer x) {
	        	a.sum += x;
	        	a.count += 1;
	            return a;
	        }
	    };

	    Function2<AvgCounter, AvgCounter, AvgCounter> combine 
	    		= new Function2<AvgCounter, AvgCounter, AvgCounter>() {
	        @Override
	        public AvgCounter call(AvgCounter a, AvgCounter b) {
	            a.sum += b.sum;
	            a.count += b.count;
	            return a;
	        }
	    };        

	    AvgCounter initial = new AvgCounter(0,0);
	    JavaPairRDD<String, AvgCounter> aggregate 
	    	= dnoSalPairs.aggregateByKey(initial, addAndCount, combine);	    

	    //collect and output on console
	    List<Tuple2<String, AvgCounter>> output = aggregate.collect();	    
        output.forEach( tuple -> System.out.println(tuple._1 + ": " + tuple._2.average()));
        
        sc.stop();        
        sc.close();        
        
	}	

}

//define class to hold aggregated data while processing
final class AvgCounter implements Serializable {
	private static final long serialVersionUID = 1L;
	
	public AvgCounter(int sum, int count) {
		this.sum = sum;
		this.count = count;
	}
    public int sum;
    public int count;
    public float average() {
    	return sum / (float) count;
    }
}	

