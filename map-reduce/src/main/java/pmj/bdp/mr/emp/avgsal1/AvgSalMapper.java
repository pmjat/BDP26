package pmj.bdp.mr.emp.avgsal1;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class AvgSalMapper
	extends Mapper<LongWritable, Text, Text, SumCountWritable> {

	private Text dept = new Text();
	private SumCountWritable outValue = new SumCountWritable();

	@Override
	public void map(LongWritable key,
	                Text value,
	                Context context)
	        throws IOException, InterruptedException {
	
	    String[] record = value.toString().split(",");
	
	    String department = record[2];
	    int salary = Integer.parseInt(record[3]);
	
	    if (salary > 0) {
	        dept.set(department);
	        outValue.set(salary, 1);
	        context.write(dept, outValue);
	    }
	}
}