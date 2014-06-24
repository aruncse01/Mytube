

import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class ListenerSession implements HttpSessionListener {
	RDSManager rdsMgr = new RDSManager();
	Logger logger = LoggerFactory.getLogger(ListenerSession.class);
	public ListenerSession() {
	}

	public void sessionCreated(HttpSessionEvent sessionEvent) {
		HttpSession session = sessionEvent.getSession();
		try {
			//1. create RDS Instance
			Map<String, String> map = DataBaseManager.initRDSInstance();
			//2.Create DB Connection Pool&Data source(DBCP) 
			DataSource dataSource = DataBaseManager.initDBPooling(map);
			session.setAttribute(AbstractServlet.DBCP_DATA_SOURCE_SESSION_ATTRIBUTE_NAME, dataSource);
			logger.debug("DB connection pool been created");
			//3.Init DB
			String dbEndpoint = map.get("hostUrl");
			rdsMgr.initDB(dataSource,dbEndpoint);
			logger.debug("DB init ended");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		
	}
}