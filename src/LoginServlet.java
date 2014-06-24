

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;
	Logger logger = LoggerFactory.getLogger(LoginServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		response.setContentType("text/html");
		String username = request.getParameter("username");
		String pass = request.getParameter("pass");
		
		
		
		//retrieve from RDS
		DataSource dataSource = (DataSource) session.getAttribute(AbstractServlet.DBCP_DATA_SOURCE_SESSION_ATTRIBUTE_NAME);
		String sql = "use " + rdsMgr.defaultDataBaseName;
		rdsMgr.execute(dataSource,sql);
		sql = "select * from users where username = '"+username+"' and pwd='"+pass+"'";
		ResultSet rs = rdsMgr.executeQuery(dataSource,sql);
		
		if(rs==null)
		{
			session.setAttribute("result", "No such user,please register first");
			response.sendRedirect("register.jsp");
			logger.debug("login failed:No such User");
		}
		boolean success = false;
		try {
			success = rs.next();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//redirect
		if(success)
		{
			session.setAttribute("username", username);
			session.setAttribute("result", "Login Complete");
			response.sendRedirect("index.jsp");
			logger.debug("login successfully");
		}
		else
		{
			session.setAttribute("result", "UserName or Password is wrong");
			response.sendRedirect("login.jsp");
			logger.debug("login failed");
		}
	}
}
