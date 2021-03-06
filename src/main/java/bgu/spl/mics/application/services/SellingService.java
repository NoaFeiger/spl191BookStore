package bgu.spl.mics.application.services;

import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.*;

/**
 * Selling service in charge of taking orders from customers.
 * Holds a reference to the {@link MoneyRegister} singleton of the store.
 * Handles {@link BookOrderEvent}.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link Inventory}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class SellingService extends MicroService{
	private int proccessTick;
	private int issuedTick;
	private MoneyRegister moneyRegister = MoneyRegister.getInstance();
	public SellingService(String name) {
		super(name);
	}

	@Override
	protected void initialize() {
		subscribeEvent(BookOrderEvent.class, c -> {
			Future<Integer> fProccessTick = sendEvent(new TimeRequestEvent<>());
			if (fProccessTick==null) {
				complete(c, null);
				return;
			}
			proccessTick = fProccessTick.get();
			Future<Integer> fPrice = sendEvent(new CheckAvailabilityEvent<Integer>(c.getBookName()));
			if (fPrice==null) {
				complete(c, null);
				return;
			}
			int price = fPrice.get();
			if (price==-1) {
				complete(c, null);
				return;
			}

			//sync because there is no possibility to change the money of the customer while we take book
			synchronized (c.getCustomer().getAvailableAmountInCreditCard()) {
				int money = c.getCustomer().getAvailableAmountInCreditCard().intValue();
				if (money-price>=0) {
					Future<Boolean> fTake = sendEvent(new TakeEvent<>(c.getBookName()));
					if (fTake==null) {
						complete(c, null);
						return;
					}
					Boolean answer = fTake.get();
					if (answer) {
						moneyRegister.chargeCreditCard(c.getCustomer(), price);
						Future<Integer> fIssued = sendEvent(new TimeRequestEvent<Integer>());
						if (fIssued==null) {
							complete(c, null);
							return;
						}
						issuedTick = fIssued.get();
						Customer customer = c.getCustomer();
						OrderReceipt orderReceipt = new OrderReceipt
								(getName(), customer.getId(), c.getBookName(),
										price, issuedTick, c.getOrderTick(), proccessTick);
						moneyRegister.file(orderReceipt);
						complete(c, orderReceipt);
					}
					else {
						complete(c, null);
					}
				}
				else {
					complete(c, null);
				}
			}
		});

		subscribeBroadcast(TerminateBroadcast.class, c -> terminate());
		sendBroadcast( new FinishInitializeBroadcast());

	}

}
