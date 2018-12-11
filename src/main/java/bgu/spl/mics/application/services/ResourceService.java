package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.*;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * ResourceService is in charge of the store resources - the delivery vehicles.
 * Holds a reference to the {@link ResourcesHolder} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class ResourceService extends MicroService {
	private ResourcesHolder resourcesHolder = ResourcesHolder.getInstance();
	private Queue<Future<DeliveryVehicle>> futures;
	public ResourceService(String name) {
		super(name);
		futures = new LinkedList<>();
	}

	@Override
	protected void initialize() {
		subscribeEvent(AcquireEvent.class, new Callback<AcquireEvent>() {
			@Override
			public void call(AcquireEvent c) {
				Future<DeliveryVehicle> f_vehicle = resourcesHolder.acquireVehicle();
				futures.add(f_vehicle);
				complete(c, f_vehicle);
			}
		});
		subscribeEvent(ReleaseEvent.class, new Callback<ReleaseEvent>() {
			@Override
			public void call(ReleaseEvent c) {
				resourcesHolder.releaseVehicle(c.getDeliveryVehicle());
				complete(c,true);
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
			@Override
			public void call(TerminateBroadcast c) {
				for (Future<DeliveryVehicle> f : futures) {
					if (!f.isDone()) {
						f.resolve(null);
					}
				}
				terminate();
			}
		});
		sendBroadcast( new FinishInitializeBroadcast());

	}
}
