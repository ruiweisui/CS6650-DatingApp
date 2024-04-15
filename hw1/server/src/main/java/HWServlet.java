import com.google.gson.Gson;
import model.SwipeBody;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.BufferedReader;
import java.io.IOException;

@WebServlet(name = "HWServlet", value = "/HWServlet")
public class HWServlet extends HttpServlet {
    private boolean isUrlValid(String[] urlPath) {
        if (urlPath[1].equals("swipe")){
            return urlPath[2].equals("left") || urlPath[2].equals("right");
        }
        return false;
    }

    private boolean isRequestBodyValid(SwipeBody body){
        if (Integer.parseInt(body.getSwiper()) >= 1 &&
                Integer.parseInt(body.getSwiper()) <= 5000 &&
                Integer.parseInt(body.getSwipee()) >= 1 &&
                Integer.parseInt(body.getSwipee()) <= 1000000 &&
                body.getComment().length() == 256
        ){
            return true;
        }
        return false;
    }

    private void processRequest(HttpServletResponse response, HttpServletRequest request) throws ServletException, IOException {
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
            SwipeBody body = gson.fromJson(data, SwipeBody.class);

            if (isUrlValid(urlParts) && isRequestBodyValid(body)) {
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("It works");
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("invalid url path");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // processRequest(response, request);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(response, request);
    }

}
