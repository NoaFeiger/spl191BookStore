package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 * eventually be resolved to hold a result of some operation. The class allows
 * Retrieving the result once it is available.
 * 
 * Only private methods may be added to this class.
 * No public constructor is allowed except for the empty constructor.
 */
public class Future<T> {
	private T result;
	private boolean done;
	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		this.result = null;
		this.done=false;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public synchronized T get() {
		while (!isDone()) {
			try {
				synchronized (this) {
					this.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e.getMessage());
			}
		}
		return result;
	}
	
	/**
     * Resolves the result of this Future object.
     */
	public synchronized void resolve (T result) {
		this.result=result;
		this.done=true;
		notifyAll();
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return this.done;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public T get(long timeout, TimeUnit unit) {

		//Thread.sleep(unit.convert(timeout, unit));

		long timeoutExpiredMs = getSystem(unit, System.currentTimeMillis()) + getSystem(unit, timeout);
	//	long timeoutExpiredMs = System.currentTimeMillis() + timeout;
		while (!isDone()) {
			long waitMs = timeoutExpiredMs - getSystem(unit, System.currentTimeMillis());
	//		long waitMs = timeoutExpiredMs -System.currentTimeMillis();
			if (waitMs <= 0) {
				// timeout expired
				return null;
			}
			try {
				synchronized (this) {
					this.wait();
				}
			}
			catch (InterruptedException e){
				System.out.println(e.getMessage());
			}
		}
		return result;
	}

	private long getSystem(TimeUnit unit, long time) {
		long system = 0;
		if (unit.equals(TimeUnit.DAYS))
			system = TimeUnit.MILLISECONDS.toDays(time);
		else if (unit.equals(TimeUnit.HOURS)) {
			system = TimeUnit.MILLISECONDS.toHours(time);
		}
		else if (unit.equals(TimeUnit.MICROSECONDS)) {
			system = TimeUnit.MILLISECONDS.toMicros(time);
		}
		else if (unit.equals(TimeUnit.MILLISECONDS)) {
			system = time;
		}
		else if (unit.equals(TimeUnit.MINUTES)) {
			system = TimeUnit.MILLISECONDS.toMinutes(time);
		}
		else if (unit.equals(TimeUnit.NANOSECONDS)) {
			system = TimeUnit.MILLISECONDS.toNanos(time);
		}
		else if (unit.equals(TimeUnit.SECONDS)) {
			system = TimeUnit.MILLISECONDS.toSeconds(time);
		}
		return system;
	}
}
