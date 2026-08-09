package pmj.bdp.mr.emp.join;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class CustomerOrderGroupingComparator
        extends WritableComparator {

    protected CustomerOrderGroupingComparator() {
        super(CustomerOrderKey.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        CustomerOrderKey k1 = (CustomerOrderKey) a;
        CustomerOrderKey k2 = (CustomerOrderKey) b;
        return k1.getCid().compareTo(k2.getCid());
    }
}