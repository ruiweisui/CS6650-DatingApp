import model.LikesObject;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class LikesDao {
    private static BasicDataSource dataSource;

    public LikesDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public void createLikes(List<LikesObject> likesObjects) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO Likes (swiper, numOfLikes, numOfDislikes) VALUES (?,?,?)" +
                "ON DUPLICATE KEY UPDATE " +
                "numOfLikes = VALUES(numOfLikes), " +
                "numOfDisLikes = VALUES(numOfDislikes)";
        try {
            conn = dataSource.getConnection();

            for (LikesObject likeObject:likesObjects){
                preparedStatement = conn.prepareStatement(insertQueryStatement);
                preparedStatement.setString(1, likeObject.getSwiper());
                preparedStatement.setInt(2, likeObject.getNumOfLikes());
                preparedStatement.setInt(3, likeObject.getNumOfDislikes());
                preparedStatement.addBatch();
            }
            // execute insert SQL statement
            preparedStatement.executeBatch();
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
