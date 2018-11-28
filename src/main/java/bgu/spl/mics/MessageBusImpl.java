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
	private ConcurrentHashMap<Event<?>, Future> eventFutureHashMap;

	private MessageBusImpl() {
		serviceQueueHashMap = new ConcurrentHashMap<>();
		eventFutureHashMap = new ConcurrentHashMap<>();
		eventQueueHashMap_robin = new ConcurrentHashMap<>();
		broadcastListHashMap = new ConcurrentHashMap<>();
		System.out.println("messagebus ");


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
		synchronized (type) {
			if (!eventQueueHashMap_robin.containsKey(type)) {
				eventQueueHashMap_robin.put(type, new LinkedBlockingQueue<>());
			}
		}
		eventQueueHashMap_robin.get(type).add(m);
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		synchronized (type) {
			if (!broadcastListHashMap.containsKey(type)) {
				broadcastListHashMap.put(type, new LinkedList<>());
			}
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
			synchronized(m) {  // TODO CHECK IF SYNCHRONIZED IS NEEDED
				serviceQueueHashMap.get(m).add(b);
			//	m.notifyAll();
			}
		}

	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f = new Future<>();
		eventFutureHashMap.put(e,f);
		BlockingQueue<MicroService> robin = eventQueueHashMap_robin.get(e.getClass());
		MicroService m;
		synchronized (robin)
		{
			 m = robin.remove();
			robin.add(m);
		}
		serviceQueueHashMap.get(m).add(e);
//		synchronized (m)
//		{
//			m.notifyAll();
//		}
		return f;
	}

	@Override
	public void register(MicroService m) {
		serviceQueueHashMap.put(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		m.terminate();
		for ( BlockingQueue<MicroService> q : eventQueueHashMap_robin.values()){
			synchronized(q) {
				q.remove(m);
			}
		}
		for ( LinkedList<MicroService> l : broadcastListHashMap.values()){
			synchronized(l) {
				l.remove(m);
			}
		}
		synchronized(serviceQueueHashMap.get(m)) {
			for (Message mes : serviceQueueHashMap.get(m)) {
				if (mes instanceof Event) {
					eventFutureHashMap.get(mes).resolve(null);
				}
			}
		}
		serviceQueueHashMap.remove(m);
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
//		synchronized (m) {
//			while(serviceQueueHashMap.get(m).isEmpty()) {
//				m.wait();
//			}
		return serviceQueueHashMap.get(m).remove();
//		}
	}

	

}
