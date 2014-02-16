package service.database.models;

public class Location {
	private long id;
	private long timestamp;
	private double latitude;
	private double longitude;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

  	public long getTimestamp() {
  		return timestamp;
  	}

  	public void setTimestamp(long timestamp) {
  		this.timestamp = timestamp;
  	}

  	public double getLatitude() {
  		return latitude;
  	}
  
  	public void setLatitude(double latitude) {
  		this.latitude = latitude;
  	}
  	
  	public double getLongitude() {
  		return longitude;
  	}
  
  	public void setLongitude(double longitude) {
  		this.longitude = longitude;
  	}

  	// It may be used by GUI
  	@Override
 	public String toString() {
  		return String.valueOf(latitude) + " " + String.valueOf(longitude) + " ";
  	}
}
