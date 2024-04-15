import org.apache.commons.dbcp2.BasicDataSource;

public class DBCPDataSource {
    private static BasicDataSource dataSource;

     /*
     // NEVER store sensitive information below in plain text!
     private static final String HOST_NAME = System.getProperty("MySQL_IP_ADDRESS");
     private static final String PORT = System.getProperty("MySQL_PORT");
     private static final String DATABASE = "LiftRides";
     private static final String USERNAME = System.getProperty("DB_USERNAME");
     private static final String PASSWORD = System.getProperty("DB_PASSWORD");
      */

    private static final String HOST_NAME = "database-cs6650-sui.cyeytl7rscgk.us-west-2.rds.amazonaws.com";
    private static final String PORT = "3306";
    private static final String DATABASE = "Swipes";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "p4ssw0rd";

    static {
        // https://dev.mysql.com/doc/connector-j/8.0/en/connector-j-reference-jdbc-url-format.html
        dataSource = new BasicDataSource();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=UTC", HOST_NAME, PORT, DATABASE);
        dataSource.setUrl(url);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        dataSource.setInitialSize(10);
        dataSource.setMaxTotal(60);

//        //stacy database
//        String url = ("jdbc:mysql://localhost:3306/Swipes?enabledTLSProtocols=TLSv1.2&serverTimezone=UTC");
//        //String url = ("jdbc:mysql://shongdatabase.cwh56uhcrztm.us-west-2.rds.amazonaws.com:3306/Twinder?enabledTLSProtocols=TLSv1.2");
//        dataSource.setUrl(url);
//        dataSource.setUsername("root");
//        dataSource.setPassword("password");
//        dataSource.setInitialSize(10);
//        dataSource.setMaxTotal(60);
    }

    public static BasicDataSource getDataSource() {
        return dataSource;
    }
}