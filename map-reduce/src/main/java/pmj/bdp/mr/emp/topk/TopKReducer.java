package pmj.bdp.mr.emp.topk;

import java.io.IOException;
import java.util.PriorityQueue;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class TopKReducer
        extends Reducer<Text, Text, Text, Text> {

    private PriorityQueue<String> heap;
    private int K;

    @Override
    protected void setup(Context context) {

        K = context.getConfiguration().getInt("top.k", 10);
        heap = new PriorityQueue<>(K, new SalaryComparator());
    }

    @Override
    public void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {

        for (Text value : values) {
            String employee = value.toString();

            int salary = Integer.parseInt( employee.split(",")[3] );
            if (heap.size() < K)
                heap.add(employee);
            else {
                String minEmployee = heap.peek();
                int minSalary = Integer.parseInt( minEmployee.split(",")[3]);
                if (salary > minSalary) {
                    heap.poll();
                    heap.add(employee);
                }
            }
        }
    }

    @Override
    protected void cleanup(Context context)
            throws IOException, InterruptedException {

        while (!heap.isEmpty()) {
            String employee = heap.poll();  
            //poll() gives first value, i.e. smallest salary
            String salary = employee.split(",")[3];
            context.write(new Text(salary), new Text(employee));
        }
    }
}