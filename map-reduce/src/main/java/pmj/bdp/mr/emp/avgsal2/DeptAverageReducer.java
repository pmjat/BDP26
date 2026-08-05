package pmj.bdp.mr.emp.avgsal2;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

class DeptAverageReducer
        extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

    private DoubleWritable average = new DoubleWritable();

    @Override
    protected void reduce(Text department,
                          Iterable<DoubleWritable> salaries,
                          Context context)
            throws IOException, InterruptedException {

        double sum = 0.0;
        int count = 0;

        for (DoubleWritable salary : salaries) {
            sum += salary.get();
            count++;
        }

        if (count > 0) {
            average.set(sum / count);
            context.write(department, average);
        }
    }
}