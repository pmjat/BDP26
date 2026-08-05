package pmj.bdp.mr.emp.avgsal2;

import java.io.IOException;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

class DeptAverageMapper
        extends Mapper<LongWritable, Text, Text, DoubleWritable> {

    private Text department = new Text();
    private DoubleWritable salary = new DoubleWritable();

    @Override
    protected void map(LongWritable key,
                       Text value,
                       Context context)
            throws IOException, InterruptedException {

        String line = value.toString();

        // Skip header line
        if (line.startsWith("emp-no")) {
            return;
        }

        String[] fields = line.split(",");

        // Skip malformed records
        if (fields.length != 6) {
            return;
        }

        department.set(fields[2].trim());

        try {
            salary.set(Double.parseDouble(fields[3].trim()));
            context.write(department, salary);
        }
        catch (NumberFormatException e) {
            // Ignore records with invalid salary
        }
    }
}