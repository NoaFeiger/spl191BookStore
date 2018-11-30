package bgu.spl.mics.application.passiveObjects;

import com.google.gson.JsonArray;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a customer of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class Customer {
	private  int id;
	private String name;
	private String address;
	private Double distance;
	private String creditcard_num;
	private Double creditcard_amount;
	private LinkedList<OrderSchedule> orders;


	public Customer(int id, String name, String address, Double distance, String creditcard_num,
					Double creditcard_amount, LinkedList<OrderSchedule> orders) {
		this.id = id;
		this.name=name;
		this.address = address;
		this.distance = distance;
		this.creditcard_num = creditcard_num;
		this.creditcard_amount = creditcard_amount;
		this.orders = orders;
	}

	/**
     * Retrieves the name of the customer.
     */
	public String getName() {
		return this.name;
	}

	/**
     * Retrieves the ID of the customer  .
     */
	public int getId() {
		return this.id;
	}

	/**
     * Retrieves the address of the customer.
     */
	public String getAddress() {
		// TODO Implement this
		return null;
	}

	/**
     * Retrieves the distance of the customer from the store.
     */
	public int getDistance() {
		// TODO Implement this
		return 0;
	}

	
	/**
     * Retrieves a list of receipts for the purchases this customer has made.
     * <p>
     * @return A list of receipts.
     */
	public List<OrderReceipt> getCustomerReceiptList() {
		// TODO Implement this
		return null;
	}
	
	/**
     * Retrieves the amount of money left on this customers credit card.
     * <p>
     * @return Amount of money left.   
     */
	public int getAvailableCreditAmount() {
		// TODO Implement this
		return 0;
	}
	
	/**
     * Retrieves this customers credit card serial number.    
     */
	public int getCreditNumber() {
		// TODO Implement this
		return 0;
	}
	
}
