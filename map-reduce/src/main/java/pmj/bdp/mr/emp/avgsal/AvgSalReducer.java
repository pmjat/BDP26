package pmj.bdp.mr.emp.avgsal;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.IntWritable;

public class AvgSalReducer extends Reducer<Text, IntWritable, Text, DoubleWritable> {

	@Override
	public void reduce(Text key, Iterable<IntWritable> values, Context context)
			throws IOException, InterruptedException {
	 
		double sum = 0;
		int n = 0;
		for (IntWritable value : values) {
			n++;
			sum += value.get();
		}
		double avg = sum / n;
		context.write(key, new DoubleWritable(avg));
	}
}
