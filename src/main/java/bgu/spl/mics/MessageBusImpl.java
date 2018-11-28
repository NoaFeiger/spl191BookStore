package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = null;
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> serviceQueueHashMap;
	private ConcurrentHashMap<Class<? extends Event>, BlockingQueue<MicroService>> eventQueueHashMap_robin;
	private ConcurrentHashMap<Class<? extends Broadcast>, LinkedList<MicroService>> broadcastListHashMap;
	private ConcurrentHashMap<Event, Future> eventFutureHashMap;
//	private Queue<MicroService> CheckAvailablityEventQueue;
//	private Queue<MicroService> OrderBookEventQueue;
	private MessageBusImpl() {
		serviceQueueHashMap = new ConcurrentHashMap<>();
		eventFutureHashMap = new ConcurrentHashMap<>();
		eventQueueHashMap_robin = new ConcurrentHashMap<>();
		broadcastListHashMap = new ConcurrentHashMap<>();
//		CheckAvailablityEventQueue = new LinkedList<>();
//		OrderBookEventQueue = new LinkedList<>();
	}

	public static MessageBusImpl getInstance() {
		if(instance == null) {
			synchronized (MessageBusImpl.class) {
				if(instance == null) {
					instance = new MessageBusImpl();
				}
			}
		}
		return instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		if (!eventQueueHashMap_robin.containsKey(type)) {
			eventQueueHashMap_robin.put(type, new LinkedBlockingQueue<>());
		}
		eventQueueHashMap_robin.get(type).add(m);
//		if(CheckAvailabiltyEvent.class.isAssignableFrom(type)) {
//			CheckAvailablityEventQueue.add(m);
//		}
//		else if (OrderBookEvent.class.isAssignableFrom(type)){
//			OrderBookEventQueue.add(m);
//		}

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		if (!broadcastListHashMap.containsKey(type)) {
			broadcastListHashMap.put(type, new LinkedList<>());
		}
		broadcastListHashMap.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		eventFutureHashMap.get(e).resolve(result);

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		for ( MicroService m : broadcastListHashMap.get(b.getClass())){
			serviceQueueHashMap.get(m).add(b);
			m.notify();
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f = new Future<>();
		eventFutureHashMap.put(e,f);
		MicroService m = eventQueueHashMap_robin.get(e.getClass()).remove();
		serviceQueueHashMap.get(m).add(e);
		eventQueueHashMap_robin.get(e.getClass()).add(m);
		m.notifyAll();
		return f;
	}

	@Override
	public void register(MicroService m) {
		serviceQueueHashMap.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		serviceQueueHashMap.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		while(serviceQueueHashMap.get(m).isEmpty()) {
			m.wait();
		}
		return serviceQueueHashMap.get(m).remove();
	}

	

}
