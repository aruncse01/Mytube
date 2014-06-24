

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.PropertiesCredentials;

/**
 * 
 * (1)S3 bucket for web site static resources and video files <br>
 * (2)EC2 for dynamic contents<br>
 * (3)CloudFront are using Amazon S3 or Amazon EC2 as an origin server
 * 
 * 
 * 
 * ref:
 * http://answers.oreilly.com/topic/2614-working-with-s3cloudfront-on-amazons
 * -web-services/<br>
 * http://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/
 * TutorialStreamingJWPlayer.html<br>
 * http://docs.aws.amazon.com/AmazonCloudFront/latest/DeveloperGuide/
 * WorkingWithDownloadDistributions.html<br>
*/
public class AWSManager implements Serializable{
	AWSCredentials credentials;
	public static String AWS_ACCOUNT_ID = "2688-8609-8778";
	public static String AWS_NAMESPACE = "videobuzz.";
	public static String AWS_S3_END_POINT_NAME = "s3.amazonaws.com";
	public static String AWS_RDS_END_POINT_NAME = "rds.us-east-1.amazonaws.com";
	public static String AWS_CLOUDFRONT_END_POINT_NAME = "cloudfront.amazonaws.com";
	public static String JW_PLAYER_BUCKET_NAME = "jw_player_bucket";
	Logger logger = LoggerFactory.getLogger(AWSManager.class);
	
	public AWSManager() {

		//read credential
		InputStream credentialsAsStream = Thread.currentThread()
				.getContextClassLoader()
				.getResourceAsStream("AwsCredentials.properties");
		try {
			credentials = new PropertiesCredentials(credentialsAsStream);
		} catch (IOException e) {

		}
	}

}
