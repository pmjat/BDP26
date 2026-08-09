package pmj.bdp.mr.emp.join;

import java.io.IOException;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

class OrderMapper
        extends Mapper<LongWritable, Text, CustomerOrderKey, Text> {

    private CustomerOrderKey outKey = new CustomerOrderKey();
    private Text outValue = new Text();

    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String[] record = value.toString().split(",");
        if (record.length < 3)
            return;

        String orderNo = record[0];
        String cid = record[1];
        String amount = record[2];
        // Composite key: (cid, O)
        outKey.set(cid, "O");
        // Keep tag in value also
        outValue.set("O," + orderNo + "," + amount);

        context.write(outKey, outValue);
    }
}