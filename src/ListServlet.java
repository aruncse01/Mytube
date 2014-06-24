

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ListServlet extends AbstractServlet {
	Logger logger = LoggerFactory.getLogger(ListServlet.class);

	public class VideoObject implements Serializable {
		public String id;
		public String userName;
		public String streamingDistributionId;
		public String videoName;
		public String rating;
		public String ratingCount;
		public String averageRating;
		public String timestamp;
		public String downLoadDistributionId;

		public String getDownLoadDistributionId() {
			return downLoadDistributionId;
		}

		public void setDownLoadDistributionId(String downLoadDistributionId) {
			this.downLoadDistributionId = downLoadDistributionId;
		}

		public String getAverageRating() {
			return averageRating;
		}

		public void setAverageRating(String averageRating) {
			this.averageRating = averageRating;
		}

		public String getRatingCount() {
			return ratingCount;
		}

		public void setRatingCount(String ratingCount) {
			this.ratingCount = ratingCount;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		public String getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(String timestamp) {
			this.timestamp = timestamp;
		}

		public String getRating() {
			return rating;
		}

		public void setRating(String rating) {
			this.rating = rating;
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getStreamingDistributionId() {
			return streamingDistributionId;
		}

		public void setStreamingDistributionId(String streamingDistributionId) {
			this.streamingDistributionId = streamingDistributionId;
		}

		public String getVideoName() {
			return videoName;
		}

		public void setVideoName(String videoName) {
			this.videoName = videoName;
		}

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession session = req.getSession();
		String username = (String) session.getAttribute("username");

		// get videos list
		DataSource dataSource = (DataSource) session
				.getAttribute(AbstractServlet.DBCP_DATA_SOURCE_SESSION_ATTRIBUTE_NAME);
		String sql = "use " + rdsMgr.defaultDataBaseName;
		rdsMgr.execute(dataSource, sql);
		
		
		if(username!=null)
		{
		sql = "select users.stream_dist_id,users.username,users.download_dist_id,videos.id,videos.name,videos.rating,videos.rating_count,videos.timestamp from users,videos where users.username = videos.username and users.username = '"
				+ username
				+ "' ORDER BY (videos.rating/videos.rating_count) DESC";
		}
		else
		{
			sql = "select users.stream_dist_id,users.username,users.download_dist_id,videos.id,videos.name,videos.rating,videos.rating_count,videos.timestamp from users,videos where users.username = videos.username ORDER BY (videos.rating/videos.rating_count) DESC";
		}
		
		ResultSet rs = rdsMgr.executeQuery(dataSource, sql);
		List<VideoObject> videoList = new ArrayList<VideoObject>();
		try {
			while (rs!=null&&rs.next()) {
				VideoObject vo = new VideoObject();
				vo.id = rs.getString("id");
				vo.userName = rs.getString("username");
				vo.streamingDistributionId = rs.getString("stream_dist_id");
				vo.downLoadDistributionId = rs.getString("download_dist_id");
				vo.videoName = rs.getString("name");
				vo.rating = rs.getString("rating");
				vo.ratingCount = rs.getString("rating_count");
				if (Integer.parseInt(vo.ratingCount) == 0) {
					vo.averageRating = "0";
				} else {
					vo.averageRating = String.valueOf(Integer
							.parseInt(vo.rating)
							/ Integer.parseInt(vo.ratingCount));
				}
				vo.timestamp = rs.getString("timestamp");
				videoList.add(vo);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		session.setAttribute("videoList", videoList);
		if(username!=null)
		{
		RequestDispatcher dispatcher = req
				.getRequestDispatcher("listVideo.jsp");// dispatch without
														// changing the url in
														// the address bar
		dispatcher.forward(req, resp);
		}
		else
		{
			RequestDispatcher dispatcher = req
					.getRequestDispatcher("toprated.jsp");// dispatch without
															// changing the url in
															// the address bar
			dispatcher.forward(req, resp);
		}

	}
}