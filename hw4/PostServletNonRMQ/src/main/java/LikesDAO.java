import model.LikesObject;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LikesDAO {
    private static BasicDataSource dataSource;

    public LikesDAO() {
        dataSource = DBCPDataSource.getDataSource();
    }

    /** One insertion**/
    public void createLike(LikesObject likeObject) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = null;
        //increment both
        if(likeObject.getNumOfLikes() > 0 && likeObject.getNumOfDislikes()>0) {
            insertQueryStatement = "INSERT INTO Likes (swiper, numOfLikes, numOfDislikes) VALUES (?,?,?)" +
                    "ON DUPLICATE KEY UPDATE " +
                    "numOfLikes = numOfLikes+1, " +
                    "numOfDisLikes = numOfDislikes+1";
        }
        //increment like
        if(likeObject.getNumOfLikes() >0 && likeObject.getNumOfDislikes() == 0) {
            insertQueryStatement = "INSERT INTO Likes (swiper, numOfLikes, numOfDislikes) VALUES (?,?,?)" +
                    "ON DUPLICATE KEY UPDATE " +
                    "numOfLikes = numOfLikes+1 ";
        }
        //increment dislike
        else {
            insertQueryStatement = "INSERT INTO Likes (swiper, numOfLikes, numOfDislikes) VALUES (?,?,?)" +
                    "ON DUPLICATE KEY UPDATE " +
                    "numOfDisLikes = numOfDislikes+1";
        }
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setString(1, likeObject.getSwiper());
            preparedStatement.setInt(2, likeObject.getNumOfLikes());
            preparedStatement.setInt(3, likeObject.getNumOfDislikes());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }
}
