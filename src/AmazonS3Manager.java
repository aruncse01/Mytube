

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.amazonaws.auth.policy.Policy;
import com.amazonaws.auth.policy.Principal;
import com.amazonaws.auth.policy.Resource;
import com.amazonaws.auth.policy.Statement;
import com.amazonaws.auth.policy.Statement.Effect;
import com.amazonaws.auth.policy.actions.S3Actions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringUtils;


public class AmazonS3Manager extends AWSManager {

	AmazonS3 s3Client = new AmazonS3Client(credentials);

	
	public AmazonS3Manager() {
		s3Client.setEndpoint(AWS_S3_END_POINT_NAME);
	}

	/**
	 * This gets a list of Buckets that you own. This also prints out the bucket
	 * name and creation date of each bucket.
	 * 
	 * @return
	 */
	public List<Bucket> listBuckets() {
		List<Bucket> buckets = s3Client.listBuckets();
		for (Bucket bucket : buckets) {
			System.out.println(bucket.getName() + '\t'
					+ StringUtils.fromDate(bucket.getCreationDate()));
		}
		return buckets;
	}
	

	public void createBucket(String name) {
		//create bucket
		s3Client.createBucket(name);
		//access policy
		Statement bucketAccessStmt = new Statement(Effect.Allow);
		ArrayList<Statement> stmtList = new ArrayList<Statement>();
		stmtList.add(bucketAccessStmt);
		Policy bucketAccessPolicy = new Policy("policy.s3.cs9223.poly.edu"+UUID.randomUUID().toString(),stmtList);
		bucketAccessStmt.withId("statement.s3.cs9223.poly.edu"+UUID.randomUUID().toString()).withActions(S3Actions.AllS3Actions).withResources(new Resource("arn:aws:s3:::"+name+"/*")).withPrincipals(new Principal(AWS_ACCOUNT_ID));
		// set policy
		s3Client.setBucketPolicy(name, bucketAccessPolicy.toJson());
		
	}

	/**
	 * This gets a list of objects in the bucket. This also prints out each
	 * object’s name, the file size, and last modified date.
	 * 
	 * @param name
	 */
	public void listBucketContents(String name) {
		ObjectListing objects = s3Client.listObjects(name);
		do {
			for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
				System.out
						.println(objectSummary.getKey()
								+ '\t'
								+ objectSummary.getSize()
								+ '\t'
								+ StringUtils.fromDate(objectSummary
										.getLastModified()));
			}
			objects = s3Client.listNextBatchOfObjects(objects);
		} while (objects.isTruncated());
	}

	public void delteBucket(String name) {
		s3Client.deleteBucket(name);
	}

	/**
	 * This creates a file [filename] with the content in Bucket[bucketName]
	 * 
	 * @param bucketName
	 * @param fileContent
	 * @param fileName
	 */
	public void createObject(String bucketName, InputStream fileContent,
			String fileName) {
		PutObjectResult result = s3Client.putObject(bucketName, fileName, fileContent,
				new ObjectMetadata());

		// This makes the object to be publicly readable
		s3Client.setObjectAcl(bucketName, fileName,
				CannedAccessControlList.PublicRead);
	}

	public void deleteObject(String bucketName, String fileName) {
		s3Client.deleteObject(bucketName, fileName);
	}

	/**
	 * This generates an unsigned download URL for [fileName]. This works
	 * because we made [fileName] public by setting the ACL above. This then
	 * generates a signed download URL for secret_plans.txt that will work for 1
	 * hour. Signed download URLs will work for the time period even if the
	 * object is private (when the time period is up, the URL will stop
	 * working).
	 * 
	 * @return
	 */
	public String getObject(String bucketName, String fileName) {
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(
				bucketName, fileName);
		String url = s3Client.generatePresignedUrl(request).toString();
		return url;
	}
}