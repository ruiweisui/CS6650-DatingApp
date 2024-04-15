import com.google.gson.Gson;
import com.rabbitmq.client.*;
import model.PotentialMatchObject;
import model.SwipeObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class ConsumerTwo implements Runnable{

    private Gson gson = new Gson();
    private Connection connection;
    private ConcurrentHashMap<String, PotentialMatchObject> likesDict;
    private CountDownLatch completed;

    public ConsumerTwo(Connection connection, ConcurrentHashMap<String, PotentialMatchObject> likesDict, CountDownLatch completed) {
        this.connection = connection;
        this.likesDict = likesDict;
        this.completed = completed;
    }

    private static final int BATCH_SIZE = 25;
    private static final String QUEUE_NAME = "matchQueue";
    private static final String EXCHANGE_NAME = "swipe";
    List<PotentialMatchObject> batchData = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void run() {
        try {
            final Channel channel = this.connection.createChannel();
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-queue-type", "quorum");
            channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
            // max one message per receiver
            channel.basicQos(BATCH_SIZE);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "fanout");
            channel.basicConsume(QUEUE_NAME, false, new DefaultConsumer(channel){
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    formatPotentialMatchData(body);
                    if (batchData.size() >= BATCH_SIZE) {
                        MatchesDao matchesDao = new MatchesDao();
                        matchesDao.createMatches(batchData);
                        channel.basicAck(envelope.getDeliveryTag(), true);
                        batchData.clear();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.completed.countDown();
    }

    private void formatPotentialMatchData(byte[] body) throws IOException {
        try {
            String message = new String(body, StandardCharsets.UTF_8);
            // System.out.println(" [x] Received '" + message + "'");
            SwipeObject swipeObject = gson.fromJson(message, SwipeObject.class);
            String userId = swipeObject.getSwiper();
            String candidate = swipeObject.getSwipee();
            PotentialMatchObject potentialMatchObject;
            // if swiper is already in the map
            if(likesDict.get(userId) != null){
                potentialMatchObject = likesDict.get(userId);
                // if swiper is not in the map
            } else {
                potentialMatchObject = new PotentialMatchObject();
                potentialMatchObject.setSwiper(userId);
            }
            if (swipeObject.isLikes()){
                potentialMatchObject.addToPotentialMatches(candidate);
            }
            likesDict.put(userId, potentialMatchObject);
            batchData.add(potentialMatchObject);
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}