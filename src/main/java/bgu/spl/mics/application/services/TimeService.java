package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TimeRequestEvent;

import java.util.Timer;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link Tick Broadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private Double speed;
	private Double duration;
	private Timer timer;
	private int currentTick;
	public TimeService(Double speed,Double duration,String name) {
		super(name);
		this.duration=duration;
		this.speed=speed;
		this.currentTick = 1;
		this.timer = new Timer();
//		timer.schedule(ne);
	}

	@Override
	protected void initialize() {
		subscribeEvent(TimeRequestEvent.class, new Callback<TimeRequestEvent>() {
			@Override
			public void call(TimeRequestEvent c) {
				complete(c, currentTick);
			}
		});
	}

}
