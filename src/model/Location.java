package model;

/**
 * Entity class representing a LOCATION (branch) in the database.
 * 
 * PURPOSE: Maps to the 'locations' table in MySQL database.
 * 
 * FIELDS TO IMPLEMENT:
 * - locationId (int/String) - Primary key
 * - locationName (String) - Branch name (e.g., "Manila Downtown", "Cebu Airport")
 * - address (String) - Physical address
 * - city (String) - City name
 * - phone (String) - Branch contact number
 * - managerName (String) - Branch manager name
 * 
 * METHODS TO IMPLEMENT:
 * - Constructor(s)
 * - Getters and Setters for all fields
 * - toString() for debugging
 * - equals() and hashCode()
 * 
 * COLLABORATOR NOTES:
 * - Vehicles and Technicians are assigned to locations
 * - Used in deployment tracking and location-based reports
 */
public class Location {
    // TODO: Add private fields for location attributes
    private String locationID;
    private String name;
    private String status;
    
    // TODO: Add constructors (default and parameterized)
    public Location(){
        this.status = "Active";
    }
    
    public Location(String locationID, String name, String status)
    {
        this.locationID = locationID;
        this.name = name;
        this.status = status;
    }

    public String getLocationID() {
        return locationID;
    }
    
    public void setLocationID(String locationID) {
        this.locationID = locationID;
    }

    public String getName() {
        return name;
    }
    

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public boolean isActive() {
        return "Active".equalsIgnoreCase(status);
    }
    
    /**
     * String representation for debugging
     * @return String describing the location
     */
    @Override
    public String toString() {
        return "Location{" +
                "locationID='" + locationID + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    // TODO: Add toString(), equals(), hashCode()
        /**
     * Check if two locations are equal (based on ID)
     * @param o Object to compare with
     * @return true if same location, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Location location = (Location) o;
        return locationID != null && locationID.equals(location.locationID);
    }
    
    /**
     * Generate hash code based on locationID
     * @return hash code
     */
    @Override
    public int hashCode() {
        return locationID != null ? locationID.hashCode() : 0;
    }
}
