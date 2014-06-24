

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.cloudfront.model.Distribution;
import com.amazonaws.services.cloudfront.model.StreamingDistribution;

public class PlayVideoServlet extends AbstractServlet {

	Logger logger = LoggerFactory.getLogger(PlayVideoServlet.class);

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String videoFileName = (String) request.getParameter("video_file_name");
		String streamingDistId = (String) request
				.getParameter("stream_dist_id");
		String downloadDistId = (String) request
				.getParameter("download_dist_id");
		HttpSession session = request.getSession();

		// wait stram distirbution until deployed
		StreamingDistribution streamingDist = cfMgr
				.getStreamingDistribution(streamingDistId);
		String streamingDistributionStatus = streamingDist.getStatus();
		while (!streamingDistributionStatus.equals("Deployed")) {
			try {
				synchronized (this) {
					this.wait(5 * 1000);
				}
				streamingDist = cfMgr.getStreamingDistribution(streamingDistId);
				streamingDistributionStatus = streamingDist.getStatus();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("waiting until stream distribution deployed");
		}

		// wait download distirbution until deployed
		Distribution downloadDist = cfMgr
				.getDownloadDistribution(downloadDistId);
		String downloadDistStatus = downloadDist.getStatus();
		while (!downloadDistStatus.equals("Deployed")) {
			try {
				synchronized (this) {
					this.wait(5 * 1000);
				}
				downloadDist = cfMgr.getDownloadDistribution(downloadDistId);
				downloadDistStatus = downloadDist.getStatus();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.info("waiting until download distribution deployed");
		}

		String streamingDistributionDomainName = streamingDist.getDomainName();
		String downloadDistributionDomainName = downloadDist.getDomainName();

		session.setAttribute("streaming_distribution_domain_name",
				streamingDistributionDomainName);
		session.setAttribute("download_distribution_domain_name",
				downloadDistributionDomainName);
		session.setAttribute("video_file_name", videoFileName);
		response.sendRedirect("video.jsp");
	}
}
