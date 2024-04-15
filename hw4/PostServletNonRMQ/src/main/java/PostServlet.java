import com.google.gson.Gson;
import model.LikesObject;
import model.PotentialMatchObject;
import model.SwipeObject;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(
        name = "PostServlet",
        value = {"/PostServlet"}
)
public class PostServlet extends HttpServlet {
    private Gson gson = new Gson();
    private boolean isUrlValid(String[] urlPath) {
        if (!urlPath[1].equals("swipe")) {
            return false;
        } else {
            return urlPath[2].equals("left") || urlPath[2].equals("right");
        }
    }

    private boolean isRequestBodyValid(SwipeObject body) {
        return Integer.parseInt(body.getSwiper()) >= 1 && Integer.parseInt(body.getSwiper()) <= 5000 && Integer.parseInt(body.getSwipee()) >= 1 && Integer.parseInt(body.getSwipee()) <= 1000000 && body.getComment().length() <= 256;
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();

        try {
            String urlPath = request.getPathInfo();
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = request.getReader();

            String line;
            while((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append(System.lineSeparator());
            }

            String data = buffer.toString();
            if (urlPath != null && !urlPath.isEmpty()) {
                String[] urlParts = urlPath.split("/");
                SwipeObject body = (SwipeObject)gson.fromJson(data, SwipeObject.class);
                body.setLikes(urlParts[2].equals("right"));
                if (this.isUrlValid(urlParts) && this.isRequestBodyValid(body)) {
                    response.setStatus(200);
                    response.getWriter().write("It works");
                    //likes
                    LikesObject likeObject = formatLikesData(body);
                    LikesDAO likesDao = new LikesDAO();
                    likesDao.createLike(likeObject);
                    // matches
                    if (body.isLikes()){
                        PotentialMatchObject potentialMatchObject = formatMatchesData(body);
                        MatchesDAO matchesDao = new MatchesDAO();
                        matchesDao.createMatch(potentialMatchObject);
                    }
                } else {
                    response.setStatus(400);
                    response.getWriter().write("invalid url path");
                }
            } else {
                response.setStatus(404);
                response.getWriter().write("missing parameters");
            }
        } catch (Exception var12) {
            var12.printStackTrace();
            throw new RuntimeException(var12);
        }
    }

    private LikesObject formatLikesData(SwipeObject swipeObject){
        LikesObject likesObject;
        String userId = swipeObject.getSwiper();
            likesObject = new LikesObject();
            likesObject.setSwiper(userId);
            if (swipeObject.isLikes()) {
                likesObject.setNumOfLikes(1);
                likesObject.setNumOfDislikes(0);
            } else {
                likesObject.setNumOfLikes(0);
                likesObject.setNumOfDislikes(1);
            }
        return likesObject;
    }

    private PotentialMatchObject formatMatchesData(SwipeObject swipeObject){
        //SwipeObject swipeObject = gson.fromJson(message, SwipeObject.class);
        String userId = swipeObject.getSwiper();
        String candidate = swipeObject.getSwipee();
        PotentialMatchObject potentialMatchObject;

        potentialMatchObject = new PotentialMatchObject();
        potentialMatchObject.setSwiper(userId);

        if (swipeObject.isLikes()){
            potentialMatchObject.setPotentialMatch(candidate);
        }
        return potentialMatchObject;
    }
}
