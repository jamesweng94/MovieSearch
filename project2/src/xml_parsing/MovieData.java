package xml_parsing;

public class MovieData {
	
	private String movieID;
	private String title;
	private int year;
	private String director;
	private String genres;
	
	// genres in movies
	private int genres_id;
	private String movie_id;
	
	MovieData(){
		movieID = null;
		title = null;
		year = 0;
		director = null;
		genres = null;
	}
	
	MovieData(String movieID, String title, int year, String director){
		this.movieID = movieID;
		this.title = title;
		this.year = year;
		this.director = director;
	}
	
	MovieData(int genres_id, String movie_id){
		this.genres_id = genres_id;
		this.movie_id = movie_id;
	}
	
	//getters
	public String getMovieID() {
		return movieID;
	}
	
	public String getTitle() {
		return title;
	}
	
	public int getYear() {
		return year;
	}
	
	public String getDirector() {
		return director;
	}
	
	public String getGenres() {
		return genres;
	}
	
	public int getGenresID() {
		return genres_id;
	}
	
	public String getGenresInMovieID() {
		return movie_id;
	}
	
	
	//setters
	public void setMovieID(String movieID) {
		this.movieID = movieID;
	}
	
	public void setMovieTitle(String title) {
		this.title = title;
	}
	
	public void setYear(int year) {
		this.year = year;
	}
	
	public void setDirector(String director) {
		this.director = director;
	}
	
	public void setGenres(String genres) {
		this.genres = genres;
	}
	
	public void setGenresID(int genres_id) {
		this.genres_id = genres_id;
	}
	
	public void setGenresInMovieID(String movie_id) {
		this.movie_id = movie_id;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getMovieID());
		sb.append(",");
		sb.append("\"");
		sb.append(getTitle());
		sb.append("\"");
		sb.append(",");
		sb.append(getYear());
		sb.append(",");
		sb.append(getDirector());
		sb.append(",");
		sb.append(getGenres());
		sb.append('\n');
		return sb.toString();
	}
}
