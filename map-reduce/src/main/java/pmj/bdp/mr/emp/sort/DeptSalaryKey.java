package pmj.bdp.mr.emp.sort;

import java.io.DataOutput;
import java.io.IOException;
import java.io.DataInput;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;


class DeptSalaryKey
        implements WritableComparable<DeptSalaryKey> {

    private Text dept = new Text();
    private IntWritable salary = new IntWritable();

    public DeptSalaryKey() {
    }

    public DeptSalaryKey(String dept, int salary) {
        this.dept.set(dept);
        this.salary.set(salary);
    }

    public void set(String dept, int salary) {
        this.dept.set(dept);
        this.salary.set(salary);
    }

    public Text getDept() {
        return dept;
    }

    public IntWritable getSalary() {
        return salary;
    }

    @Override
    public void write(DataOutput out) throws IOException {
        dept.write(out);
        salary.write(out);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        dept.readFields(in);
        salary.readFields(in);
    }

    @Override
    public int compareTo(DeptSalaryKey other) {

        int cmp = dept.compareTo(other.dept);

        if (cmp != 0)
            return cmp;

        return salary.compareTo(other.salary);
    }

    @Override
    public String toString() {
        return dept + "\t" + salary;
    }
}