package mk.meeskantje.meeskantjecontrol.model;

public class Drone {
    private int id, country_id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCountry_id() {
        return country_id;
    }

    public void setCountry_id(int country_id) {
        this.country_id = country_id;
    }

    public Drone(int id, int country_id) {
        this.id = id;
        this.country_id = country_id;
    }

    @Override
    public String toString() {
        return "Drone{" +
                "id=" + id +
                ", country_id=" + country_id +
                '}';
    }
}
