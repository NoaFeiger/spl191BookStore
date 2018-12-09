package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TimeRequestEvent;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.*;
import bgu.spl.mics.application.services.CountdownLatchWraper;

import java.util.Timer;
import java.util.TimerTask;

/**
 * TimeService is the global system timer There is only one instance of this micro-service.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other micro-services about the current time tick using {@link TickBroadcast}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends MicroService{
	private long speed;
	private int duration;
	private Timer timer;
	private int currentTick;
	private CountdownLatchWraper countdownLatchWraper=CountdownLatchWraper.getInstance();

	public TimeService(long speed,int duration,String name) {
		super(name);
		this.duration=duration;
		this.speed=speed;
		this.currentTick = 1;
	}

	@Override
	protected void initialize() {
		subscribeEvent(TimeRequestEvent.class, new Callback<TimeRequestEvent>() {
			@Override
			public void call(TimeRequestEvent c) {
				complete(c, currentTick);
			}
		});
		countdownLatchWraper.await();
		this.timer = new Timer();
		timer.schedule(new sendBroadcastTask(), 100, speed);
	}

	private class sendBroadcastTask extends TimerTask {
		@Override
		public void run() {
			currentTick++;
			if (currentTick < duration) { //TODO CHECK EQUAL
				sendBroadcast(new TickBroadcast(currentTick));
				if (currentTick == 1) {
					countFirst.await();
				}
			}
			else {
				sendBroadcast(new TerminationMessage());
				cancel();
				timer.cancel();
				countReceipt.countDown();
			}
		}
	}
}
