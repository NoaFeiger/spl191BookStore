package bgu.spl.mics;

public class DeliveryEvent<Boolean> implements Event<Boolean> {
    private int Distance;
    private String address;
    public DeliveryEvent(int distance, String address) {
        Distance = distance;
        this.address = address;
    }

    public int getDistance() {
        return Distance;
    }

    public String getAddress() {
        return address;
    }
}
