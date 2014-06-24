

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.amazonaws.services.rds.AmazonRDSClient;
import com.amazonaws.services.rds.model.AuthorizeDBSecurityGroupIngressRequest;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.CreateDBSecurityGroupRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DBInstanceNotFoundException;
import com.amazonaws.services.rds.model.DBSecurityGroupNotFoundException;
import com.amazonaws.services.rds.model.DescribeDBInstancesRequest;
import com.amazonaws.services.rds.model.DescribeDBInstancesResult;
import com.amazonaws.services.rds.model.DescribeDBSecurityGroupsRequest;
import com.amazonaws.services.rds.model.DescribeDBSecurityGroupsResult;



public class RDSManager extends AWSManager {
	AmazonRDSClient rdsClient;
	DBInstance dbInstance;
	public String defaultDataBaseName = "myTubeDB";

	public RDSManager() {
		rdsClient = new AmazonRDSClient(credentials);
		rdsClient.setEndpoint(AWS_RDS_END_POINT_NAME);
	}

	public void initDB(DataSource dataSource,String dbEndpoint) {
		// Create DataBase and Tables
		try {
			String sql = "create database IF NOT EXISTS " + defaultDataBaseName;
			execute(dataSource,sql);

			sql = "use " + defaultDataBaseName;
			execute(dataSource,sql);

			sql = "CREATE TABLE IF NOT EXISTS users (username VARCHAR(100),pwd VARCHAR(100),stream_dist_id VARCHAR(100),download_dist_id VARCHAR(100))";
			execute(dataSource,sql);

			sql = "CREATE TABLE IF NOT EXISTS videos (id VARCHAR(100),name VARCHAR(100),rating Int,rating_count Int,username VARCHAR(100),timestamp VARCHAR(100))";
			execute(dataSource,sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Map<String, String> createRDSInstance() {

		// 1.Create DB Security Group
		// 1.1 whether there existis DB Security Group with the same name
		String cIDRIP = "0.0.0.0/0";// allow every valid ipv4 ip to connect
		String groupName = "custom_security_group";
		DescribeDBSecurityGroupsResult descDBSgResult = null;
		try {
			descDBSgResult = rdsClient
					.describeDBSecurityGroups(new DescribeDBSecurityGroupsRequest()
							.withDBSecurityGroupName(groupName));
		} catch (DBSecurityGroupNotFoundException e2) {
			logger.debug(
					"DB Security Group {} does not exisit,create a new one",
					groupName);
		}
		// 1.2 DB Security Group does not exist,create a new one
		if (descDBSgResult == null) {

			// create security group
			rdsClient.createDBSecurityGroup(new CreateDBSecurityGroupRequest()
					.withDBSecurityGroupName(groupName)
					.withDBSecurityGroupDescription("created by Java."));
			logger.debug("DB Security Group {} has been created", groupName);
			// add ip to security group
			rdsClient
					.authorizeDBSecurityGroupIngress(new AuthorizeDBSecurityGroupIngressRequest()
							.withDBSecurityGroupName(groupName).withCIDRIP(
									cIDRIP));
			logger.debug("Add {} Ingress to DB Security Group {}", cIDRIP,
					groupName);
		}

		// 2.Create DB Instance
		String dbIdentifier = "RDS-DB-" + defaultDataBaseName;
		String defaultUserName = "root";
		String defaultUserPwd="rootroot";
		// 2.1 whether there existis DB Instance with the same name
		DescribeDBInstancesResult descDBResult = null;
		String dbEndpoint = null;
		try {
			descDBResult = rdsClient
					.describeDBInstances(new DescribeDBInstancesRequest()
							.withDBInstanceIdentifier(dbIdentifier));
			defaultUserName = descDBResult.getDBInstances().get(0).getMasterUsername();
		} catch (DBInstanceNotFoundException e1) {
			logger.debug("DB Instance {} does not exist,create a new one",
					dbIdentifier);
		}
		// 2.2 DB does not exist,create a new one
		if (descDBResult == null) {
			// create new DB instance
			dbInstance = rdsClient
					.createDBInstance(new CreateDBInstanceRequest()
							.withDBSecurityGroups(groupName)
							.withDBInstanceIdentifier(dbIdentifier)
							.withDBInstanceClass("db.t1.micro")
							.withMultiAZ(false)
							.withLicenseModel("general-public-license")
							.withAutoMinorVersionUpgrade(true)
							.withEngine("MySQL").withEngineVersion("5.5.27")
							.withAllocatedStorage(5).withMasterUsername(defaultUserName)
							.withMasterUserPassword(defaultUserPwd)
							.withDBName(defaultDataBaseName));
			logger.debug("DB Instance {} has been created", dbIdentifier);
		}

		// wait
		DescribeDBInstancesResult descDbInstanceResult = rdsClient
				.describeDBInstances(new DescribeDBInstancesRequest()
						.withDBInstanceIdentifier(dbIdentifier).withMaxRecords(
								100));
		String dbStatus = descDbInstanceResult.getDBInstances().get(0)
				.getDBInstanceStatus();
		while (!dbStatus.equals("available")) {
			try {
				synchronized (this) {
					this.wait(5 * 1000);
				}
				descDbInstanceResult = rdsClient
						.describeDBInstances(new DescribeDBInstancesRequest()
								.withDBInstanceIdentifier(dbIdentifier)
								.withMaxRecords(100));
				dbStatus = descDbInstanceResult.getDBInstances().get(0)
						.getDBInstanceStatus();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			logger.debug("waiting until db instance available");
		}
		
		dbEndpoint = descDbInstanceResult.getDBInstances().get(0).getEndpoint().getAddress();
		
		Map<String,String> returnMap = new HashMap<String, String>();
		returnMap.put("defaultDBName", defaultDataBaseName);
		returnMap.put("hostUrl", dbEndpoint);
		returnMap.put("userName", defaultUserName);
		returnMap.put("userPwd", defaultUserPwd);
		
		return returnMap;

	}


	public boolean execute(DataSource dataSource,String sql) {
		
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		boolean result = false;
		try {
			Statement statement = conn.createStatement();
			// Result set get the result of the SQL query
			result = statement.execute(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public ResultSet executeQuery(DataSource dataSource,String sql) {
		Connection conn = null;
		try {
			conn = dataSource.getConnection();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ResultSet resultSet = null;
		try {
			Statement statement = conn.createStatement();
			// Result set get the result of the SQL query
			resultSet = statement.executeQuery(sql);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		try {
//			conn.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return resultSet;
	}
}