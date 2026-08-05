package pmj.bdp.mr.emp.avgsal1;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class AvgSalReducer
	extends Reducer<Text, SumCountWritable,
                Text, DoubleWritable> {

	@Override
	public void reduce(Text key, Iterable<SumCountWritable> values, Context context)
	        throws IOException, InterruptedException {
	
	    double sum = 0;
	    int count = 0;
	
	    for (SumCountWritable value : values) {
	        sum += value.getSum();
	        count += value.getCount();
	    }
	
	    context.write(key, new DoubleWritable(sum / count));
	}
}