

import java.util.UUID;

import com.amazonaws.services.cloudfront.AmazonCloudFrontClient;
import com.amazonaws.services.cloudfront.model.Aliases;
import com.amazonaws.services.cloudfront.model.CacheBehaviors;
import com.amazonaws.services.cloudfront.model.CloudFrontOriginAccessIdentityConfig;
import com.amazonaws.services.cloudfront.model.CookiePreference;
import com.amazonaws.services.cloudfront.model.CreateCloudFrontOriginAccessIdentityRequest;
import com.amazonaws.services.cloudfront.model.CreateCloudFrontOriginAccessIdentityResult;
import com.amazonaws.services.cloudfront.model.CreateDistributionRequest;
import com.amazonaws.services.cloudfront.model.CreateDistributionResult;
import com.amazonaws.services.cloudfront.model.CreateStreamingDistributionRequest;
import com.amazonaws.services.cloudfront.model.CreateStreamingDistributionResult;
import com.amazonaws.services.cloudfront.model.DefaultCacheBehavior;
import com.amazonaws.services.cloudfront.model.Distribution;
import com.amazonaws.services.cloudfront.model.DistributionConfig;
import com.amazonaws.services.cloudfront.model.ForwardedValues;
import com.amazonaws.services.cloudfront.model.GetDistributionRequest;
import com.amazonaws.services.cloudfront.model.GetDistributionResult;
import com.amazonaws.services.cloudfront.model.GetStreamingDistributionRequest;
import com.amazonaws.services.cloudfront.model.ItemSelection;
import com.amazonaws.services.cloudfront.model.LoggingConfig;
import com.amazonaws.services.cloudfront.model.Origin;
import com.amazonaws.services.cloudfront.model.Origins;
import com.amazonaws.services.cloudfront.model.S3Origin;
import com.amazonaws.services.cloudfront.model.S3OriginConfig;
import com.amazonaws.services.cloudfront.model.StreamingDistribution;
import com.amazonaws.services.cloudfront.model.StreamingDistributionConfig;
import com.amazonaws.services.cloudfront.model.StreamingLoggingConfig;
import com.amazonaws.services.cloudfront.model.TrustedSigners;

public class CloudFrontManager extends AWSManager {
	AmazonCloudFrontClient cfClient;

	public CloudFrontManager() {
		cfClient = new AmazonCloudFrontClient(credentials);
		cfClient.setEndpoint(AWS_CLOUDFRONT_END_POINT_NAME);
	}

	/**
	 * Create a new CloudFront Download Distribution
	 */
	public CreateDistributionResult createDownloadDistribution(
			String bucketName, String domainName, String rootObject) {

		// prepare
		String callerReference = UUID.randomUUID().toString();
		CreateCloudFrontOriginAccessIdentityResult createCloudFrontOriginAccessIdentityResult = cfClient
				.createCloudFrontOriginAccessIdentity(new CreateCloudFrontOriginAccessIdentityRequest()
						.withCloudFrontOriginAccessIdentityConfig(new CloudFrontOriginAccessIdentityConfig()
								.withCallerReference(callerReference)
								.withComment(
										"createCloudFrontOriginAccessIdentity")));
		String originAccessIdentity = createCloudFrontOriginAccessIdentityResult
				.getCloudFrontOriginAccessIdentity().getId();

		String s3OriginId = "S3-" + bucketName;
		Origin s3Origin = new Origin()
				.withDomainName(domainName)
				.withId(s3OriginId)
				.withS3OriginConfig(
						new S3OriginConfig()
								.withOriginAccessIdentity("origin-access-identity/cloudfront/"
										+ originAccessIdentity));

		// create
		CreateDistributionResult result = cfClient
				.createDistribution(new CreateDistributionRequest(
						new DistributionConfig()
								.withDefaultCacheBehavior(
										new DefaultCacheBehavior()
												.withForwardedValues(
														new ForwardedValues()
																.withCookies(
																		new CookiePreference()
																				.withForward(ItemSelection.All))
																.withQueryString(
																		false))
												.withTrustedSigners(
														new TrustedSigners()
																.withQuantity(0)
																.withEnabled(
																		false))
												.withViewerProtocolPolicy(
														"allow-all")
												.withMinTTL(0l)
												.withTargetOriginId(s3OriginId))
								.withCacheBehaviors(
										new CacheBehaviors().withQuantity(0))
								.withLogging(
										new LoggingConfig()
												.withIncludeCookies(false)
												.withBucket(domainName)
												.withEnabled(false)
												.withPrefix(domainName))
								.withAliases(new Aliases().withQuantity(0))

								.withComment(
										"Download Distribution created by java")
								.withCallerReference(
										AWS_NAMESPACE
												+ UUID.randomUUID().toString())
								.withEnabled(true)
								.withPriceClass("PriceClass_100")
								.withDefaultRootObject(rootObject)
								.withEnabled(true)
								.withOrigins(
										new Origins().withQuantity(1)
												.withItems(s3Origin))));

		return result;
	}

	public StreamingDistribution getStreamingDistribution(String streamingDistId) {
		StreamingDistribution streamingDist = cfClient
				.getStreamingDistribution(
						new GetStreamingDistributionRequest()
								.withId(streamingDistId))
				.getStreamingDistribution();

		return streamingDist;
	}
	
	public Distribution getDownloadDistribution(String downloadDistId)
	{
		GetDistributionResult downloadDistResult = cfClient.getDistribution(new GetDistributionRequest().withId(downloadDistId));
		return downloadDistResult.getDistribution();
	}

	public CreateStreamingDistributionResult createStreamDistribution(
			String domainName) {
		String callerReference = UUID.randomUUID().toString();
		CreateCloudFrontOriginAccessIdentityResult createCloudFrontOriginAccessIdentityResult = cfClient
				.createCloudFrontOriginAccessIdentity(new CreateCloudFrontOriginAccessIdentityRequest()
						.withCloudFrontOriginAccessIdentityConfig(new CloudFrontOriginAccessIdentityConfig()
								.withCallerReference(callerReference)
								.withComment(
										"createCloudFrontOriginAccessIdentity")));
		String originAccessIdentity = createCloudFrontOriginAccessIdentityResult
				.getCloudFrontOriginAccessIdentity().getId();

		// create
		CreateStreamingDistributionResult result = cfClient
				.createStreamingDistribution(new CreateStreamingDistributionRequest(
						new StreamingDistributionConfig()
								.withAliases(new Aliases().withQuantity(0))
								.withLogging(
										new StreamingLoggingConfig()
												.withBucket(domainName)
												.withEnabled(false)
												.withPrefix(domainName))
								.withTrustedSigners(
										new TrustedSigners().withQuantity(0)
												.withEnabled(false))
								.withComment(
										"Stream Distribution created by java")
								.withCallerReference(
										AWS_NAMESPACE
												+ UUID.randomUUID().toString())
								.withEnabled(true)
								.withPriceClass("PriceClass_100")
								.withS3Origin(
										new S3Origin(domainName)
												.withOriginAccessIdentity("origin-access-identity/cloudfront/"
														+ originAccessIdentity))));

		return result;
	}
}
