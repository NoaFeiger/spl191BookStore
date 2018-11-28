package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.Queue;
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
	private ConcurrentHashMap<Class<? extends Event>, BlockingQueue<MicroService>> classQueueHashMap;
	private ConcurrentHashMap<Event, Future> eventFutureHashMap;
//	private Queue<MicroService> CheckAvailablityEventQueue;
//	private Queue<MicroService> OrderBookEventQueue;
	private MessageBusImpl() {
		serviceQueueHashMap = new ConcurrentHashMap<>();
		eventFutureHashMap = new ConcurrentHashMap<>();
		classQueueHashMap = new ConcurrentHashMap<>();
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
		if (!classQueueHashMap.containsKey(type)) {
			classQueueHashMap.put(type, new LinkedBlockingQueue<>());
		}
		classQueueHashMap.get(type).add(m);
//		if(CheckAvailabiltyEvent.class.isAssignableFrom(type)) {
//			CheckAvailablityEventQueue.add(m);
//		}
//		else if (OrderBookEvent.class.isAssignableFrom(type)){
//			OrderBookEventQueue.add(m);
//		}

	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		// TODO Auto-generated method stub

	}

	@Override
	public void sendBroadcast(Broadcast b) {
		// TODO Auto-generated method stub

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void register(MicroService m) {
		serviceQueueHashMap.put(m, new LinkedList<>());
	}

	@Override
	public void unregister(MicroService m) {
		// TODO Auto-generated method stub

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		while(serviceQueueHashMap.get(m).isEmpty()) {
			m.wait();
		}
		return serviceQueueHashMap.get(m).remove();
	}

	

}
