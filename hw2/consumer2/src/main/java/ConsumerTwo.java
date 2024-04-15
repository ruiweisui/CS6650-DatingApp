import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import model.PotentialMatchObject;
import model.SwipeObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    private static final String QUEUE_NAME = "matchQueue";
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
                formatPotentialMatchData(delivery, channel);
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
            };
            channel.basicConsume(QUEUE_NAME, false, deliverCallback, consumerTag -> { });
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.completed.countDown();
    }

    private void formatPotentialMatchData(Delivery delivery, Channel channel) throws IOException {
        try {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            // System.out.println(" [x] Received '" + message + "'");
            SwipeObject swipeObject = gson.fromJson(message, SwipeObject.class);
            String userId = swipeObject.getSwiper();
            String candidate = swipeObject.getSwipee();
            // if swiper is already in the map
            if(likesDict.get(userId) != null){
                PotentialMatchObject existUserLikes = likesDict.get(userId);
                if (swipeObject.isLikes()){
                    existUserLikes.addToPotentialMatches(candidate);
                }
                likesDict.put(userId, existUserLikes);
            // if swiper is not in the map
            } else {
                PotentialMatchObject newUserLikes = new PotentialMatchObject();
                if (swipeObject.isLikes()) {
                    newUserLikes.addToPotentialMatches(candidate);
                }
                likesDict.put(userId, newUserLikes);
            }
        } catch (RuntimeException e){
            System.out.println(e.getMessage());
        }
    }
}