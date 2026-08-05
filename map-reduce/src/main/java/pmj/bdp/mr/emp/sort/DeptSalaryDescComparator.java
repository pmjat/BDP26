package pmj.bdp.mr.emp.sort;

import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.io.WritableComparable;

class DeptSalaryDescComparator
	extends WritableComparator {

    protected DeptSalaryDescComparator() {
        super(DeptSalaryKey.class, true);
    }

    @Override
    public int compare(WritableComparable a, WritableComparable b) {

        DeptSalaryKey k1 = (DeptSalaryKey)a;
        DeptSalaryKey k2 = (DeptSalaryKey)b;

        int cmp =
            k1.getDept().compareTo(k2.getDept());

        if(cmp != 0)
            return cmp;

        return Integer.compare(
                k2.getSalary().get(),
                k1.getSalary().get());
    }

}