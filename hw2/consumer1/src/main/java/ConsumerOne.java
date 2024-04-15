import com.google.gson.Gson;
import com.rabbitmq.client.*;
import model.LikesObject;
import model.SwipeObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    private static final String QUEUE_NAME = "likesQueue";
    private static final String EXCHANGE_NAME = "swipe";

    @Override
    public void run() {
        try {
            final Channel channel = this.connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            // max one message per receiver
            channel.basicQos(1);

            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, "fanout");
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                formatLikesData(delivery, channel);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.completed.countDown();
    }

    private void formatLikesData(Delivery delivery, Channel channel) throws IOException {
        try {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            // System.out.println(" [x] Received '" + message + "'");
            SwipeObject swipeObject = gson.fromJson(message, SwipeObject.class);
            String userId = swipeObject.getSwiper();
            // if swiper is already in the map
            if(likesDict.get(userId) != null){
                LikesObject existUserLikes = likesDict.get(userId);
                if (swipeObject.isLikes()){
                    existUserLikes.incrementLikes();
                } else {
                    existUserLikes.incrementDislikes();
                }
                likesDict.put(userId, existUserLikes);
            // if swiper is not in the map
            } else {
                LikesObject newUserLikes = new LikesObject();
                newUserLikes.setSwiper(userId);
                if (swipeObject.isLikes()) {
                    newUserLikes.setNumOfLikes(1);
                    newUserLikes.setNumOfDislikes(0);
                } else {
                    newUserLikes.setNumOfLikes(0);
                    newUserLikes.setNumOfDislikes(1);
                }
                likesDict.put(userId, newUserLikes);
            }
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}