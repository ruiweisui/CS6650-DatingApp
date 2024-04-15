import com.google.gson.Gson;
import model.LikesObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "LikesServlet", value = "/LikesServlet")
public class LikesServlet extends HttpServlet {

    private boolean isUrlValid(String[] urlPath) {
        if (urlPath[1].equals("stats")){
            return Integer.parseInt(urlPath[2]) >= 1 && Integer.parseInt(urlPath[2]) <= 5000;
        }
        return false;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();

        try {
            // url
            String urlPath = request.getPathInfo();


            if (urlPath == null || urlPath.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("missing parameters");
                return;
            }

            String[] urlParts = urlPath.split("/");

            if (isUrlValid(urlParts)) {
                LikesDao likesDao = new LikesDao();
                LikesObject res = likesDao.getLikes(urlParts[2]);
                String message = gson.toJson(res);
                response.setStatus(HttpServletResponse.SC_OK);
                response.setContentType("application/json");
                response.getWriter().write(message);
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
