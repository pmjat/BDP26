package pmj.bdp.mr.emp.topk;

import java.io.IOException;
import java.util.PriorityQueue;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class TopKMapper
        extends Mapper<LongWritable, Text, Text, Text> {

    private PriorityQueue<String> min_heap;
    private int K;

    @Override
    protected void setup(Context context) {
        K = context.getConfiguration().getInt("top.k", 10);
        min_heap = new PriorityQueue<>(K, new SalaryComparator());
    }

    @Override
    public void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String employee = value.toString();
        String[] record = employee.split(",");

        // Skip header / bad records
        if (record.length < 6)
            return;

        int salary;
        try {
            salary = Integer.parseInt(record[3]);
        }
        catch (NumberFormatException e) {
            return;
        }

        if (min_heap.size() < K)
        		min_heap.add(employee);
        else {
            String minEmployee = min_heap.peek();
            int minSalary = Integer.parseInt(minEmployee.split(",")[3]);
            if (salary > minSalary) {
            		min_heap.poll();
            		min_heap.add(employee);
            }
        }
    }

    @Override
    protected void cleanup(Context context)
            throws IOException, InterruptedException {

        Text key = new Text("TOP");
        for (String employee : min_heap)
            context.write(key,new Text(employee));
    }
}