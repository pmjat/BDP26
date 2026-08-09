package pmj.bdp.mr.emp.join0;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

class CustomerMapper
	extends Mapper<LongWritable, Text, Text, Text> {
	
	private Text outKey = new Text();
	private Text outValue = new Text();
	
	@Override
	public void map(LongWritable key,
	                Text value,
	                Context context)
	        throws IOException, InterruptedException {
	
	    String[] rec = value.toString().split(",");
	
	    String cid = rec[0];
	
	    outKey.set(cid);
	
	    outValue.set("C," + rec[1] + "," + rec[2]);
	
	    context.write(outKey, outValue);
	}
}