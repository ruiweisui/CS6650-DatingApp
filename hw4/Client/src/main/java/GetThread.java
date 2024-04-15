import io.swagger.client.ApiClient;
import io.swagger.client.ApiException;
import io.swagger.client.ApiResponse;
import io.swagger.client.api.MatchesApi;
import io.swagger.client.api.StatsApi;
import io.swagger.client.model.MatchStats;
import io.swagger.client.model.Matches;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;

public class GetThread implements Runnable{

    CountDownLatch completed;
    List<Long> list;

    final String MatchURL = "http://34.219.169.8:8080/MatchesServer_war/Twinder";
    ApiClient matchClient;
    MatchesApi matchApi;

    final String StatsURL = "http://52.40.239.233:8080/LikesServer_war/Twinder";
    ApiClient statsClient;
    StatsApi statsApi;

    public GetThread(CountDownLatch completed, List<Long> getList){
        this.completed = completed;
        this.list = getList;
        this.matchClient = new ApiClient();
        matchClient.setBasePath(MatchURL);
        this.matchApi = new MatchesApi(matchClient);
        this.statsClient = new ApiClient();
        statsClient.setBasePath(StatsURL);
        this.statsApi = new StatsApi(statsClient);
    }

    @Override
    public void run() {
        while (this.completed.getCount() > 1){
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            try {
                int swiper = ThreadLocalRandom.current().nextInt(1, 5001);
                int operation = ThreadLocalRandom.current().nextInt(0, 2);
                long startTime = System.currentTimeMillis();
                getRequest(swiper, operation);
                long totalTime = System.currentTimeMillis() - startTime;
                list.add(totalTime);

            } catch (ApiException e) {
                throw new RuntimeException(e);
            }
        }
        this.completed.countDown();
    }

    private void getRequest(int swiper, int operation) throws ApiException {
        if (operation == 0){
            ApiResponse<Matches> response1 = matchApi.matchesWithHttpInfo(String.valueOf(swiper));
        } else {
            ApiResponse<MatchStats> response2 = statsApi.matchStatsWithHttpInfo(String.valueOf(swiper));
        }

    }
}
