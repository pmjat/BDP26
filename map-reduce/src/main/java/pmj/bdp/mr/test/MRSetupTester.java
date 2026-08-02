package pmj.bdp.mr.test;
import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class MRSetupTester {

    // Mapper
    public static class CatMapper
            extends Mapper<LongWritable, Text, Text, IntWritable> {

        private final static IntWritable ONE = new IntWritable(1);
        private Text category = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context)
                throws IOException, InterruptedException {

            String[] record = value.toString().split(",");
            category.set(record[2].trim());
            context.write(category, ONE);
        }
    }

    // Reducer
    public static class CatReducer
            extends Reducer<Text, IntWritable, Text, IntWritable> {

        private IntWritable result = new IntWritable();

        @Override
        protected void reduce(Text key, Iterable<IntWritable> values,
                              Context context)
                throws IOException, InterruptedException {

            int sum = 0;

            for (IntWritable val : values) {
                sum += val.get();
            }

            if (sum > 10) {
                result.set(sum);
                context.write(key, result);
            }
        }
    }

    
    private static void check_hadoop_setup() {
    	System.out.println("java.library.path =");
    	System.out.println(System.getProperty("java.library.path"));

    	System.out.println("HADOOP_HOME =");
    	System.out.println(System.getenv("HADOOP_HOME"));

    	System.out.println("hadoop.home.dir =");
    	System.out.println(System.getProperty("hadoop.home.dir"));    	
    }
    
    
    // Driver
    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            System.err.println("Usage: CatCount <input> <output>");
            System.exit(-1);
        }
        
        System.setProperty("hadoop.home.dir", "C:\\hadoop");

        Configuration conf = new Configuration();

        conf.set("mapreduce.framework.name", "local");
        conf.set("fs.defaultFS", "file:///");

        // Try disabling native libraries
        conf.setBoolean("hadoop.native.lib", false);
        System.setProperty("hadoop.native.lib", "false");        
        
        check_hadoop_setup();

        Job job = Job.getInstance(conf, "Category Count");

        job.setJarByClass(MRSetupTester.class);

        job.setMapperClass(CatMapper.class);
        job.setReducerClass(CatReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
