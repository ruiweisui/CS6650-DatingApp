import com.google.gson.Gson;
import model.PotentialMatchObject;
import model.SwipeObject;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MatchesDAO {
    private static BasicDataSource dataSource;

    public MatchesDAO() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public void createMatch(PotentialMatchObject matchObject) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO Matches (swiper, matches) VALUES (?,?)" +
                "ON DUPLICATE KEY UPDATE " +
                "matches = CONCAT_WS(',',matches,?)";
        try {
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setString(1, matchObject.getSwiper());
            preparedStatement.setString(2, matchObject.getPotentialMatch());
            preparedStatement.setString(3, matchObject.getPotentialMatch());
            // execute insert SQL statement
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
