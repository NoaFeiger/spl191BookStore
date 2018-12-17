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

	public LogisticsService(String name) {
		super(name);
	}

	@Override
	protected void initialize() {

		subscribeEvent(DeliveryEvent.class, new Callback<DeliveryEvent>() {
			@Override
			public void call(DeliveryEvent c) {
				Future<Future<DeliveryVehicle>>f_vehicle=sendEvent(new AcquireEvent<>());
				if (f_vehicle.get()!=null) {
					DeliveryVehicle deliveryVehicle = f_vehicle.get().get();
					if (deliveryVehicle!=null) {
						deliveryVehicle.deliver(c.getAddress(),c.getDistance());
						sendEvent(new ReleaseEvent<Boolean>(deliveryVehicle));
					}
				}
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
			@Override
			public void call(TerminateBroadcast c) {
				terminate();
			}
		});
		sendBroadcast( new FinishInitializeBroadcast());

	}

}
