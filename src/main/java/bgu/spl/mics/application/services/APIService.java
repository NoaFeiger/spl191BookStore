package bgu.spl.mics.application.services;

import bgu.spl.mics.*;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.passiveObjects.Customer;
import bgu.spl.mics.application.passiveObjects.OrderReceipt;
import bgu.spl.mics.application.passiveObjects.OrderSchedule;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * APIService is in charge of the connection between a client and the store.
 * It informs the store about desired purchases using {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class APIService extends MicroService{
	private HashMap<Integer, LinkedList<String>> TickBooksHashmap;
	private Customer customer;
	private LinkedList<OrderSchedule> orderSchedule;
	private LinkedList<Future<OrderReceipt>> Futures;
	public APIService() {
		super("APIService");
	}

	public APIService(Customer C, String name) {
		super(name);
		this.customer = C;
		this.orderSchedule = C.getOrders();
		this.Futures = new LinkedList<>();
		this.TickBooksHashmap = new HashMap<>();
		for (int i = 0; i < orderSchedule.size(); i++) {
			int tick = orderSchedule.get(i).getTick();
			if (!TickBooksHashmap.containsKey(tick)) {
				TickBooksHashmap.put(tick, new LinkedList<String>());
			}
			TickBooksHashmap.get(tick).add(orderSchedule.get(i).getBook_name());
		}
	}

	@Override
	protected void initialize() {
		subscribeBroadcast(TickBroadcast.class, new Callback<TickBroadcast>() {
			@Override
			public void call(TickBroadcast c) {
				int tick = c.getTick().intValue();
				if (TickBooksHashmap.containsKey(tick)) {
					LinkedList<String> books = TickBooksHashmap.get(tick);
					for (String bookname : books) {
						Future<OrderReceipt> fOrder =
								sendEvent(new BookOrderEvent<OrderReceipt>(customer,bookname,tick));
						Futures.add(fOrder);
					}
					for (Future<OrderReceipt> f : Futures) {
						OrderReceipt receipt = f.get();
						if (receipt!=null) {
							customer.addReciept(receipt);
							sendEvent(new DeliveryEvent<Boolean>(customer.getDistance(), customer.getAddress()));
						}
					}
				}
			}
		});
		subscribeBroadcast(TerminateBroadcast.class, new Callback<TerminateBroadcast>() {
			@Override
			public void call(TerminateBroadcast c) {
			    terminate();
			}
		});
		sendBroadcast( new FinishInitializeBroadcast());
	}

}
