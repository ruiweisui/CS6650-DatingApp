import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import model.PotentialMatchObject;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class Main {
    private static final String HOSTNAME = "34.216.73.177";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String VIRTUALHOST = "broker";
    private static final int PORT = 5672;
    private static final int NUM_OF_THREAD = 20;

    public static ConcurrentHashMap<String, PotentialMatchObject> likesDict;

    public static void main(String[] argv) throws Exception {
        likesDict = new ConcurrentHashMap<>();
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOSTNAME);
        factory.setVirtualHost(VIRTUALHOST);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setPort(PORT);
        final Connection connection = factory.newConnection();

        CountDownLatch completed = new CountDownLatch(NUM_OF_THREAD);
        for (int i = 0; i < NUM_OF_THREAD; i++){
            Runnable thread = new ConsumerTwo(connection, likesDict, completed);
            new Thread(thread).start();
        }
        completed.await();
    }
}
