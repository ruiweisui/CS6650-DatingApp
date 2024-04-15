import com.google.gson.Gson;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import model.RMQChannelFactory;
import model.RMQChannelPool;
import model.SwipeObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

@WebServlet(name = "HW2Servlet", value = "/HW2Servlet")
public class HW2Servlet extends HttpServlet {
    private static final String HOSTNAME = "52.13.127.99";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "admin";
    private static final String VIRTUALHOST = "broker";
    private static final int PORT = 5672;

    private static final String EXCHANGE_NAME = "swipe";
    public Connection connection;
    public RMQChannelPool pool;

    private boolean isUrlValid(String[] urlPath) {
        if (urlPath[1].equals("swipe")){
            return urlPath[2].equals("left") || urlPath[2].equals("right");
        }
        return false;
    }

    private boolean isRequestBodyValid(SwipeObject body){
        return Integer.parseInt(body.getSwiper()) >= 1 &&
                Integer.parseInt(body.getSwiper()) <= 5000 &&
                Integer.parseInt(body.getSwipee()) >= 1 &&
                Integer.parseInt(body.getSwipee()) <= 1000000 &&
                body.getComment().length() <= 256;
    }

    private boolean sendMessageToQueue(String message) {
        try {
            Channel channel = pool.borrowObject();
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true);
            channel.basicPublish(EXCHANGE_NAME, "fanout", null, message.getBytes());
            // System.out.println(" [x] Sent '" + message + "'");
            pool.returnObject(channel);
            return true;
        }catch (Exception e){
            System.out.println("error when sending message");
            return false;
        }
    }

    @Override
    public void init() throws ServletException{
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(HOSTNAME);
        factory.setVirtualHost(VIRTUALHOST);
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setPort(PORT);
        try {
            connection = factory.newConnection();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
        int MAX_SIZE = 20;
        RMQChannelFactory rmqChannelFactory = new RMQChannelFactory(connection);
        pool = new RMQChannelPool(MAX_SIZE, rmqChannelFactory);
    }

    @Override
    public void destroy(){
        try {
            connection.close();
            pool.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();

        try {
            // url
            String urlPath = request.getPathInfo();

            // request body
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append(System.lineSeparator());
            }
            String data = buffer.toString();

            if (urlPath == null || urlPath.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("missing parameters");
                return;
            }

            String[] urlParts = urlPath.split("/");
            SwipeObject body = gson.fromJson(data, SwipeObject.class);
            body.setLikes(urlParts[2].equals("right"));
            String message = gson.toJson(body);

            if (isUrlValid(urlParts) && isRequestBodyValid(body)) {
                if (sendMessageToQueue(message)){
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.getWriter().write("It works");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("invalid url path");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
