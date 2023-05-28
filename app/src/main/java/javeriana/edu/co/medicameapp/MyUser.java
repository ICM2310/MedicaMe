package javeriana.edu.co.medicameapp;

public class MyUser {
    private String name;
    private String id;
    private String EPS;
    private CustomLatLng location;


    public MyUser() {
    }

    public MyUser(String name, String id, String EPS, CustomLatLng location) {
        this.name = name;
        this.id = id;
        this.EPS = EPS;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEPS() {
        return EPS;
    }

    public void setEPS(String EPS) {
        this.EPS = EPS;
    }

    public CustomLatLng getLocation() {
        return location;
    }

    public void setLocation(CustomLatLng location) {
        this.location = location;
    }
}
