package pmj.bdp.mr.emp.sort;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

class EmployeeSortReducer
	extends Reducer<DeptSalaryKey, Text, Text, Text> {

	@Override
	public void reduce(DeptSalaryKey key, Iterable<Text> values, Context context)
	        		   throws IOException, InterruptedException {
	
	for(Text emp : values)
		context.write(key.getDept(), emp);

	}
}

