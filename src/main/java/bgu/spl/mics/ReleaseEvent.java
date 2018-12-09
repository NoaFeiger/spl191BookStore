package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;

public class ReleaseEvent<Boolean> implements Event<Boolean> {

    private DeliveryVehicle deliver;

    public ReleaseEvent(DeliveryVehicle deliver) {
        this.deliver = deliver;
    }
    public DeliveryVehicle getDeliver() {
        return deliver;
    }

}
