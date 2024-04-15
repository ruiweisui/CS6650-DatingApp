import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MatchesDao {
    private static BasicDataSource dataSource;

    public MatchesDao() {
        dataSource = DBCPDataSource.getDataSource();
    }

    public String getMatchList(String swiper) {
        String matches = "";
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        String insertQueryStatement = "SELECT matches FROM Matches WHERE swiper=?";
        ResultSet results = null;

        try{
            conn = dataSource.getConnection();
            preparedStatement = conn.prepareStatement(insertQueryStatement);
            preparedStatement.setString(1, swiper);
            // execute insert SQL statement
            results = preparedStatement.executeQuery();

            if (results.next()){
                matches = results.getString("matches");
            }

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
        return matches;
    }

}
