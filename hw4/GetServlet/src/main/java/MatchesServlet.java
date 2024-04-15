import com.google.gson.Gson;
import model.MatchObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebServlet(name = "MatchesServlet", value = "/MatchesServlet")
public class MatchesServlet extends HttpServlet {

    private boolean isUrlValid(String[] urlPath) {
        if (urlPath[1].equals("matches")){
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
                // get the json form of the list from DB
                MatchesDao matchesDao = new MatchesDao();
                String res = matchesDao.getMatchList(urlParts[2]);
                // convert to array list
                List<String> listRes = Arrays.asList(res.split("\\s*,\\s*"));
                // use to initialize match object
                MatchObject matchObject = new MatchObject(listRes);
                // convert to json form of match object
                String message = gson.toJson(matchObject);
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
