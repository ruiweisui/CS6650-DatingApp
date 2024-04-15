import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;

public class CSVWriter {

    public void writeCSV(String fileName, BlockingQueue<CSVObject> queue) throws IOException {
        File csvFile = new File(fileName);
        FileWriter fileWriter = new FileWriter(csvFile);

        List<Long> responseTimes = new ArrayList<>();

        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

        while (!queue.isEmpty()){
            // write each line
            CSVObject result = queue.poll();
            bufferedWriter.write(result.getRequestType() +",");
            bufferedWriter.write(result.getStartTime() + ",");
            bufferedWriter.write(result.getEndTime() + ",");
            long responseTime = result.getEndTime() - result.getStartTime();
            bufferedWriter.write(responseTime + ",");
            responseTimes.add(responseTime);
            bufferedWriter.write(Integer.toString(result.getStatusCode()));

            bufferedWriter.newLine();
        }
        bufferedWriter.close();

        Collections.sort(responseTimes);
        int amount = responseTimes.size();
        long median = responseTimes.get(amount/2);
        long ninetyNine = responseTimes.get(amount/99);

        long total = 0;
        long min = responseTimes.get(0);
        long max = responseTimes.get(amount-1);

        for (Long time : responseTimes){
            total += time;
        }


        /*
        for (Long time : responseTimes){
            total += time;
            if (time < min){
                min = time;
            }
            if (time > max){
                max = time;
            }
        }

         */

        long mean = total/amount;

        System.out.println("Statistics");
        System.out.println("---------------------------------------Post Latency--------------------------------------------");
        System.out.println("mean response time: " + mean + "ms");
        System.out.println("median response time: " + median + "ms");
        System.out.println("p99 (99th percentile) response time: " + ninetyNine + "ms");
        System.out.println("min response time: " + min + "ms");
        System.out.println("max response time: " + max + "ms");
    }
}
