package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseEvent<Boolean> implements Event<Boolean> {

    private DeliveryVehicle deliveryVehicle;

    public ReleaseEvent(DeliveryVehicle deliver) {
        this.deliveryVehicle = deliver;
    }
    public DeliveryVehicle getDeliveryVehicle() {
        return deliveryVehicle;
    }

}
