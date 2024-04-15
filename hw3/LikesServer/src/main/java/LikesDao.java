import model.LikesObject;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LikesDao {
    private static BasicDataSource dataSource;

    public LikesDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public LikesObject getLikes(String swiper) {
        LikesObject temp = new LikesObject(0,0);
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "SELECT numOfLikes, numOfDislikes FROM Likes WHERE swiper=?";
        ResultSet results = null;
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setString(1, swiper);
            // execute insert SQL statement
            results = preparedStatement.executeQuery();

            if(results.next()){
                int resultLikes = results.getInt("numOfLikes");
                int resultDislikes = results.getInt("numOfDislikes");

                temp.setNumLikes(resultLikes);
                temp.setNumDislikes(resultDislikes);
            }

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
        return temp;
    }
}
