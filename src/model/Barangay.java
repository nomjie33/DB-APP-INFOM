package model;

/**
 * Entity class representing a BARANGAY in the database.
 * 
 * PURPOSE: Maps to the 'barangays' table in MySQL database.
 * Stores barangay information for customer addresses.
 * Links to City table for hierarchical address structure.
 * Used for customer demographics analysis and reporting.
 */
public class Barangay {
    
    private Integer barangayID;
    private Integer cityID;
    private String name;
    
    // Optional: reference to City object for convenience
    private City city;
    
    // Constructors
    public Barangay() {
    }
    
    public Barangay(Integer barangayID, Integer cityID, String name) {
        this.barangayID = barangayID;
        this.cityID = cityID;
        this.name = name;
    }
    
    // Getters and Setters
    public Integer getBarangayID() {
        return barangayID;
    }
    
    public void setBarangayID(Integer barangayID) {
        this.barangayID = barangayID;
    }
    
    public Integer getCityID() {
        return cityID;
    }
    
    public void setCityID(Integer cityID) {
        this.cityID = cityID;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public City getCity() {
        return city;
    }
    
    public void setCity(City city) {
        this.city = city;
        if (city != null) {
            this.cityID = city.getCityID();
        }
    }
    
    // Utility methods
    @Override
    public String toString() {
        return "Barangay{" +
                "barangayID=" + barangayID +
                ", cityID=" + cityID +
                ", name='" + name + '\'' +
                (city != null ? ", city=" + city.getName() : "") +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Barangay barangay = (Barangay) o;
        return barangayID != null && barangayID.equals(barangay.barangayID);
    }
    
    @Override
    public int hashCode() {
        return barangayID != null ? barangayID.hashCode() : 0;
    }
}
