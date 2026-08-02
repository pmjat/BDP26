package pmj.mapr.maxtemp;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MaxTemperature {

	public static void main(String[] args) throws Exception {
 
		Job job = Job.getInstance();
		job.setJarByClass(MaxTemperature.class);
		job.setJobName("Max temperature");

		FileInputFormat.addInputPath(job, new Path("E:/programs-nosql-22/data/mr/weather"));
		//note here that input is directory, and not a file
		//map function will be called for each record of each file in this directory
		FileOutputFormat.setOutputPath(job, new Path("E:/programs-nosql-22/output/mr/temperature"));
 
		job.setMapperClass(MaxTemperatureMapper.class);
		job.setReducerClass(MaxTemperatureReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
 
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
