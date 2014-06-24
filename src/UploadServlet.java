

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author JD
 */
public class UploadServlet extends AbstractServlet {

	Logger logger = LoggerFactory.getLogger(UploadServlet.class);

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		String username = session.getAttribute("username").toString();
		DataSource dataSource = (DataSource) session
				.getAttribute(AbstractServlet.DBCP_DATA_SOURCE_SESSION_ATTRIBUTE_NAME);
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);

		if (isMultipart) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);

			try {
				List items = upload.parseRequest(request);
				Iterator iterator = items.iterator();
				while (iterator.hasNext()) {
					FileItem item = (FileItem) iterator.next();

					if (!item.isFormField()) {
						String fileName = item.getName();

						String root = getServletContext().getRealPath("/");
						File path = new File(root + "/uploads");
						if (!path.exists()) {
							boolean status = path.mkdirs();
						}

						// 1. save to local file system
						File uploadedFile = new File(path + "/" + fileName);
						item.write(uploadedFile);

						// 2. save to S3 bucket
						String bucketName = AWSManager.AWS_NAMESPACE + username;
						logger.debug("uploading file [] ......",
								uploadedFile.getAbsolutePath());
						s3Mgr.createObject(bucketName, new FileInputStream(
								uploadedFile), fileName);

						// 3. update rds database

						// 3.1 get stream_dist_id
						String sql = "use " + rdsMgr.defaultDataBaseName;
						rdsMgr.execute(dataSource, sql);
						
						sql = "select stream_dist_id from users where username = '"
								+ username + "'";
						String streamDistId = null;
						ResultSet rs = rdsMgr.executeQuery(dataSource, sql);
						try {
							if (rs.next()) {
								streamDistId = rs.getString("stream_dist_id");
							}
						} catch (SQLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// 3.2 insert video info in video table
						sql = "use " + rdsMgr.defaultDataBaseName;
						rdsMgr.execute(dataSource, sql);

						sql = "insert into videos VALUES ('"
								+ UUID.randomUUID().toString()
								+ "','"
								+ fileName
										.substring(
												fileName.lastIndexOf(File.separator) + 1,
												fileName.length()) + "',0,"+ "0,'"
								+ username + "','" + new Date().toString()
								+ "')";
						rdsMgr.execute(dataSource, sql);
						// 3.3 delete file in the local file system
						uploadedFile.delete();
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		logger.debug("upload complete");
		session.setAttribute("result", "Upload Complete");
		response.sendRedirect("upload.jsp");
	}
}
