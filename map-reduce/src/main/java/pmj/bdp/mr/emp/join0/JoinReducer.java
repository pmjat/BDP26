package pmj.bdp.mr.emp.join0;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

class JoinReducer
	extends Reducer<Text, Text, Text, Text> {

    @Override
    public void reduce(Text key,
                       Iterable<Text> values,
                       Context context)
            throws IOException, InterruptedException {

        String customer = "NULL,NULL";
        List<String> orders = new ArrayList<>();
        for(Text t : values) {
            String[] rec = t.toString().split(",");
            if(rec[0].equals("C"))
                customer = rec[1] + "," + rec[2];
            else
                orders.add(rec[1] + "," + rec[2]);
        }

        if(orders.isEmpty())
            context.write(key, new Text(customer + ",NULL,NULL"));
        else
            for(String order : orders)
                context.write(key, new Text(customer + "," + order));
    }
}