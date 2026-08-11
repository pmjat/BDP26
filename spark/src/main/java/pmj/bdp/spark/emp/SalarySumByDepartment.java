package pmj.bdp.spark.emp;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import scala.Tuple2;

public class SalarySumByDepartment {
	
	private static JavaSparkContext sc;
	private static SparkSession spark;
	
    private static void Setup() {
		//SETUP: Create SparkSession and JavaSparkContext
        spark = SparkSession.builder()
                .appName("Salary SUM By Department")
                .master("local[*]")
                .getOrCreate();
        sc = new JavaSparkContext(spark.sparkContext());    	
    }

    public static void main(String[] args) {

    	Setup();

        // Read input file
        String csv_file = "..\\datasets\\employee1\\employee.csv";
        JavaRDD<String> data = sc.textFile(csv_file);

        // Convert each record into (dno, salary)
        JavaPairRDD<String, Integer> deptSalary = data
                .mapToPair(line -> {
                    String[] fields = line.split(",");
                    String dno = fields[2];
                    int salary = Integer.parseInt(fields[3]);
                    return new Tuple2<>(dno, salary);
                });

        // Compute department-wise salary
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