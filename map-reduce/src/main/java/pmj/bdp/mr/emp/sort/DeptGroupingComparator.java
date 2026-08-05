package pmj.bdp.mr.emp.sort;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

class DeptGroupingComparator 
	extends WritableComparator {

    protected DeptGroupingComparator() {
        super(DeptSalaryKey.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {

        DeptSalaryKey k1 = (DeptSalaryKey)a;
        DeptSalaryKey k2 = (DeptSalaryKey)b;

        return k1.getDept().compareTo(k2.getDept());
    }
}