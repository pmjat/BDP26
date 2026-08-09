package pmj.bdp.mr.emp.join;

import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class CustomerOrderJoinReducer
        extends Reducer<CustomerOrderKey, Text, Text, Text> {

    private Text outKey = new Text();
    private Text outValue = new Text();

    @Override
    public void reduce(CustomerOrderKey key,
                       Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        String customer = null;
        boolean orderFound = false;
        
        for (Text value : values) {

        		String[] record = value.toString().split(",");

            if (record.length == 0)
                continue;

            String tag = record[0];
            if (tag.equals("C"))
                customer = record[1] + "," + record[2];

            else if (tag.equals("O")) {

                orderFound = true;
                String order = record[1] + "," + record[2];
                outKey.set(key.getCid());

                if (customer == null)
                    // No customer record
                    outValue.set("NULL,NULL," + order);
                else
                    outValue.set(customer + "," + order);
                context.write(outKey, outValue);
            }
        }

        // Customer exists but has no orders.
        if (customer != null && !orderFound) {
            outKey.set(key.getCid());
            outValue.set(customer + ",NULL,NULL");
            context.write(outKey, outValue);
        }
    }
}