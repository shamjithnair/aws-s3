package com.mobica.adapt.aws.poc;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@SpringBootApplication
public class AwsPocApplication {

    @Value("${AWS_REGION}")
    private String awsRegion;

    @Value("${AWS_ENDPOINT:#{null}}")
    private String awsEndpoint;

	public static void main(String[] args) {
		SpringApplication.run(AwsPocApplication.class, args);
	}

    /**
     * AWS S3 Client configuration
     * @param endpointConfiguration
     * @return
     */
	@Bean
    public AmazonS3 amazonS3(AwsClientBuilder.EndpointConfiguration endpointConfiguration) {
        return AmazonS3ClientBuilder.standard()
                .withPathStyleAccessEnabled(true)
                .withEndpointConfiguration(endpointConfiguration)
                .build();
    }

    /**
     * S3 Endpoint configuration
     * When AWS_ENDPOINT variable not provided - threat as AWS
     * When AWS_ENDPOINT variable provided - threat as Minio
     * @return
     */
	@Bean
    public AwsClientBuilder.EndpointConfiguration endpointConfiguration() {
        if (StringUtils.isNullOrEmpty(awsEndpoint)) {
            return null;
        } else {
            return new AwsClientBuilder.EndpointConfiguration(awsEndpoint, awsRegion);
        }
    }

}

@RestController
class TestController {

    private static final String bucketName     = "czujnikbucket";
    private static final String keyName        = "hosts";
    private static final String uploadFileName = "/etc/hosts";


    @Autowired
    AmazonS3 amazonS3;

    /**
     * Endpoint to upload file to S3 to specified bucket
     * Bucket, file location and key name hardcoded
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET, path = "/upload")
    public String uploadFile() throws IOException {
        String response = "Error";

        try {
            System.out.println("Uploading a new object to S3 from a file\n");
            File file = new File(uploadFileName);
            // Upload file
            amazonS3.putObject(new PutObjectRequest(bucketName, keyName, file));
            response = "OK";
        } catch (AmazonServiceException ase) {
            printAWSException(ase);

        } catch (AmazonClientException ace) {
            printAWSClientException(ace);

        }

        return response;
    }

    /**
     * Endpoint to download file from S3 from specified bucket
     * Bucket, file location and key name hardcoded
     * @return
     * @throws IOException
     */
    @RequestMapping(method = RequestMethod.GET)
	public String obtainFile() throws IOException {
        String response = "Error";

		try {
			// Download file
            System.out.println("Downloading file from S3 to file\n");
			S3Object objectPortion = amazonS3.getObject(new GetObjectRequest(bucketName, keyName));
			System.out.println("Printing bytes retrieved:");
            response = displayTextInputStream(objectPortion.getObjectContent());
            System.out.println(response);
		} catch (AmazonServiceException ase) {
            printAWSException(ase);

		} catch (AmazonClientException ace) {
            printAWSClientException(ace);

		}

		return response;
	}

	private static String displayTextInputStream(InputStream input) throws IOException {
        return IOUtils.toString(input);
	}

    private void printAWSClientException(AmazonClientException ace) {
        System.out.println("Caught an AmazonClientException, which " +
                "means the client encountered " +
                "an internal error while trying to " +
                "communicate with S3, " +
                "such as not being able to access the network.");
        System.out.println("Error Message: " + ace.getMessage());
    }

    private void printAWSException(AmazonServiceException ase) {
        System.out.println("Caught an AmazonServiceException, which " +
                "means your request made it " +
                "to Amazon S3, but was rejected with an error response" +
                " for some reason.");
        System.out.println("Error Message:    " + ase.getMessage());
        System.out.println("HTTP Status Code: " + ase.getStatusCode());
        System.out.println("AWS Error Code:   " + ase.getErrorCode());
        System.out.println("Error Type:       " + ase.getErrorType());
        System.out.println("Request ID:       " + ase.getRequestId());
    }
}