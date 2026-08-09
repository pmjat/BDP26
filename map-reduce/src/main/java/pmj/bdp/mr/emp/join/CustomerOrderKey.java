package pmj.bdp.mr.emp.join;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

class CustomerOrderKey implements WritableComparable<CustomerOrderKey> {

    private Text cid = new Text();
    private Text tag = new Text();

    public CustomerOrderKey() {
    }

    public CustomerOrderKey(String cid, String tag) {
        this.cid.set(cid);
        this.tag.set(tag);
    }

    public void set(String cid, String tag) {
        this.cid.set(cid);
        this.tag.set(tag);
    }

    public Text getCid() {
        return cid;
    }

    public Text getTag() {
        return tag;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        cid.write(out);
        tag.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        cid.readFields(in);
        tag.readFields(in);
    }

    // Since "C" < "O", customer records will come before order records
    @Override
    public int compareTo(CustomerOrderKey other) {

        int cmp = cid.compareTo(other.cid);

        if (cmp != 0)
            return cmp;

        return tag.compareTo(other.tag);
    }

    @Override
    public String toString() {
        return cid.toString() + "," + tag.toString();
    }
}