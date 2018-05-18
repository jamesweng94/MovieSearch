package xml_parsing;

public class star {
	private String id;
	private String stageName;
	private String firstName;
	private String lastName;
	private int birthYear;
	
	star(){
		id = null;
		stageName = null;
		firstName = null;
		lastName = null;
		birthYear = 0;
	}
	
	star(String id, String stageName, String firstName, String lastName, int birthYear){
		this.id = id;
		this.stageName = stageName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.birthYear = birthYear;
	}
	
	//getters
	String getID() { return id; };
	String getStageName() { return stageName; }
	String getFirstName() { return firstName; }
	String getLastName() { return lastName; }
	int getBirthYear() { return birthYear; };
	
	//setters
	public void SetID(String id) {
		this.id = id;
	}
	
	public void setStageName(String stageName) {
		this.stageName = stageName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public void setBirthYear(int birthYear) {
		this.birthYear = birthYear;
	}
	
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getFirstName());
		sb.append(" ");
		sb.append(getLastName());
		sb.append(",");
		sb.append(getBirthYear());	
		sb.append('\n');
		return sb.toString();
	}
	
}
