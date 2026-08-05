package pmj.bdp.mr.emp.sort;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

class EmployeeSortMapper 
	extends Mapper<LongWritable,Text, DeptSalaryKey,Text> {
	
	private DeptSalaryKey outKey = new DeptSalaryKey();

	@Override
	public void map(LongWritable key,
	     Text value,
	     Context context)
	throws IOException, InterruptedException {
	
	String[] rec = value.toString().split(",");	
	String dept = rec[2];
	int salary = Integer.parseInt(rec[3]);	
	outKey.set(dept, salary);
	
	context.write(outKey, value);
	}
}
