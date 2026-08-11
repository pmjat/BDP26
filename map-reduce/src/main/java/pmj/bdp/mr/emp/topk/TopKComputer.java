package pmj.bdp.mr.emp.topk;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class TopKComputer {

    public static void main(String[] args)
            throws Exception {

        if (args.length != 3) {

            System.err.println(
                "Usage: TopKComputer <input> <output> <K>");

            System.exit(-1);
        }

        String input = args[0];
        String output = args[1];
        int K = Integer.parseInt(args[2]);

        Configuration conf = new Configuration();

        // Make K available to Mapper and Reducer
        conf.setInt("top.k", K);
        Job job = Job.getInstance(conf,"Top-K Employees");
        job.setJarByClass(TopKComputer.class);

        // Input and Output
        FileInputFormat.addInputPath(job, new Path(input));
        FileOutputFormat.setOutputPath(job, new Path(output));

        // Mapper and Reducer
        job.setMapperClass(TopKMapper.class);
        job.setReducerClass(TopKReducer.class);
        // Mapper output Key and Value classes
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        // Reducer output Key and Value classes
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}