
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VideoManageServlet extends AbstractServlet {

	Logger logger = LoggerFactory.getLogger(VideoManageServlet.class);
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		this.doPost(req, resp);
	}

	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		String username = (String) session.getAttribute("username");
		String reqType = req.getParameter("req-type");
		String id = req.getParameter("video-id");
		if("rate".equals(reqType))
		{
			//get parameters
			Integer rating = Integer.parseInt(req.getParameter("rating"));
			Integer ratingCount = Integer.parseInt(req.getParameter("rating-count")) + 1;
			Integer originalRating = Integer.parseInt(req.getParameter("original-rating"));
			//calculate the sum of all ratings
			rating = originalRating + rating;
			
			DataSource dataSource = (DataSource) session.getAttribute(AbstractServlet.DBCP_DATA_SOURCE_SESSION_ATTRIBUTE_NAME);
			String sql = "use " + rdsMgr.defaultDataBaseName;
			rdsMgr.execute(dataSource, sql);
			sql = "UPDATE videos SET rating="+rating+", rating_count="+ratingCount+" where id = '"+id+"'";
			rdsMgr.execute(dataSource,sql);
		}
		else if("delete".equals(reqType))
		{
			DataSource dataSource = (DataSource) session.getAttribute(AbstractServlet.DBCP_DATA_SOURCE_SESSION_ATTRIBUTE_NAME);
			String sql = "use " + rdsMgr.defaultDataBaseName;
			rdsMgr.execute(dataSource, sql);
			sql = "DELETE FROM videos where id = '"+id+"'";
			rdsMgr.execute(dataSource,sql);
			session.setAttribute("result", "Deleting Video ["+id+"]Complete");
			resp.sendRedirect("list");
		}
		else
		{
			
		}
		
	}

}
