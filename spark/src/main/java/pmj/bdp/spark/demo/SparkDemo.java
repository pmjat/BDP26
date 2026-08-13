package pmj.bdp.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class SparkDemo {

    public static void main( String[] args ) {
    	
        System.out.println( "Hello World!" );
        
		//Setup Spark Context
		SparkConf sparkConf = new SparkConf()
				.setAppName("SparkWordCount")
				.setMaster("local"); //Local mode, alternatively CLuster mode
	    JavaSparkContext sc = new JavaSparkContext(sparkConf);

	    //"D:\MyDocs\Dropbox\programs-26\bdp\datasets\spark\mm.csv"
        String input = "..\\datasets\\spark\\mm.csv";

        JavaRDD<String> lines = sc.textFile( input );
        
        System.out.println("Lines count: " + lines.count());
        
        sc.stop();        
        sc.close();

    }	
	
}
