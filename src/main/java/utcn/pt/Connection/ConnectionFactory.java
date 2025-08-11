package utcn.pt.Connection;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionFactory {
    private static final Logger LOGGER = Logger.getLogger(ConnectionFactory.class.getName());
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://localhost:3306/ordersdb?" +
            "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "futchapuk0727";

    private static ConnectionFactory singleInstance = new ConnectionFactory();

    private ConnectionFactory() {
        try{
            Class.forName(DRIVER);
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
    }

    private Connection createConnection(){
        Connection con = null;
        try{
            con = DriverManager.getConnection(DBURL,USER,PASS);
        }catch (SQLException e){
            e.printStackTrace();
        }
        return con;
    }

    public static Connection getConnection(){
        return singleInstance.createConnection();
    }

    public static void close(Statement stmt){
        if(stmt != null){
            try{
                stmt.close();
            }catch (SQLException e){
                LOGGER.log(Level.WARNING,"An error occured while trying to close the statement.");
            }
        }
    }

    public static void close(Connection con){
        if(con != null){
            try{
                con.close();
            }catch (SQLException e){
                LOGGER.log(Level.WARNING,"An error occured while trying to close the connection.");
            }
        }
    }

    public static void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                LOGGER.log(Level.WARNING, "An error occured while trying to close the ResultSet");
            }
        }
    }

}
