package pmj.bdp.dynamodb.highlevel.movies;

import java.util.Arrays;

import pmj.bdp.dynamodb.dbutil.Dynamo;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

public class MoviesCRUDEnhanced {

    private static final String TABLE_NAME = "Movies";
    private static final Integer MOVIE_YEAR = 2013;
    private static final String MOVIE_TITLE = "Prisoners";

    public static void main(String[] args) throws Exception {
    	
    	// create a lower level client
        try (DynamoDbClient __client = Dynamo.getClient() ) {

            // Create the higher-level client.
            DynamoDbEnhancedClient enhancedClient =
                    DynamoDbEnhancedClient.builder()
                            .dynamoDbClient( __client )
                            .build();
 
            // Map the existing table to the Movie class.
            DynamoDbTable<MovieModel> movieTable =
            	enhancedClient.table(
            	   TABLE_NAME, 
                   TableSchema.fromBean(MovieModel.class));

            //createMovie(movieTable);

            getMovie(movieTable);

            //updateMovieRating(movieTable);

            //printMovie(movieTable);

            //deleteMovie(movieTable);

        } catch (DynamoDbException e) {
            System.err.println("DynamoDB operation failed:");
            System.err.println(e.getMessage());
        }
    }

    // CREATE: Insert a movie.
    private static void createMovie(DynamoDbTable<MovieModel> table) {

        MovieModel.MovieInfo info = new MovieModel.MovieInfo();
        info.setDirectors(
                Arrays.asList("Denis Villeneuve"));
        info.setReleaseDate("2013-08-30T00:00:00Z");
        info.setRating(8.2);
        info.setGenres(
                Arrays.asList("Crime", "Drama", "Mystery"));

        MovieModel movie = new MovieModel();
        movie.setYear(MOVIE_YEAR);
        movie.setTitle(MOVIE_TITLE);
        movie.setInfo(info);

        table.putItem(movie);

        System.out.println("Movie inserted successfully.");
        printMovie(movie);
    }

    // READ: Retrieve a movie by partition key and sort key.
    private static void getMovie(DynamoDbTable<MovieModel> table) {

        Key key = Key.builder()
                .partitionValue(MOVIE_YEAR)
                .sortValue(MOVIE_TITLE)
                .build();

        MovieModel movie = table.getItem(key);

        if (movie != null) {
            System.out.println("Movie found:");
            printMovie(movie);
        } else {
            System.out.println("Movie not found.");
        }
    }

    // UPDATE: Change the movie rating from 8.2 to 8.5.
    private static void updateMovieRating(
            DynamoDbTable<MovieModel> table) {

    	Key key = Key.builder()
                .partitionValue(MOVIE_YEAR)
                .sortValue(MOVIE_TITLE)
                .build();

        // First, retrieve the existing movie.
        MovieModel movie = table.getItem(key);

        if (movie == null) {
            System.out.println("Movie not found; cannot update.");
            return;
        }

        // Modify the Java object.
        movie.getInfo().setRating(8.5);

        // Persist the modified object to DynamoDB.
        MovieModel updatedMovie = table.updateItem(movie);

        System.out.println("Movie updated successfully.");
        printMovie(updatedMovie);
    }

    // DELETE: Remove the movie.
    private static void deleteMovie(DynamoDbTable<MovieModel> table) {

        Key key = Key.builder()
                .partitionValue(MOVIE_YEAR)
                .sortValue(MOVIE_TITLE)
                .build();

        MovieModel deletedMovie = table.deleteItem(key);

        if (deletedMovie != null) {
            System.out.println("Movie deleted successfully.");
            System.out.println("Deleted movie was:");
            printMovie(deletedMovie);
        } else {
            System.out.println("Movie was not found.");
        }
    }

    // Display the movie in a readable format.
    private static void printMovie(MovieModel movie) {

        System.out.println("{");
        System.out.println("  year: " + movie.getYear());
        System.out.println("  title: \"" + movie.getTitle() + "\"");

        MovieModel.MovieInfo info = movie.getInfo();

        if (info != null) {
            System.out.println("  info: {");
            System.out.println(
                    "    directors: " + info.getDirectors());
            System.out.println(
                    "    release_date: \"" + info.getReleaseDate() + "\"");
            System.out.println(
                    "    rating: " + info.getRating());
            System.out.println(
                    "    genres: " + info.getGenres());
            System.out.println("  }");
        }

        System.out.println("}");
    }

}