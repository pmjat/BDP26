package pmj.bdp.spark.emp;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import scala.Tuple2;

public class SalarySumByDepartment {
	
	private static JavaSparkContext sc;
	private static SparkSession spark;
	
    private static void setup() {
		//SETUP: Create SparkSession and JavaSparkContext
        spark = SparkSession.builder()
                .appName("Salary SUM By Department")
                .master("local[*]")
                .getOrCreate();
        sc = new JavaSparkContext(spark.sparkContext());    	
    }

    public static void main(String[] args) { 

    	setup();

        // Read input file
        String csv_file = "..\\datasets\\employee1\\employee.csv";
        JavaRDD<String> lines = sc.textFile(csv_file);
        // tokenize data items by separating by ","
        JavaRDD<String[]> dataset = lines.map(line -> line.split(","));
        
        // Convert each record into (dno, salary)
        JavaPairRDD<String, Integer> deptSalary = dataset
                .mapToPair(row -> {
                    String dno = row[2];
                    int salary = Integer.parseInt(row[3]);
                    return new Tuple2<>(dno, salary);
                });

        // Compute department-wise salary sum
        JavaPairRDD<String, Integer> salSums =
                deptSalary.reduceByKey((salary1, salary2) -> salary1 + salary2);

        // Print result
        salSums.collect().forEach(
                x -> System.out.println(x._1() + "\t" + x._2())
        );

        // Stop Spark
        sc.close();
        spark.stop();
    }
}