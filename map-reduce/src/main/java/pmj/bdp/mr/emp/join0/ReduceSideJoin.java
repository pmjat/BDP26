package pmj.bdp.mr.emp.join0;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ReduceSideJoin {

	public static void main(String[] args) throws Exception {

       if (args.length != 2) {
            System.err.println("Usage: AvgSalComputer <input> <output>");
            System.exit(-1);
        }
					
        Configuration conf = new Configuration();
		Job job = Job.getInstance(conf, "Reduce Side Join");
		job.setJarByClass(ReduceSideJoin.class);

		MultipleInputs.addInputPath(
		        job,
		        new Path(args[0]),
		        TextInputFormat.class,
		        CustomerMapper.class);

		MultipleInputs.addInputPath(
		        job,
		        new Path(args[1]),
		        TextInputFormat.class,
		        OrderMapper.class);

		FileOutputFormat.setOutputPath(
		        job,
		        new Path(args[2]));

		job.setReducerClass(JoinReducer.class);

		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
	}

}
