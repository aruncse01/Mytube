
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool.Config;

public class ConnectionPool {
    public String driver = "com.mysql.jdbc.Driver";
    private String url_prefix = "jdbc:mysql://";
    public String hostUrl = "localhost/sampledb";
    public String defaultDBName = "";
    public String userName = "root";
    public String pwd = "";

    private GenericObjectPool connectionPool = null;

    public DataSource setUp() throws Exception {
        //
        // Load JDBC Driver class.
        //
        Class.forName(driver).newInstance();

        //
        // Creates an instance of GenericObjectPool that holds our
        // pool of connections object.
        //
        connectionPool = new GenericObjectPool();
        connectionPool.setMaxActive(10);
        
        //
        // Creates a connection factory object which will be use by
        // the pool to create the connection object. We passes the
        // JDBC url info, username and password.
        //
        ConnectionFactory cf = new DriverManagerConnectionFactory(
//        		url_prefix+hostUrl+"/"+defaultDBName,
        		url_prefix+hostUrl,
                userName,
                pwd);

        //
        // Creates a PoolableConnectionFactory that will wraps the
        // connection object created by the ConnectionFactory to add
        // object pooling functionality.
        //
        PoolableConnectionFactory pcf =
                new PoolableConnectionFactory(cf, connectionPool,
                        null, null, false, true);
        return new PoolingDataSource(connectionPool);
    }

    public GenericObjectPool getConnectionPool() {
        return connectionPool;
    }


    /**
     * Prints connection pool status.
     */
    private void printStatus() {
        System.out.println("Max   : " + getConnectionPool().getMaxActive() + "; " +
            "Active: " + getConnectionPool().getNumActive() + "; " +
            "Idle  : " + getConnectionPool().getNumIdle());
    }
}