package pmj.bdp.mr.emp.join;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

class CustomerMapper
        extends Mapper<LongWritable, Text, CustomerOrderKey, Text> {

    private CustomerOrderKey outKey = new CustomerOrderKey();
    private Text outValue = new Text();

    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String[] record = value.toString().split(",");
        if (record.length < 3)
            return;

        String cid = record[0];
        String country = record[1];
        String state = record[2];
        // Composite key: (cid, C)
        outKey.set(cid, "C");
        // Keep tag in value also
        outValue.set("C," + country + "," + state);

        context.write(outKey, outValue);
    }
}