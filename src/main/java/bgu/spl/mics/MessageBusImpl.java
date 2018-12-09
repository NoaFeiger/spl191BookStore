package bgu.spl.mics;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.BlockingQueue;
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
	private ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcastQueueHashMap;
	private ConcurrentHashMap<Event<?>, Future> eventFutureHashMap;

	private MessageBusImpl() {
		serviceQueueHashMap = new ConcurrentHashMap<>();
		eventFutureHashMap = new ConcurrentHashMap<>();
		eventQueueHashMap_robin = new ConcurrentHashMap<>();
		broadcastQueueHashMap = new ConcurrentHashMap<>();
		//System.out.println("messagebus ");
	}

	private static class SingletonHolder {
		private static MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}
//	public static MessageBusImpl getInstance() {
//		if(instance == null) {
//			synchronized (MessageBusImpl.class) {
//				if(instance == null) {
//					instance = new MessageBusImpl();
//				}
//			}
//		}
//		return instance;
//	}

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
			if (!broadcastQueueHashMap.containsKey(type)) {
				broadcastQueueHashMap.put(type, new LinkedBlockingQueue<>());
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
			synchronized(serviceQueueHashMap.get(m)) {  // TODO CHECK IF SYNCHRONIZED IS NEEDED
				serviceQueueHashMap.get(m).add(b);
			}
		}
	}

	
	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		Future<T> f = new Future<>();
		eventFutureHashMap.put(e,f);
		BlockingQueue<MicroService> robin = eventQueueHashMap_robin.get(e.getClass());
		if(robin==null) // event that no one register for him
			return  null;
		MicroService m;
		synchronized (robin)
		{
			try {
				m = robin.take();
				serviceQueueHashMap.get(m).add(e);
				robin.add(m);
			}
			catch (InterruptedException g){
				System.out.println(g.getMessage());
			}
		}
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
		for ( BlockingQueue<MicroService> q : broadcastQueueHashMap.values()){
			synchronized(q) {
				q.remove(m);
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
	/*
		synchronized (m) {

			while (serviceQueueHashMap.get(m).isEmpty()) {
				m.wait();
			}
		}
		*/
		BlockingQueue<Message> q= serviceQueueHashMap.get(m);
		Message msg = q.take();
		return msg;
	}

	

}
