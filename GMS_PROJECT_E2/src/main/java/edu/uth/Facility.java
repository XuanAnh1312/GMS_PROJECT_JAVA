package edu.uth;

public class Facility {
    private String facilityId;
    private String name;
    private String status;

    public Facility(String facilityId, String name, String status) {
        this.facilityId = facilityId;
        this.name = name;
        this.status = status;
    }

    public void update() {
        System.out.println("Facility " + facilityId + " (" + name + ") updated.");
    }

    public void update(String name, String status) {
        this.name = name;
        this.status = status;
        update();
    }

    public String getFacilityId() {
        return facilityId;
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

    @Override
    public String toString() {
        return "facilityId: " + facilityId + "\n" +
               "name: " + name + "\n" +
               "status: " + status;
    }
}
