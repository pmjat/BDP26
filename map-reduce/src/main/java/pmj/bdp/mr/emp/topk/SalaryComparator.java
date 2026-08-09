package pmj.bdp.mr.emp.topk;

import java.util.Comparator;

public class SalaryComparator implements Comparator<String> {

    @Override
    public int compare(String e1, String e2) {

        int salary1 = Integer.parseInt(e1.split(",")[3]);
        int salary2 = Integer.parseInt(e2.split(",")[3]);
        return Integer.compare(salary1, salary2);
    }

}