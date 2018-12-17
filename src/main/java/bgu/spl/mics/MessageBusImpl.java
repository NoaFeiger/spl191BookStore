package bgu.spl.mics;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private static MessageBusImpl instance = null;
	private ConcurrentHashMap<MicroService, BlockingQueue<Message>> serviceQueueHashMap;
	private ConcurrentHashMap<Class<? extends Event>, ConcurrentLinkedQueue<MicroService>> eventQueueHashMap_robin;
	private ConcurrentHashMap<Class<? extends Broadcast>, ConcurrentLinkedQueue<MicroService>> broadcastQueueHashMap;
	private ConcurrentHashMap<Event<?>, Future> eventFutureHashMap;

	private MessageBusImpl() {
		serviceQueueHashMap = new ConcurrentHashMap<>();
		eventFutureHashMap = new ConcurrentHashMap<>();
		eventQueueHashMap_robin = new ConcurrentHashMap<>();
		broadcastQueueHashMap = new ConcurrentHashMap<>();
	}

	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		// so there wont be the same type in the queue
		synchronized (type) {
			if (!eventQueueHashMap_robin.containsKey(type)) {
				eventQueueHashMap_robin.put(type, new ConcurrentLinkedQueue<>());
			}
		}
		eventQueueHashMap_robin.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		// so there wont be the same type in the queue
		synchronized (type) {
			if (!broadcastQueueHashMap.containsKey(type)) {
				broadcastQueueHashMap.put(type, new ConcurrentLinkedQueue<>());
			}
		}
		broadcastQueueHashMap.get(type).add(m);
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		eventFutureHashMap.get(e).resolve(result);
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		if (broadcastQueueHashMap.get(b.getClass())==null) {
			return;
		}
		for ( MicroService m : broadcastQueueHashMap.get(b.getClass())){
			// the queue of the service is synced across the program
			synchronized(serviceQueueHashMap.get(m)) {  // TODO CHECK IF SYNCHRONIZED IS NEEDED
				serviceQueueHashMap.get(m).add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f = new Future<>();
		eventFutureHashMap.put(e,f);
		ConcurrentLinkedQueue<MicroService> robin = eventQueueHashMap_robin.get(e.getClass());
		if(robin==null) { // event that no one register for him
			complete(e, null);
			return f;
		}
		MicroService m;
		// needed to make sure the queue is removed and then added in the end
		synchronized (robin) //todo check
		{
			if (robin.isEmpty()) {
				complete(e, null);
				return f;
			}
			m = robin.remove();
			// avoid getting a null queue
			synchronized (serviceQueueHashMap.get(m)) {
				if (serviceQueueHashMap.get(m)==null) {
					complete(e, null);
				}
				else {
					serviceQueueHashMap.get(m).add(e);
				}
			}
			robin.add(m);
		}
		return f;
	}

	@Override
	public void register(MicroService m) {
		serviceQueueHashMap.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		for ( ConcurrentLinkedQueue<MicroService> q : eventQueueHashMap_robin.values()){
			// so a new event wont be added
			synchronized(q) {
				q.remove(m);
			}
		}
		for ( ConcurrentLinkedQueue<MicroService> q : broadcastQueueHashMap.values()){
			// so a new broadcast wont be added
			synchronized(q) {
				q.remove(m);
			}
		}

		//
		synchronized (serviceQueueHashMap.get(m)) {
			for (Message mes : serviceQueueHashMap.get(m)) {
				if (mes instanceof Event) {
					eventFutureHashMap.get(mes).resolve(null);
				}
			}
			serviceQueueHashMap.remove(m);
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> q= serviceQueueHashMap.get(m);
		Message msg = q.take();
		return msg;
	}

	

}
