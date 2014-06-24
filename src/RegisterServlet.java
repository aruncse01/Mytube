

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.cloudfront.model.CreateDistributionResult;
import com.amazonaws.services.cloudfront.model.CreateStreamingDistributionResult;
import com.amazonaws.services.cloudfront.model.Origin;
import com.amazonaws.services.cloudfront_2012_03_15.model.S3Origin;



public class RegisterServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;
	Logger logger = LoggerFactory.getLogger(RegisterServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String username = request.getParameter("user");
		String pass = request.getParameter("pass");
		HttpSession session = request.getSession();
		if (username == null | "".equals(username)) {
			username = UUID.randomUUID().toString();
			pass = UUID.randomUUID().toString();
		}

		// user already existed?
		DataSource dataSource = (DataSource) session
				.getAttribute(AbstractServlet.DBCP_DATA_SOURCE_SESSION_ATTRIBUTE_NAME);
		String sql = "use " + rdsMgr.defaultDataBaseName;
		rdsMgr.execute(dataSource, sql);
		sql = "select * from users where username = '" + username + "'";
		ResultSet rs = rdsMgr.executeQuery(dataSource, sql);

		try {
			if (rs != null && rs.next()) {
				session.setAttribute("result", "User already existed");
				logger.debug("Register failed:User already existed");
				response.sendRedirect("register.jsp");
				return;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// create s3 bucket
		String bucketName = AWSManager.AWS_NAMESPACE + username;
		s3Mgr.createBucket(bucketName);
		logger.debug("bucket [{}] has been created", bucketName);
		// create streaming distribution
		String s3BucketDomainName = bucketName + "."
				+ AWSManager.AWS_S3_END_POINT_NAME;
		CreateStreamingDistributionResult createStreamDistResult = cfMgr
				.createStreamDistribution(s3BucketDomainName);
		// create download distribution
		CreateDistributionResult createDownloadDistResult = cfMgr
				.createDownloadDistribution(bucketName, s3BucketDomainName, "");

		// insert user info into RDS DB
		String streamDistId = createStreamDistResult.getStreamingDistribution()
				.getId();
		String downLoadDistId = createDownloadDistResult.getDistribution()
				.getId();

		sql = "use " + rdsMgr.defaultDataBaseName;
		rdsMgr.execute(dataSource, sql);
		sql = "INSERT INTO users VALUES('" + username + "','" + pass + "','"
				+ streamDistId + "','" + downLoadDistId + "')";
		rdsMgr.execute(dataSource, sql);

		logger.debug("download distribution [{}] has been created",
				downLoadDistId);
		logger.debug("cloudfront streaming distribution [{}] has been created",
				streamDistId);

		session.setAttribute("username", username);
		session.setAttribute("result", "Register Complete");
		response.sendRedirect("index.jsp");
	}
}
