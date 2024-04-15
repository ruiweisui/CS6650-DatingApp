import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    final static private int NUMTHREADS = 100;
    public static void main(String[] args) throws InterruptedException, IOException {
        BlockingQueue<CSVObject> queue = new LinkedBlockingQueue<>();
        CountDownLatch completed = new CountDownLatch(NUMTHREADS);
        AtomicInteger numOfFailRequest = new AtomicInteger();
        AtomicInteger numOfSuccessRequest = new AtomicInteger();
        String FILENAME = "result.csv";

        // call each thread and record run time
        long ProgramStartTime = System.currentTimeMillis();
        for (int i = 0; i < NUMTHREADS; i++){
            Runnable thread = new Implementation(completed, queue, numOfSuccessRequest, numOfFailRequest);
            new Thread(thread).start();
        }
        completed.await();
        long ProgramEndTime = System.currentTimeMillis();
        long ProgramWallTime = ProgramEndTime - ProgramStartTime;

        // write into csv file
        CSVWriter writer = new CSVWriter();
        writer.writeCSV(FILENAME, queue);

        // printout the run info
        int numOfTotalRequest = numOfSuccessRequest.intValue() + numOfFailRequest.intValue();
        System.out.println("Number of thread " + NUMTHREADS + " takes " + ProgramWallTime + " ms (" + (ProgramWallTime/1000.0) + "s)");
        System.out.println("Number of successful requests " + numOfSuccessRequest + " Number of failed requests " + numOfFailRequest);
        System.out.println("Throughput: (" + (numOfTotalRequest * 1.0) + "/" + (ProgramWallTime/1000.0) + "=) "
            + ((numOfTotalRequest)/(ProgramWallTime/1000.0)) + " requests/seconds");
        System.out.println("Each requests takes " + ((ProgramWallTime/1000.0)/(numOfTotalRequest)) + " seconds");
    }
}
