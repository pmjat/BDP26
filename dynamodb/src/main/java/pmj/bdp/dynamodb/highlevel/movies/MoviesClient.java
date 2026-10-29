package pmj.bdp.dynamodb.highlevel.movies;

import pmj.bdp.dynamodb.dbutil.Dynamo;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class MoviesClient {

    public static void main(String[] args) throws Exception {

            try (DynamoDbClient client = Dynamo.getClient() ) { 

            DynamoDbEnhancedClient enhancedClient =
                    DynamoDbEnhancedClient.builder()
                            .dynamoDbClient(client)
                            .build();

            DynamoDbTable<Movie> movieTable =
                    enhancedClient.table(
                            "Movies",
                            TableSchema.fromBean(Movie.class));

            Movie movie = new Movie();
            movie.setYear(2013);
            movie.setTitle("Prisoners");
            movie.setRating(8.2);

            movieTable.putItem(movie);

            System.out.println("Movie inserted successfully.");
        }
    }
}