package pmj.bdp.sparkdf.emp;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import static org.apache.spark.sql.functions.sum;

public class SalaryByDepartment {

    public static void main(String[] args) {

        // Create SparkSession
        SparkSession spark = SparkSession.builder()
                .appName("Salary By Department")
                .master("local[*]")
                .getOrCreate();
        
        String csv_file = "input\\employee1\\employee_m.csv";

        // Read CSV file
        Dataset<Row> employees = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv( csv_file );

        // Display input
        employees.show();

        // Display schema
        employees.printSchema();

        // Group by department and calculate total salary
        Dataset<Row> result = employees
                .groupBy("dno")
                .agg(sum("salary").alias("total_salary"));

        // Display result
        result.show();

        // Stop Spark
        spark.stop();
    }
}