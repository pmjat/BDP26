package pmj.bdp.dynamodb.dbutil;

import java.net.URI;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class Dynamo {

//	private static DynamoDbClient client;
	
	public static DynamoDbClient getClient() throws Exception {
		
        // Connect to DynamoDB Local
		
    	DynamoDbClient client = DynamoDbClient.builder()
            .endpointOverride(URI.create("http://localhost:8000"))
            .region(Region.US_WEST_2)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                		AwsBasicCredentials.create("1234","1234"))
            ).build();
    	
    	return client;
	}
}
