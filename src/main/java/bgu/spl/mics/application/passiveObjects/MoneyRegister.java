package bgu.spl.mics.application.passiveObjects;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the store finance management. 
 * It should hold a list of receipts issued by the store.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class MoneyRegister {
	private static MoneyRegister instance = null;
	private BlockingQueue<OrderReceipt> orderReceipts;
	private AtomicInteger totalEarnings;

	private static class SingletonHolder {
		private static MoneyRegister instance = new MoneyRegister();
	}

	public static MoneyRegister getInstance() {
		return SingletonHolder.instance;
	}

	private MoneyRegister() {
		orderReceipts = new LinkedBlockingQueue<>();
		totalEarnings = new AtomicInteger(0);
	}

	/**
     * Retrieves the single instance of this class.
     */
//	public static MoneyRegister getInstance() {
//		if(instance == null) {
//			synchronized (MoneyRegister.class) {
//				if(instance == null) {
//					instance = new MoneyRegister();
//				}
//			}
//		}
//		return instance;
//	}
	
	/**
     * Saves an order receipt in the money register.
     * <p>   
     * @param r		The receipt to save in the money register.
     */
	public void file (OrderReceipt r) {
		orderReceipts.add(r);
		totalEarnings.addAndGet(r.getPrice());
//		synchronized (totalEarnings) {
//			totalEarnings += r.getPrice();
//		}
	}
	
	/**
     * Retrieves the current total earnings of the store.  
     */
	public int getTotalEarnings() {
		return totalEarnings.intValue();
	}
	
	/**
     * Charges the credit card of the customer a certain amount of money.
     * <p>
     * @param amount 	amount to charge
     */
	public void chargeCreditCard(Customer c, int amount) {
		c.chargeCreditCard(amount);
	}
	
	/**
     * Prints to a file named @filename a serialized object List<OrderReceipt> which holds all the order receipts 
     * currently in the MoneyRegister
     * This method is called by the main method in order to generate the output.
     */
	public void printOrderReceipts(String filename) {
		try
		{
			FileOutputStream fos =
					new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(orderReceipts);
			oos.close();
			fos.close();
		}catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
}
