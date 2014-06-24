

import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DataBaseManager {

	static RDSManager rdsMgr = new RDSManager();
	static Logger logger = LoggerFactory.getLogger(DataBaseManager.class);
	
	public static Map<String, String> initRDSInstance()
	{
		Map<String, String> map = rdsMgr.createRDSInstance();
		logger.debug("rds db has been created");
		return map;
	}
	public static DataSource initDBPooling(Map<String, String> map)
	{
		
		//Create DB Connection Pool&Data source(DBCP) 
		ConnectionPool dbConnectionPool = new ConnectionPool();
		dbConnectionPool.defaultDBName = map.get("defaultDBName");
		dbConnectionPool.hostUrl = map.get("hostUrl");
		dbConnectionPool.userName = map.get("userName");
		dbConnectionPool.pwd = map.get("userPwd");
		DataSource dataSource  =null;
		try {
			dataSource = dbConnectionPool.setUp();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataSource;
	}
}
