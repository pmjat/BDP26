package pmj.bdp.spark.sales;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import scala.Tuple2;
import java.util.List;

public class StateSalesSummary {
	
	public static void main(String[] args) {
		
        SparkConf conf = new SparkConf()
        		.setAppName("StateOrderSum")
        		.setMaster("local[*]");
        
        JavaSparkContext sc = new JavaSparkContext(conf);
	    
        JavaRDD<String> customers = sc.textFile("data/Customers.csv");
        JavaRDD<String> orders = sc.textFile("data/Orders.csv");
        
        //Project CustomerID, State
        JavaPairRDD<String, String> custRecs = customers.mapToPair(
        		x -> new Tuple2<>(x.split(",")[0], x.split(",")[2])
        	);
        
        //Project CustomerID,Amount
        JavaPairRDD<String, Integer> orderRecs = orders.mapToPair(
        		x -> new Tuple2<>(x.split(",")[1],Integer.parseInt(x.split(",")[2]))
        	);
        
        //Perform Join ON CustomerID
        //JOIN is performed on "this.key=other.key"
        //Produces CustomerID, (State, Amount)
        JavaPairRDD<String, Tuple2<String, Integer>> 
        		joinedRDD = custRecs.join(orderRecs);

        System.out.println("Joined RDD:");
        joinedRDD.foreach( r -> System.out.println(r._1 + "," + r._2._1 + "," + r._2._2));        
        
        //Project State, OrdrAmount
        JavaPairRDD<String, Integer> stateOrders = joinedRDD.mapToPair(
        		t -> new Tuple2<>( t._2._1, t._2._2)
        	);

        System.out.println("State Orders:");
        stateOrders.foreach( r -> System.out.println(r._1 + "," + r._2));        
        //Compute state wise sum of OrdrAmount
	    JavaPairRDD<String, Integer> summary = stateOrders.reduceByKey((x, y) -> x + y);        

	    //collect and output on console
	    List<Tuple2<String, Integer>> output = summary.collect();	    
        System.out.println("State wise sum of Order Amounts:");
	    output.forEach( tuple -> System.out.println(tuple._1 + ": " + tuple._2));
	    
	    sc.stop();        
        sc.close();        
		
	}
}
