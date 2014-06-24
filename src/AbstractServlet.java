

import javax.servlet.http.HttpServlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class AbstractServlet extends HttpServlet {
	AmazonS3Manager s3Mgr = new AmazonS3Manager();
	RDSManager rdsMgr = new RDSManager();
	CloudFrontManager cfMgr = new CloudFrontManager();
	public static final String DBCP_CONNECTION_POOL_SESSION_ATTRIBUTE_NAME = "dbcp_connection_pool";
	public static final String DBCP_DATA_SOURCE_SESSION_ATTRIBUTE_NAME = "dbcp_data_source";
}
