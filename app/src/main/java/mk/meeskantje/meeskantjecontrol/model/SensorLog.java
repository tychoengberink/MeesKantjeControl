package mk.meeskantje.meeskantjecontrol.model;

public class SensorLog {
    private int id, sensor_id, coordinate_id;
    private String value;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(int sensor_id) {
        this.sensor_id = sensor_id;
    }

    public int getCoordinate_id() {
        return coordinate_id;
    }

    public void setCoordinate_id(int coordinate_id) {
        this.coordinate_id = coordinate_id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public SensorLog(int id, int sensor_id, int coordinate_id, String value) {
        this.id = id;
        this.sensor_id = sensor_id;
        this.coordinate_id = coordinate_id;
        this.value = value;
    }

    @Override
    public String toString() {
        return "SensorLog{" +
                "id=" + id +
                ", sensor_id=" + sensor_id +
                ", coordinate_id=" + coordinate_id +
                ", value='" + value + '\'' +
                '}';
    }
}
