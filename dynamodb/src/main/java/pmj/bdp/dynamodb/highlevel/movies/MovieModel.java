package pmj.bdp.dynamodb.highlevel.movies;

import java.util.List;

//import pmj.bdp.dynamodb.highlevel.movies.MoviesCRUDEnhanced.MovieInfo;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

// MODEL CLASS 1: Movie
// Maps to an item in the Movies DynamoDB table.
@DynamoDbBean
public class MovieModel {

    private Integer year;
    private String title;
    private MovieInfo info;

    public MovieModel() {
    }

    @DynamoDbPartitionKey
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @DynamoDbSortKey
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public MovieInfo getInfo() {
        return info;
    }

    public void setInfo(MovieInfo info) {
        this.info = info;
    }
    
    // Maps to the nested "info" map in DynamoDB.
    @DynamoDbBean
    public static class MovieInfo {
		private List<String> directors;
		private String releaseDate;
		private Double rating;
		private List<String> genres;

		public MovieInfo() {
		}

		public List<String> getDirectors() {
			return directors;
		}
	
		public void setDirectors(List<String> directors) {
			this.directors = directors;
		}
	
		// The Java property is releaseDate, but the DynamoDB
		// attribute in the existing table is release_date.
		@DynamoDbAttribute("release_date")
		public String getReleaseDate() {
			return releaseDate;
		}
	
		public void setReleaseDate(String releaseDate) {
			this.releaseDate = releaseDate;
		}
	
		public Double getRating() {
			return rating;
		}
	
		public void setRating(Double rating) {
			this.rating = rating;
		}
	
		public List<String> getGenres() {
			return genres;
		}
	
		public void setGenres(List<String> genres) {
			this.genres = genres;
		}
    }    
}


