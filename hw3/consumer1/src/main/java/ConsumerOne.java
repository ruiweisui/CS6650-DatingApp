import com.google.gson.Gson;
import com.rabbitmq.client.*;
import model.LikesObject;
import model.SwipeObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class ConsumerOne implements Runnable{

    private Gson gson = new Gson();
    private Connection connection;
    private ConcurrentHashMap<String, LikesObject> likesDict;
    private CountDownLatch completed;

    public ConsumerOne(Connection connection, ConcurrentHashMap<String, LikesObject> likesDict, CountDownLatch completed) {
        this.connection = connection;
        this.likesDict = likesDict;
        this.completed = completed;
    }

    private static final int BATCH_SIZE = 25;
    private static final String QUEUE_NAME = "likesQueue";
    private static final String EXCHANGE_NAME = "swipe";
    List<LikesObject> batchData = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void run() {
        try {
            final Channel channel = this.connection.createChannel();
            Map<String, Object> arguments = new HashMap<>();
            arguments.put("x-queue-type", "quorum");
            channel.queueDeclare(QUEUE_NAME, true, false, false, arguments);
            // max 25 message per receiver
            channel.basicQos(BATCH_SIZE);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "fanout");
            channel.basicConsume(QUEUE_NAME, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    formatLikesData(body);
                    // System.out.println("Received...");
                    if (batchData.size() >= BATCH_SIZE) {
                        // System.out.println("insert into DB...");
                        LikesDao likesDao = new LikesDao();
                        likesDao.createLikes(batchData);
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

    private void formatLikesData(byte[] body) throws IOException {
        try {
            LikesObject likesObject;
            String message = new String(body, StandardCharsets.UTF_8);
            // System.out.println(" [x] Received '" + message + "'");
            SwipeObject swipeObject = gson.fromJson(message, SwipeObject.class);
            String userId = swipeObject.getSwiper();
            // if swiper is already in the map
            if(likesDict.get(userId) != null){
                likesObject = likesDict.get(userId);
                if (swipeObject.isLikes()){
                    likesObject.incrementLikes();
                } else {
                    likesObject.incrementDislikes();
                }
                likesDict.put(userId, likesObject);
            // if swiper is not in the map
            } else {
                likesObject = new LikesObject();
                likesObject.setSwiper(userId);
                if (swipeObject.isLikes()) {
                    likesObject.setNumOfLikes(1);
                    likesObject.setNumOfDislikes(0);
                } else {
                    likesObject.setNumOfLikes(0);
                    likesObject.setNumOfDislikes(1);
                }
                likesDict.put(userId, likesObject);
            }
            batchData.add(likesObject);
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}