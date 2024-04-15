import com.google.gson.Gson;
import model.PotentialMatchObject;
import model.SwipeObject;
import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MatchesDao {
    private static BasicDataSource dataSource;

    public MatchesDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public void createMatches(List<PotentialMatchObject> matchObjects) {
        Gson gson = new Gson();
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "INSERT INTO Matches (swiper, matches) VALUES (?,?)" +
                "ON DUPLICATE KEY UPDATE " +
                "matches = VALUES(matches)";
        try {
            conn = dataSource.getConnection();

            for (PotentialMatchObject matchObject:matchObjects){
                preparedStatement = conn.prepareStatement(insertQueryStatement);
                preparedStatement.setString(1, matchObject.getSwiper());
                preparedStatement.setString(2, gson.toJson(matchObject.getPotentialMatches()));
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
