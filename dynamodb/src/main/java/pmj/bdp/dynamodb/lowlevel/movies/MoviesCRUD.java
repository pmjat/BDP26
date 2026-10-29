package pmj.bdp.dynamodb.lowlevel.movies;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import pmj.bdp.dynamodb.dbutil.Dynamo;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.PutItemResponse;
import software.amazon.awssdk.services.dynamodb.model.ReturnValue;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemRequest;
import software.amazon.awssdk.services.dynamodb.model.UpdateItemResponse;

public class MoviesCRUD {

    private static final String TABLE_NAME = "Movies";

    public static void main(String[] args) {

            try (DynamoDbClient client = Dynamo.getClient() ) {

            // C: Create
            createMovie(client);

            // R: Read
            getMovie(client);

            // U: Update
            updateMovieRating(client);

            // R: Read again to verify the update
            getMovie(client);

            // D: Delete
            deleteMovie(client);

        } catch (Exception e) {
            System.err.println("DynamoDB operation failed.");
            System.err.println(e.getMessage());
        }
    }

    // C: CREATE
    private static void createMovie(DynamoDbClient client) {

		// Define Primary key
		Map<String, AttributeValue> item = new HashMap<>();
		item.put("year", numberValue( 2013 ));
		item.put("title", stringValue( "Prisoners" ));

        // Nested info map
        Map<String, AttributeValue> info = new HashMap<>();

        info.put(
                "directors",
                AttributeValue.builder().l(Arrays.asList( // L for List
                                stringValue("Denis Villeneuve"))).build());
        info.put("release_date",stringValue("2013-08-30T00:00:00Z"));
        info.put("rating", numberValue(8.2));
        info.put(
                "genres",
                AttributeValue.builder()
                        .l(Arrays.asList(stringValue("Crime"),
                        	stringValue("Drama"),stringValue("Mystery"))).build());

        item.put("info", AttributeValue.builder().m(info).build());

        PutItemRequest request = PutItemRequest.builder()
                .tableName(TABLE_NAME).item(item).build();

        PutItemResponse response = client.putItem(request);

        System.out.println("Movie inserted successfully.");
        System.out.println("HTTP status: "
                + response.sdkHttpResponse().statusCode());
    }

    // R: READ
    private static void getMovie(DynamoDbClient client) {

        Map<String, AttributeValue> key = new HashMap<>();
		key.put("year", numberValue( 2013 ));
		key.put("title", stringValue( "Prisoners" ));

        GetItemRequest request = GetItemRequest.builder()
                .tableName(TABLE_NAME) // TABLE_NAME = Movies
                .key(key)
                .consistentRead(true)
                .build();

        GetItemResponse response = client.getItem(request);

        if (response.hasItem() && !response.item().isEmpty()) {
            System.out.println("Movie found:");
            printItem(response.item());
        } else {
            System.out.println("Movie not found.");
        }
    }

    // U: UPDATE
    private static void updateMovieRating(DynamoDbClient client) {

        Map<String, AttributeValue> key = new HashMap<>();
		key.put("year", numberValue( 2013 ));
		key.put("title", stringValue( "Prisoners" ));

        Map<String, String> expressionNames = new HashMap<>();
        expressionNames.put("#info", "info");
        expressionNames.put("#rating", "rating");

        Map<String, AttributeValue> expressionValues =
                new HashMap<>();

        expressionValues.put(":newRating", numberValue(8.5));
        expressionValues.put(":oldRating", numberValue(8.2));

        UpdateItemRequest request = UpdateItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)

                // Update the nested attribute info.rating.
                .updateExpression("SET #info.#rating = :newRating")

                // Update only if the current rating is 8.2.
                .conditionExpression("#info.#rating = :oldRating")

                .expressionAttributeNames(expressionNames)
                .expressionAttributeValues(expressionValues)

                // Return the complete item after updating it.
                .returnValues(ReturnValue.ALL_NEW)
                .build();

        UpdateItemResponse response = client.updateItem(request);

        System.out.println("Movie updated successfully.");
        System.out.println("Updated movie:");
        printItem(response.attributes());
    }

    // D: DELETE
    private static void deleteMovie(DynamoDbClient client) {

        Map<String, AttributeValue> key = new HashMap<>();
		key.put("year", numberValue( 2013 ));
		key.put("title", stringValue( "Prisoners" ));

        DeleteItemRequest request = DeleteItemRequest.builder()
                .tableName(TABLE_NAME)
                .key(key)
                .returnValues(ReturnValue.ALL_OLD)
                .build();

        DeleteItemResponse response = client.deleteItem(request);

        if (response.hasAttributes() && !response.attributes().isEmpty()) {
            System.out.println("Deleted movie:");
            printItem(response.attributes());
        } else {
            System.out.println("No existing movie was returned.");
        }
    }

    // HELPER METHODS
    private static AttributeValue stringValue(String value) {
        return AttributeValue.builder().s(value).build();
    }

    private static AttributeValue numberValue(Number value) {
        return AttributeValue.builder().n(value.toString()).build();
    }

    // Simple readable printer for DynamoDB items.
    private static void printItem(
            Map<String, AttributeValue> item) {

        System.out.println("{");
        for (Map.Entry<String, AttributeValue> entry
                : item.entrySet()) {

            System.out.println(
                    "  " + entry.getKey() + ": "
                            + formatValue(entry.getValue()));
        }
        System.out.println("}");
    }

    private static String formatValue(AttributeValue value) {

        if (value.s() != null) {
            return "\"" + value.s() + "\"";
        }

        if (value.n() != null) {
            return value.n();
        }

        if (value.bool() != null) {
            return value.bool().toString();
        }

        if (value.hasM()) {
            StringBuilder result = new StringBuilder("{");

            for (Map.Entry<String, AttributeValue> entry
                    : value.m().entrySet()) {

                result.append("\n    ")
                        .append(entry.getKey())
                        .append(": ")
                        .append(formatValue(entry.getValue()));
            }

            result.append("\n  }");
            return result.toString();
        }

        if (value.hasL()) {
            StringBuilder result = new StringBuilder("[");

            for (AttributeValue element : value.l()) {
                if (result.length() > 1) {
                    result.append(", ");
                }

                result.append(formatValue(element));
            }

            result.append("]");
            return result.toString();
        }

        if (value.hasSs()) {
            return value.ss().toString();
        }

        if (value.hasNs()) {
            return value.ns().toString();
        }

        if (Boolean.TRUE.equals(value.nul())) {
            return "null";
        }

        return value.toString();
    }
}