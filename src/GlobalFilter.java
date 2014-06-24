

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;




public class GlobalFilter implements Filter {
	Logger logger = LoggerFactory.getLogger(GlobalFilter.class);
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain filterChain) throws IOException, ServletException {
		HttpSession session = ((HttpServletRequest)req).getSession();
		DataSource dataSource = (DataSource) session.getAttribute(AbstractServlet.DBCP_DATA_SOURCE_SESSION_ATTRIBUTE_NAME);
		String username = (String) session.getAttribute("username");
		
		if(dataSource==null || username==null)
		{
			//1. create RDS Instance
			Map<String, String> map = DataBaseManager.initRDSInstance();
			//2.Create DB Connection Pool&Data source(DBCP) 
			dataSource = DataBaseManager.initDBPooling(map);
			session.setAttribute(AbstractServlet.DBCP_DATA_SOURCE_SESSION_ATTRIBUTE_NAME, dataSource);
			logger.debug("DB connection pool been created");
		}
		
		filterChain.doFilter(req, resp);
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}

}
