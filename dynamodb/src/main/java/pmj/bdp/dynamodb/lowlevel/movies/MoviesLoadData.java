package pmj.bdp.dynamodb.lowlevel.movies;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

// JSON manipulation Library
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pmj.bdp.dynamodb.dbutil.Dynamo;


public class MoviesLoadData {

	private static String tableName = "Movies1";
	private static DynamoDbClient client;
	
    public static void main(String[] args) throws Exception {

        // Connect to DynamoDB Local
        try (DynamoDbClient _client = Dynamo.getClient() ) {

        	client = _client;
            //String movies_data_file = "..\\datasets\\json\\moviedata.json";                      
            //load_data( movies_data_file );
        	
	        Map<String, AttributeValue> key = new HashMap<>();        
	        key.put("year", numberValue(2013));
	        key.put("title", stringValue("Prisoners"));
	        
	        //getMovie0( key );
	        getMovie( 2013, "Prisoners" );
            
        }
    }
    
    public static void load_data(String file_name) throws Exception {

        // Read the JSON file
        ObjectMapper mapper = new ObjectMapper();
    	JsonNode rootNode = mapper.readTree(new File( file_name ));

        Iterator<JsonNode> iter = rootNode.elements();

        while (iter.hasNext()) {

            JsonNode currentNode = iter.next();

            int year = currentNode.path("year").asInt();
            String title = currentNode.path("title").asText();

            try {
                // Construct the DynamoDB item
                Map<String, AttributeValue> item = new HashMap<>();

                // Partition key: year (Number)
                item.put(
                    "year",
                    AttributeValue.builder()
                        .n(Integer.toString(year))
                        .build()
                );

                // Sort key: title (String)
                item.put(
                    "title",
                    AttributeValue.builder()
                        .s(title)
                        .build()
                );

                // Store info as a DynamoDB Map attribute
                JsonNode infoNode = currentNode.path("info");

                item.put(
                    "info",
                    toAttributeValue(infoNode)
                );

                // Insert the item
                PutItemRequest request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(item)
                    .build();

                client.putItem(request);

                System.out.println(
                    "PutItem succeeded: " + year + " " + title
                );

            } catch (DynamoDbException e) {

                System.err.println(
                    "Unable to add movie: " + year + " " + title
                );

                System.err.println(e.getMessage());

                break;
            }
        }
    }

    /**
     * Convert a Jackson JsonNode into a DynamoDB AttributeValue.
     *
     * Supports JSON objects, arrays, strings, numbers,
     * booleans, and null values.
     */
    private static AttributeValue toAttributeValue(JsonNode node) {

        if (node == null || node.isNull() || node.isMissingNode()) {
            return AttributeValue.builder()
                .nul(true)
                .build();
        }

        // JSON object -> DynamoDB Map (M)
        if (node.isObject()) {

            Map<String, AttributeValue> attributes = new HashMap<>();

            Iterator<Map.Entry<String, JsonNode>> fields =
                node.fields();

            while (fields.hasNext()) {

                Map.Entry<String, JsonNode> field = fields.next();

                attributes.put(
                    field.getKey(),
                    toAttributeValue(field.getValue())
                );
            }

            return AttributeValue.builder()
                .m(attributes)
                .build();
        }

        // JSON array -> DynamoDB List (L)
        if (node.isArray()) {

            List<AttributeValue> values = new ArrayList<>();

            for (JsonNode element : node) {
                values.add(toAttributeValue(element));
            }

            return AttributeValue.builder()
                .l(values)
                .build();
        }

        // JSON string -> DynamoDB String (S)
        if (node.isTextual()) {
            return AttributeValue.builder()
                .s(node.asText())
                .build();
        }

        // JSON number -> DynamoDB Number (N)
        if (node.isNumber()) {
            return AttributeValue.builder()
                .n(node.asText())
                .build();
        }

        // JSON boolean -> DynamoDB Boolean (BOOL)
        if (node.isBoolean()) {
            return AttributeValue.builder()
                .bool(node.asBoolean())
                .build();
        }

        throw new IllegalArgumentException(
            "Unsupported JSON value: " + node
        );
    }
    
    private static void getMovie(int year, String title) {

        try {
            // Construct the composite primary key
            Map<String, AttributeValue> key = Map.of(
                "year", AttributeValue.builder()
                                      .n(String.valueOf(year))
                                      .build(),

                "title", AttributeValue.builder()
                                       .s(title)
                                       .build()
            );

            // Retrieve the movie
            GetItemResponse response = client.getItem(
                GetItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .build()
            );

            // Check whether the movie exists
            if (response.item().isEmpty()) {
                System.out.println("Movie not found: " + title + " (" + year + ")");
                return;
            }

            Map<String, AttributeValue> movie = response.item();

            // Extract top-level attributes
            int movieYear = Integer.parseInt(movie.get("year").n());
            String movieTitle = movie.get("title").s();

            // Extract nested information
            Map<String, AttributeValue> info = movie.get("info").m();

            // Print formatted movie details
            System.out.println();
            System.out.println("==============================================");
            System.out.println("               MOVIE DETAILS");
            System.out.println("==============================================");

            System.out.printf("Title          : %s%n", movieTitle);
            System.out.printf("Year           : %d%n", movieYear);

            System.out.println("----------------------------------------------");
            System.out.println("INFORMATION");
            System.out.println("----------------------------------------------");

            printStringList("Directors", info.get("directors"));
            printStringList("Genres", info.get("genres"));
            printStringList("Actors", info.get("actors"));

            printString("Release Date", info.get("release_date"));
            printNumber("Rating", info.get("rating"));
            printNumber("Rank", info.get("rank"));
            printNumber("Running Time (seconds)", info.get("running_time_secs"));
            printString("Image URL", info.get("image_url"));
            printString("Plot", info.get("plot"));

            System.out.println("==============================================");

        } catch (DynamoDbException e) {
            System.err.println("Failed to retrieve movie from DynamoDB.");
            System.err.println(e.getMessage());
        }
    }

    private static void printString(String label, AttributeValue value) {
        if (value != null && value.s() != null) {
            System.out.printf("%-24s: %s%n", label, value.s());
        }
    }

    private static void printNumber(String label, AttributeValue value) {
        if (value != null && value.n() != null) {
            System.out.printf("%-24s: %s%n", label, value.n());
        }
    }

    private static void printStringList(String label, AttributeValue value) {
        if (value != null && value.hasL()) {
            List<String> values = value.l().stream()
                .map(AttributeValue::s)
                .toList();

            System.out.printf("%-24s: %s%n", label, String.join(", ", values));
        }
    }    

	private static AttributeValue stringValue(String value) {
	    return AttributeValue.builder().s(value).build();
	}

	private static AttributeValue numberValue(Number value) {
	    return AttributeValue.builder().n(value.toString()).build();
	}
	
}