package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * Logistic service in charge of delivering books that have been purchased to customers.
 * Handles {@link DeliveryEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class LogisticsService extends MicroService {
	private Integer amount;
	public LogisticsService(String name) {
		super(name);
	}

	@Override
	protected void initialize() {

		subscribeEvent(DeliveryEvent.class, new Callback<DeliveryEvent>() {
			@Override
			public void call(DeliveryEvent c) {
				Future<DeliveryVehicle>f_vehicle=sendEvent(new AcquireEvent<DeliveryVehicle>());
				DeliveryVehicle deliveryVehicle=f_vehicle.get();
				deliveryVehicle.deliver(c.getAddress(),c.getDistance());
				Future<Boolean> f_release=sendEvent(new ReleaseEvent<Boolean>(deliveryVehicle));

			}
		});
	}

}
