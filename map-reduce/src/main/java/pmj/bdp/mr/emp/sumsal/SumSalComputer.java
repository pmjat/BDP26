package pmj.bdp.mr.emp.sumsal;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class SumSalComputer {

	public static void main(String[] args) throws Exception {

		
        if (args.length != 2) {
            System.err.println("Usage: AvgSalComputer <input> <output>");
            System.exit(-1);
        }
				
		Job job = Job.getInstance();
		job.setJarByClass(SumSalComputer.class);
		job.setJobName("Average Salary");
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
	 
		job.setMapperClass(SumSalMapper.class);
		job.setReducerClass(SumSalReducer.class);
		// Note in this case you do not need to define separate Combiner
		// standard reducer would work
		job.setCombinerClass(SumSalReducer.class);
		// Otherwise, if you need, you can define and specify it as following
		//job.setCombinerClass(SumSalCombiner.class);
	
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
	 
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}