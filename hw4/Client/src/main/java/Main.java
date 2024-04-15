import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    final static private int NUMTHREADS = 100;
    public static void main(String[] args) throws InterruptedException, IOException {
        BlockingQueue<CSVObject> postQueue = new LinkedBlockingQueue<>();
        List<Long> getList = Collections.synchronizedList(new ArrayList<Long>());
        CountDownLatch completed = new CountDownLatch(NUMTHREADS);
        AtomicInteger numOfFailRequest = new AtomicInteger();
        AtomicInteger numOfSuccessRequest = new AtomicInteger();
        String FILENAME = "result.csv";

        // call each thread and record run time
        long ProgramStartTime = System.currentTimeMillis();
        for (int i = 0; i < NUMTHREADS; i++){
            Runnable thread = new Implementation(completed, postQueue, numOfSuccessRequest, numOfFailRequest);
            new Thread(thread).start();
        }

        // Runnable getThread = new GetThread(completed, getList);
        // new Thread(getThread).start();

        completed.await();
        long ProgramEndTime = System.currentTimeMillis();
        long ProgramWallTime = ProgramEndTime - ProgramStartTime;

        // write into csv file
        CSVWriter writer = new CSVWriter();
        writer.writeCSV(FILENAME, postQueue);

        // print out the run info
        System.out.println("---------------------------------------Throughput--------------------------------------------");
        int numOfTotalRequest = numOfSuccessRequest.intValue() + numOfFailRequest.intValue();
        System.out.println("Number of thread " + NUMTHREADS + " takes " + ProgramWallTime + " ms (" + (ProgramWallTime/1000.0) + "s)");
        System.out.println("Number of successful requests " + numOfSuccessRequest + " Number of failed requests " + numOfFailRequest);
        System.out.println("Throughput: (" + (numOfTotalRequest * 1.0) + "/" + (ProgramWallTime/1000.0) + "=) "
            + ((numOfTotalRequest)/(ProgramWallTime/1000.0)) + " requests/seconds");
        System.out.println("Each requests takes " + ((ProgramWallTime/1000.0)/(numOfTotalRequest)) + " seconds");

        //print out the get info
        System.out.println("---------------------------------------Get Latency--------------------------------------------");
        if (!getList.isEmpty()){
            long sum = 0;
            int length = getList.size();
            for (long t:getList){
                sum += t;
            }
            double getMean = sum/length;
            Collections.sort(getList);
            double getMedium = getList.get(length/2);
            long getNinetyNine = getList.get(length/99);
            long getMin = getList.get(0);
            long getMax = getList.get(length-1);

            System.out.println("mean response time: " + getMean + "ms");
            System.out.println("median response time: " + getMedium + "ms");
            System.out.println("p99 (99th percentile) response time: " + getNinetyNine + "ms");
            System.out.println("min response time: " + getMin + "ms");
            System.out.println("max response time: " + getMax + "ms");
        }
    }

}
