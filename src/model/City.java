package model;

/**
 * Entity class representing a CITY in the database.
 * 
 * PURPOSE: Maps to the 'cities' table in MySQL database.
 * Stores city information for customer addresses.
 * Used for customer demographics analysis and reporting.
 */
public class City {
    
    private Integer cityID;
    private String name;
    
    // Constructors
    public City() {
    }
    
    public City(Integer cityID, String name) {
        this.cityID = cityID;
        this.name = name;
    }
    
    // Getters and Setters
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
    
    // Utility methods
    @Override
    public String toString() {
        return name;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return cityID != null && cityID.equals(city.cityID);
    }
    
    @Override
    public int hashCode() {
        return cityID != null ? cityID.hashCode() : 0;
    }
}
