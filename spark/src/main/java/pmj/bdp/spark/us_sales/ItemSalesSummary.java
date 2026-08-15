package pmj.bdp.spark.us_sales;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import pmj.bdp.spark.setup.SparkContextSetup;
import scala.Tuple2;

public class ItemSalesSummary {

    public static void main(String[] args) {

    	JavaSparkContext sc = SparkContextSetup.setup( "Item Sales Summary" );
    	
        // Read data file
        String data_file = "..\\datasets\\us-sales\\orders_0.csv";

        getFrequentSoldItems( sc, data_file );

        sc.close();
    }
    
    public static void getFrequentSoldItems(JavaSparkContext sc, String data_file) {
    	
        JavaRDD<String> dataLines = sc.textFile(data_file);

        // Split each line into fields
        JavaRDD<String[]> dataTokens = dataLines.map(line -> line.split(","));

        final int POS_ITEM_CODE = 11;
        final int POS_ITEM_QTY = 12;

        // Create (itemCode, quantity) pairs
        JavaPairRDD<Integer, Integer> itemCountPairs = dataTokens.mapToPair(
        		t -> new Tuple2<>(Integer.parseInt(t[POS_ITEM_CODE]), 
        				Integer.parseInt(t[POS_ITEM_QTY])));

        // Compute total quantity for each item
        JavaPairRDD<Integer, Integer> itemTotalQty = itemCountPairs.reduceByKey( (v1, v2) -> v1 + v2);
        // Number of distinct items
        System.out.println(itemTotalQty.count());

        // Find frequently ordered items
        final int NFREQ = 850;
        JavaPairRDD<Integer, Integer> freqItems 
        	= itemTotalQty.filter(r -> r._2() > NFREQ);

        // Print frequent items
        freqItems.collect().forEach(
                r -> System.out.println(r._1() + "\t" + r._2()));
    	
    }
}