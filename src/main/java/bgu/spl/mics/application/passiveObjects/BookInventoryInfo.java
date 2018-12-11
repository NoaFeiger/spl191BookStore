package bgu.spl.mics.application.passiveObjects;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Passive data-object representing a information about a certain book in the inventory.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add fields and methods to this class as you see fit (including public methods).
 */
public class BookInventoryInfo {

	private String  bookTitle;
	private int amountInInventory;
	private int price;
	private Object lockAmountInInventory;
	protected Semaphore semaphore;

	public BookInventoryInfo(String bookTitle, int amountInInventory, int price)
	{
		this.amountInInventory = amountInInventory;
		this.price=price;
		this.bookTitle=bookTitle;
		lockAmountInInventory = new Object();
		semaphore = new Semaphore(amountInInventory);
	}

	/**
     * Retrieves the title of this book.
     * <p>
     * @return The title of this book.   
     */
	public String getBookTitle() {
		return bookTitle;
	}

	/**
     * Retrieves the amount of books of this type in the inventory.
     * <p>
     * @return amount of available books.      
     */
	public int getAmountInInventory() {
		synchronized (lockAmountInInventory) {
			return amountInInventory;
		}
	}

	/**
     * Retrieves the price for  book.
     * <p>
     * @return the price of the book.
     */
	public int getPrice() {
		return price;
	}
	
	public void reduceAmount() {
		synchronized (lockAmountInInventory) { //todo check
			amountInInventory -= 1;
		}
	}
}
