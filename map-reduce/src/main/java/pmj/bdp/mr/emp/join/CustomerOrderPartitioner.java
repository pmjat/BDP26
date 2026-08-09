package pmj.bdp.mr.emp.join;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Partitioner;

class CustomerOrderPartitioner
        extends Partitioner<CustomerOrderKey, Text> {

    @Override
    public int getPartition(CustomerOrderKey key, Text value, int numPartitions) {
        return (key.getCid().hashCode()
                & Integer.MAX_VALUE)
                % numPartitions;
    }
}