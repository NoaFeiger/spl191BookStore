package bgu.spl.mics.application.services;

import bgu.spl.mics.Callback;
import bgu.spl.mics.CheckAvailabiltyEvent;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.TakeEvent;
import bgu.spl.mics.application.passiveObjects.*;

/**
 * InventoryService is in charge of the book inventory and stock.
 * Holds a reference to the {@link Inventory} singleton of the store.
 * This class may not hold references for objects which it is not responsible for:
 * {@link ResourcesHolder}, {@link MoneyRegister}.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */

public class InventoryService extends MicroService{
	private Inventory inventory  = Inventory.getInstance();
	public InventoryService(String name) {
		super(name);
	}

	@Override
	protected void initialize() {
		subscribeEvent(CheckAvailabiltyEvent.class, new Callback<CheckAvailabiltyEvent>() {
			@Override
			public void call(CheckAvailabiltyEvent c) {
				int price = inventory.checkAvailabiltyAndGetPrice(c.getBookname());
				complete(c, price);
			}
		});
		subscribeEvent(TakeEvent.class, new Callback<TakeEvent>() {
			@Override
			public void call(TakeEvent c) {
				OrderResult or = inventory.take(c.getBookname());
				if (or==OrderResult.SUCCESSFULLY_TAKEN) {
					complete(c, true);
				}
				else {
					complete(c, false);
				}

			}
		});
	}

}
