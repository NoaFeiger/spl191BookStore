package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer implements Serializable {
	private int id;
	private String name;
	private String address;
	private int distance;
	private List<OrderReceipt> Receipts;
	private int creditCard;
	private AtomicInteger availableAmountInCreditCard;
	private LinkedList<OrderSchedule> orders;

	public Customer (int id, String name, String address, int distance, int creditCard, int availableAmountInCreditCard, LinkedList<OrderSchedule> orders) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.distance = distance;
		this.creditCard = creditCard;
		this.availableAmountInCreditCard = new AtomicInteger(availableAmountInCreditCard);
		this.orders = orders;
		this.Receipts = new LinkedList<>();
	}
	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return name;
	}

	/**
     * Retrieves the ID of the customer  .
     */
	public int getId() {
		return id;
	}

	/**
     * Retrieves the address of the customer.
     */
	public String getAddress() {
		return address;
	}

	/**
     * Retrieves the distance of the customer from the store.
     */
	public int getDistance() {
		return distance;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		return Receipts;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		synchronized (availableAmountInCreditCard) {
			return availableAmountInCreditCard.intValue();
		}
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		return creditCard;
	}

	public void chargeCreditCard(int amount) {
		synchronized (availableAmountInCreditCard) {
			availableAmountInCreditCard.addAndGet(-1*amount);
		}
	}

	public LinkedList<OrderSchedule> getOrders() {
		return orders;
	}

	public AtomicInteger getAvailableAmountInCreditCard() {
		return availableAmountInCreditCard;
	}

	public void addReceipt(OrderReceipt o) {
		Receipts.add(o);
	}
}
