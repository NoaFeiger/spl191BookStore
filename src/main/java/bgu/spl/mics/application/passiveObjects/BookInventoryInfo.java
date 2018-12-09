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
	private AtomicInteger amountInInventory;
	private int price;
//	private ReadWriteLock rwLock;
//	private Lock writeLock;
//	private Lock readLock;
	protected Semaphore semaphore;

	public BookInventoryInfo(String bookTitle, int amountInInventory, int price)
	{
		this.amountInInventory = new AtomicInteger(amountInInventory);
		this.price=price;
		this.bookTitle=bookTitle;
//		rwLock = new ReentrantReadWriteLock();
//		writeLock = rwLock.writeLock();
//		readLock = rwLock.readLock();
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
		synchronized (amountInInventory) {
			return amountInInventory.intValue();
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
		System.out.println("");
		System.out.println("beforereducesync");
		System.out.println("");
		synchronized (amountInInventory) {
			System.out.println("");
			System.out.println("inreduce");
			System.out.println("");
			amountInInventory.decrementAndGet();
		}
	}
}
