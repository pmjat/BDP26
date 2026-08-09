package pmj.bdp.mr.emp.join;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.MultipleInputs;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class ReduceSideJoin {

    public static void main(String[] args) throws Exception {

        if (args.length != 3) {

            System.err.println(
                "Usage: ReduceSideJoin " + "<customers> <orders> <output>");
            System.exit(-1);
        }

        Configuration conf = new Configuration();

        Job job = Job.getInstance(conf, "Reduce Side Join");

        job.setJarByClass(ReduceSideJoin.class);

        //Different mapper for each input file
        MultipleInputs.addInputPath(job, 
        		new Path(args[0]), TextInputFormat.class, CustomerMapper.class);

        MultipleInputs.addInputPath(job, new Path(args[1]),
                TextInputFormat.class, OrderMapper.class);

        //Output directory.
        FileOutputFormat.setOutputPath(job, new Path(args[2]));

        // Mapper output types.
        job.setMapOutputKeyClass(CustomerOrderKey.class);
        job.setMapOutputValueClass(Text.class);

        // Reducer
        job.setReducerClass(CustomerOrderJoinReducer.class);

        // Custom partitioner: partition only on CID.
        job.setPartitionerClass(CustomerOrderPartitioner.class);

        //Grouping: group only on CID.
        job.setGroupingComparatorClass(
                CustomerOrderGroupingComparator.class);

        //Final output types.
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
