import io.swagger.client.ApiClient;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.SwipeApi;
import io.swagger.client.model.SwipeDetails;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class Implementation implements Runnable{

    final String LOCAL_URL = "http://localhost:8080/server-hw2_war_exploded/hwSui/";
    final String AWS_URL = "http://35.92.113.0:8080/server-hw2_war/hwSui/";
    final String SPRING_URL = "http://localhost:8080";
    final String AWS_SPRING_URL = "http://34.221.144.118:8080";
    final String LOAD_BALANCE_URL = "http://ServerLoadBalancer-1671705404.us-west-2.elb.amazonaws.com:8080/server-hw2_war/hwSui/";
    final int MAX_FAILS = 5;
    final static private int NUMREQUEST = 5000;

    AtomicInteger numOfFailRequest;
    AtomicInteger numOfSuccessRequest;
    CountDownLatch completed;
    ApiClient client;
    SwipeApi api;
    BlockingQueue<CSVObject> queue;
    int successfulRequestsPerThread;
    int failedRequestsPerThread;

    public Implementation(CountDownLatch completed, BlockingQueue<CSVObject> queue, AtomicInteger numOfSuccessRequest, AtomicInteger numOfFailRequest){
        this.completed = completed;
        this.client = new ApiClient();
        client.setBasePath(AWS_URL);
        this.api = new SwipeApi(client);
        this.queue = queue;
        this.numOfSuccessRequest = numOfSuccessRequest;
        this.numOfFailRequest = numOfFailRequest;
        this.successfulRequestsPerThread = 0;
        this.failedRequestsPerThread = 0;
    }

    /**
     * Runs this operation.
     */
    @Override
    public void run() {
        for (int i = 0; i < NUMREQUEST; i++){
            for (int tries = 1; tries <= MAX_FAILS; tries++){
                try{
                    // generate random value for a request
                    RequestGenerator request = new RequestGenerator();
                    SwipeDetails body = request.getBody();
                    String direction = request.getDirection();

                    // time the request
                    long startTime = System.currentTimeMillis();
                    ApiResponse<Void> response = api.swipeWithHttpInfo(body, direction);
                    long endTime = System.currentTimeMillis();

                    // add the details to queue
                    this.queue.add(new CSVObject(startTime, endTime, response.getStatusCode(), "POST"));

                    // rerun up to 5 times if fails
                    if (response.getStatusCode() == 200 || response.getStatusCode() == 201){
                        successfulRequestsPerThread += 1;
                        break;
                    } else {
                        int FailType = Integer.parseInt(Integer.toString(response.getStatusCode()).substring(0,1));
                        if (FailType == 4 || FailType == 5){
                            failedRequestsPerThread += 1;
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // add total number of successful and failed request per thread at the end
        numOfSuccessRequest.addAndGet(successfulRequestsPerThread);
        numOfFailRequest.addAndGet(failedRequestsPerThread);
        this.completed.countDown();
    }
}
