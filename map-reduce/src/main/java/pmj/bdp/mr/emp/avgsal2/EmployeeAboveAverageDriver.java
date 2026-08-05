package pmj.bdp.mr.emp.avgsal2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class EmployeeAboveAverageDriver {

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {
            System.err.println(
                "Usage: EmployeeAboveAverageDriver <employee-input> <temp-output> <final-output>");
            System.exit(-1);
        }

        String employeeInput = args[0];
        String tempOutput = args[1];
        String finalOutput = args[2];

        Configuration conf = new Configuration();

        // Delete previous output directories if they already exist
        FileSystem fs = FileSystem.get(conf);

        fs.delete(new Path(tempOutput), true);
        fs.delete(new Path(finalOutput), true);

        // Job 1 : Compute Department Average Salary
        Job job1 = Job.getInstance(conf, "Department Average Salary");

        job1.setJarByClass(EmployeeAboveAverageDriver.class);

        job1.setMapperClass(DeptAverageMapper.class);
        job1.setReducerClass(DeptAverageReducer.class);

        job1.setMapOutputKeyClass(org.apache.hadoop.io.Text.class);
        job1.setMapOutputValueClass(org.apache.hadoop.io.DoubleWritable.class);

        job1.setOutputKeyClass(org.apache.hadoop.io.Text.class);
        job1.setOutputValueClass(org.apache.hadoop.io.DoubleWritable.class);

        FileInputFormat.addInputPath(job1, new Path(employeeInput));
        FileOutputFormat.setOutputPath(job1, new Path(tempOutput));

        boolean success = job1.waitForCompletion(true);

        if (!success) {
            System.exit(1);
        }

        // Job 2 : Find employees whose salary > department average
        Job job2 = Job.getInstance(conf, "Employees Above Department Average");

        job2.setJarByClass(EmployeeAboveAverageDriver.class);

        job2.setMapperClass(CompareMapper.class);

        // No reducer is required
        job2.setNumReduceTasks(0);

        job2.setOutputKeyClass(org.apache.hadoop.io.Text.class);
        job2.setOutputValueClass(org.apache.hadoop.io.Text.class);

        // Pass Job1 output through Distributed Cache
        Path avgFile = new Path(tempOutput, "part-r-00000");
        job2.addCacheFile(avgFile.toUri());

        FileInputFormat.addInputPath(job2, new Path(employeeInput));
        FileOutputFormat.setOutputPath(job2, new Path(finalOutput));

        System.exit(job2.waitForCompletion(true) ? 0 : 1);
    }
}