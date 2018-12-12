package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.Future;

import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Passive object representing the resource manager.
 * You must not alter any of the given public methods of this class.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private methods and fields to this class.
 */
public class ResourcesHolder {
	private static ResourcesHolder instance = null;
	private Queue<DeliveryVehicle> deliveryVehicles;
	private Queue<Future<DeliveryVehicle>> futureNotResolved;
	private Semaphore semaphore;

	private static class SingletonHolder {
		private static ResourcesHolder instance = new ResourcesHolder();
	}

	public static ResourcesHolder getInstance() {
		return SingletonHolder.instance;
	}

	private ResourcesHolder() {
		deliveryVehicles = new ConcurrentLinkedQueue<>();
		futureNotResolved = new ConcurrentLinkedQueue<>();
	}
	/**
     * Retrieves the single instance of this class.
     */
	
	/**
     * Tries to acquire a vehicle and gives a future object which will
     * resolve to a vehicle.
     * <p>
     * @return 	{@link Future<DeliveryVehicle>} object which will resolve to a 
     * 			{@link DeliveryVehicle} when completed.   
     */
	public Future<DeliveryVehicle> acquireVehicle() {
		Future<DeliveryVehicle> f = new Future<>();
		synchronized (futureNotResolved) {
			if (semaphore.tryAcquire()) {
				DeliveryVehicle d = deliveryVehicles.remove();
				f.resolve(d);
			}
			else {
				futureNotResolved.add(f);
			}
			return f;
		}
	}
	
	/**
     * Releases a specified vehicle, opening it again for the possibility of
     * acquisition.
     * <p>
     * @param vehicle	{@link DeliveryVehicle} to be released.
     */
	public void releaseVehicle(DeliveryVehicle vehicle) {
		synchronized (futureNotResolved) {
			if (!futureNotResolved.isEmpty()) {
				Future<DeliveryVehicle> f = futureNotResolved.remove();
				f.resolve(vehicle);
			}
			else { // no future waiting
				deliveryVehicles.add(vehicle);
				semaphore.release();
			}
		}
	}
	
	/**
     * Receives a collection of vehicles and stores them.
     * <p>
     * @param vehicles	Array of {@link DeliveryVehicle} instances to store.
     */
	public void load(DeliveryVehicle[] vehicles) {
		for (int i = 0; i < vehicles.length; i++) {
			deliveryVehicles.add(vehicles[i]);
		}
		semaphore = new Semaphore(vehicles.length);
	}

}
