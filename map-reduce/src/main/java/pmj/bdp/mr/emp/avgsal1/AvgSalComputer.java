package pmj.bdp.mr.emp.avgsal1;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class AvgSalComputer {

	public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: AvgSalComputer <input> <output>");
            System.exit(-1);
        }
				
		Job job = Job.getInstance();
		job.setJarByClass(AvgSalComputer.class);
		job.setJobName("Average Salary");
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
	 
		job.setMapperClass(AvgSalMapper.class);
		job.setCombinerClass(AvgSalCombiner.class);
		job.setReducerClass(AvgSalReducer.class);

		// Mapper output
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(SumCountWritable.class);

		// Final output
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);		
		
	 
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}