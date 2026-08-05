package pmj.bdp.mr.emp.sort;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class EmployeeSorter1 {

	public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: AvgSalComputer <input> <output>");
            System.exit(-1);
        }
				
		Job job = Job.getInstance();
		job.setJarByClass(EmployeeSorter1.class);
		job.setJobName("Average Salary");
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setMapperClass(EmployeeSortMapper.class);
		job.setReducerClass(EmployeeSortReducer.class);

		job.setMapOutputKeyClass(DeptSalaryKey.class);
		job.setMapOutputValueClass(Text.class);

		job.setGroupingComparatorClass(
		        DeptGroupingComparator.class);			
	 
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}