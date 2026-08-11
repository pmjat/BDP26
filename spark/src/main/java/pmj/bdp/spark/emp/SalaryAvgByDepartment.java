package pmj.bdp.spark.emp;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import scala.Tuple2;

public class SalaryAvgByDepartment {

    public static void main(String[] args) {

    		//SETUP
        // Create SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("Salary SUM By Department")
                .master("local[*]")
                .getOrCreate();
        // Create JavaSparkContext
        JavaSparkContext sc = new JavaSparkContext(spark.sparkContext());

        // Read input file
        String csv_file = "..\\datasets\\employee1\\employee.csv";
        JavaRDD<String> data = sc.textFile(csv_file);

        JavaPairRDD<String, Tuple2<Integer, Integer>> deptSalary =
                data.mapToPair(line -> {

                    String[] fields = line.split(",");

                    String dno = fields[2];
                    int salary = Integer.parseInt(fields[3]);

                    return new Tuple2<>(
                            dno,
                            new Tuple2<>(salary, 1)
                    );
                });

        JavaPairRDD<String, Tuple2<Integer, Integer>> totals =
                deptSalary.reduceByKey(
                        (x, y) -> new Tuple2<>(
                                x._1() + y._1(),   // sum
                                x._2() + y._2()    // count
                        )
                );

        JavaPairRDD<String, Double> averageSalary =
                totals.mapValues(
                        x -> (double) x._1() / x._2()
                );        

        // Print result
        averageSalary.collect().forEach(
                x -> System.out.println(x._1() + "\t" + x._2())
        );
        
        // Stop Spark
        sc.close();
        spark.stop();
    }
}