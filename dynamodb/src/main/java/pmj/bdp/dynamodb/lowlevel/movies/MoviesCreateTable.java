package pmj.bdp.dynamodb.lowlevel.movies;

import java.util.Arrays;

import pmj.bdp.dynamodb.dbutil.Dynamo;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeDefinition;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.KeySchemaElement;
import software.amazon.awssdk.services.dynamodb.model.KeyType;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;
import software.amazon.awssdk.services.dynamodb.model.ResourceInUseException;
import software.amazon.awssdk.services.dynamodb.model.ScalarAttributeType;
import software.amazon.awssdk.services.dynamodb.model.TableDescription;

public class MoviesCreateTable {

    public static void main(String[] args) {

        try (DynamoDbClient client = Dynamo.getClient() ) {

        	String tableName = "Movies1";
            try {
                System.out.println(
                    "Attempting to create table; please wait..."
                );

                CreateTableRequest request = CreateTableRequest.builder()
                    .tableName(tableName)
                    // Define the attributes used by the primary key
                    .attributeDefinitions(
                        Arrays.asList(
                        	AttributeDefinition.builder().attributeName("year").attributeType(ScalarAttributeType.N).build(),
                            AttributeDefinition.builder().attributeName("title").attributeType(ScalarAttributeType.S).build()
                        )
                    )

                    // Define the partition key and sort key
                    .keySchema(
                        Arrays.asList(
                            KeySchemaElement.builder().attributeName("year").keyType(KeyType.HASH).build(),
                            KeySchemaElement.builder().attributeName("title").keyType(KeyType.RANGE).build()
                        )
                    )

                    // Set provisioned throughput
                    .provisionedThroughput(
                        ProvisionedThroughput.builder().readCapacityUnits(10L).writeCapacityUnits(10L).build()
                    ).build();

                client.createTable(request);

                System.out.println(
                    "Table creation request submitted." 
                );

            } catch (ResourceInUseException e) {
                // Table already exists or is currently being created.
                // Ignore this exception and continue.
            }

            // Wait until the table exists and is ready
            client.waiter().waitUntilTableExists(
                DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build()
            );

            // Retrieve the table description
            TableDescription description = client.describeTable(
                DescribeTableRequest.builder()
                    .tableName(tableName)
                    .build()
            ).table();

            System.out.println(
                "Success. Table status: "
                + description.tableStatus()
            );

        } catch (Exception e) {
            System.err.println("Unable to create or verify table:");
            System.err.println(e.getMessage());
        }
    }
}