package pmj.bdp.dynamodb.document.movies;

import pmj.bdp.dynamodb.dbutil.Dynamo;

import software.amazon.awssdk.enhanced.dynamodb.AttributeConverterProvider;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableMetadata;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.document.EnhancedDocument;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.core.JsonProcessingException;

public class MoviesCRUDDocument { 

    private static final String TABLE_NAME = "Movies";
    private static final int MOVIE_YEAR = 2013;
    private static final String MOVIE_TITLE = "Prisoners";

    public static void main(String[] args) throws Exception{

    	// create a lower level client
    	try (DynamoDbClient __client = Dynamo.getClient() ) { 

    	// Create the Enhanced Client.
        DynamoDbEnhancedClient enhancedClient =
                DynamoDbEnhancedClient.builder()
                        .dynamoDbClient( __client )
                        .build();

        // Define the table schema.
        TableSchema<EnhancedDocument> documentSchema =
            TableSchema.documentSchemaBuilder()
	            .addIndexPartitionKey(
	                    TableMetadata.primaryIndexName(),"year",AttributeValueType.N)
	            .addIndexSortKey(TableMetadata.primaryIndexName(),"title",AttributeValueType.S)
	            .attributeConverterProviders(AttributeConverterProvider.defaultProvider())
	            .build();

        // Associate the document schema with the existing table.
        DynamoDbTable<EnhancedDocument> movieTable = enhancedClient.table(TABLE_NAME,documentSchema);

            createMovie(movieTable);

            getMovie(movieTable);

            updateMovieRating(movieTable);

            getMovie(movieTable);

            deleteMovie(movieTable);

        } catch (DynamoDbException e) {
            System.err.println("DynamoDB operation failed:");
            System.err.println(e.getMessage());
        }
    }

    // CREATE
    private static void createMovie(
            DynamoDbTable<EnhancedDocument> table) {

        String json = """
            {
                "year": 2013,
                "title": "Prisoners",
                "info": {
                    "directors": ["Denis Villeneuve"],
                    "release_date": "2013-08-30T00:00:00Z",
                    "rating": 8.2,
                    "genres": ["Crime", "Drama", "Mystery"]
                }
            }
            """;        
        
        EnhancedDocument movie =
                EnhancedDocument.builder()
                        .json(json)
                        .build();

        table.putItem(movie);

        System.out.println("Movie inserted successfully.");
        System.out.println(movie.toJson());
    }

    private static void createMovie1(
                DynamoDbTable<EnhancedDocument> table) {

    	ObjectMapper mapper = new ObjectMapper();
    	ObjectNode movie = mapper.createObjectNode();

        movie.put("year", 2013);
        movie.put("title", "Prisoners");

        // Create the nested "info" object
        ObjectNode info = movie.putObject("info");

        // Add directors array
        ArrayNode directors = info.putArray("directors");
        directors.add("Denis Villeneuve");

        info.put("release_date", "2013-08-30T00:00:00Z");
        info.put("rating", 8.2);

        // Add genres array
        ArrayNode genres = info.putArray("genres");
        genres.add("Crime");
        genres.add("Drama");
        genres.add("Mystery");

        // Convert the JSON object into a string
    	String json = "";
	    try {
	        json = mapper.writerWithDefaultPrettyPrinter()
	                            .writeValueAsString(movie);
        } catch (JsonProcessingException e) {
        	
        }
	    
        EnhancedDocument movieDoc =
                EnhancedDocument.builder().json(json).build();

        table.putItem(movieDoc);

        System.out.println("Movie inserted successfully.");
        System.out.println(movieDoc.toJson());
        
    }
    
    // READ
    private static void getMovie(
            DynamoDbTable<EnhancedDocument> table) {

    	Key key = movieKey();

        EnhancedDocument movie = table.getItem(key);

        if (movie != null) {
            System.out.println("Movie found:");
            System.out.println(movie.toJson());

            System.out.println(
                    "Title: " + movie.getString("title"));

            System.out.println(
                    "Year: " + movie.getNumber("year"));

            System.out.println(
                    "Rating: "
                    + movie.getJson("info"));
        } else {
            System.out.println("Movie not found.");
        }
    }

    // UPDATE
    private static void updateMovieRating(
            DynamoDbTable<EnhancedDocument> table) {

        EnhancedDocument existingMovie =
                table.getItem(movieKey());

        if (existingMovie == null) {
            System.out.println("Movie not found; cannot update.");
            return;
        }

        // Rebuild the document with the new rating.
        // All the other movie attributes are preserved.

        String updatedJson = """
                {
                    "year": 2013,
                    "title": "Prisoners",
                    "info": {
                        "directors": ["Denis Villeneuve"],
                        "release_date": "2013-08-30T00:00:00Z",
                        "rating": 8.5,
                        "genres": ["Crime", "Drama", "Mystery"]
                    }
                }
                """;        
        
        
        EnhancedDocument updatedMovie =
                EnhancedDocument.builder()
                        .json(updatedJson)
                        .build();

        EnhancedDocument result =
                table.updateItem(updatedMovie);

        System.out.println("Movie updated successfully.");
        System.out.println(result.toJson());
    }

    // DELETE
    private static void deleteMovie(
            DynamoDbTable<EnhancedDocument> table) {

        table.deleteItem(movieKey());

        System.out.println("Delete request completed.");

        // Verify that the item no longer exists.
        EnhancedDocument movie =
                table.getItem(movieKey());

        if (movie == null) {
            System.out.println("Movie no longer exists.");
        } else {
            System.out.println("Movie still exists.");
        }
    }

    // Construct the composite primary key.
    private static Key movieKey() {

        return Key.builder()
                .partitionValue(MOVIE_YEAR)
                .sortValue(MOVIE_TITLE)
                .build();
    }
}
