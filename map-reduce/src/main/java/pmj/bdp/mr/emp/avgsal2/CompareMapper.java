package pmj.bdp.mr.emp.avgsal2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

class CompareMapper
        extends Mapper<LongWritable, Text, Text, Text> {

    // department -> average salary
    private Map<String, Double> deptAverage = new HashMap<>();

    @Override
    protected void setup(Context context)
            throws IOException, InterruptedException {

 
        // Read the file placed in the Distributed Cache.
        // The cached file is available as a local file.

    	URI[] cacheFiles = context.getCacheFiles();
       
        if (cacheFiles == null || cacheFiles.length == 0) {
            throw new IOException("Department average file not found.");
        }

        File cacheFile = new File(cacheFiles[0].getPath());

        BufferedReader br = new BufferedReader(new FileReader(cacheFile));

        String line;
        while ((line = br.readLine()) != null) {

            String[] fields = line.split("\\t");

            if (fields.length != 2)
                continue;

            String dept = fields[0].trim();
            double avg = Double.parseDouble(fields[1].trim());

            deptAverage.put(dept, avg);
        }

        br.close();
    }

    @Override
    protected void map(LongWritable key,
                       Text value,
                       Context context)
            throws IOException, InterruptedException {

        String line = value.toString();

        // Skip header
        if (line.startsWith("emp-no"))
            return;

        String[] fields = line.split(",");

        if (fields.length != 6)
            return;

        String empNo = fields[0].trim();
        String name = fields[1].trim();
        String dept = fields[2].trim();
        double salary = Double.parseDouble(fields[3].trim());

        Double avgSalary = deptAverage.get(dept);

        if (avgSalary != null && salary > 1.5 * avgSalary) {

            context.write(
                    new Text(empNo),
                    new Text(name + "," +
                             dept + "," +
                             salary));
        }
    }
}