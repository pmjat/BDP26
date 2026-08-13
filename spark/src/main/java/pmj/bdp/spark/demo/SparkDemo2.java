package pmj.bdp.spark.demo;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public class SparkDemo2 {

	public static void main(String[] args) throws Exception {
		
        SparkConf conf = new SparkConf().setAppName("firstSparkProject").setMaster("local[*]");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<Integer> rdd = sc.parallelize( Arrays.asList(1, 2, 3, 4));
        JavaRDD<Integer> result = rdd.map( x -> x*x );
        //result.sortBy(null, false, 0)
        
        System.out.println(StringUtils.join(result.collect(), ","));                
        
        sc.stop();        
        sc.close();

	}
}