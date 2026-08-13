package pmj.bdp.spark.wordcount;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

public class LineCounter0 {

    public static void main( String[] args ) {
        System.out.println( "Hello World!" );
 
        SparkConf conf = new SparkConf().setAppName("firstSparkProject").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        String input = "data/mm.csv";
		//String input = "data/text1/text_file.txt";
 
        System.out.println("Trying to open: " + input);
 
        JavaRDD<String> lines = sc.textFile(input.toString());
        System.out.println("Lines count: " + lines.count());
        sc.stop();        
        sc.close();

    }
	
	
}
