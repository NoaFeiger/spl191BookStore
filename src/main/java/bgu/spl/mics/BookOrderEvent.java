package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent<T> implements Event<T> {
    private Customer customer;
    private String bookName;
    private int orderTick;

    public BookOrderEvent(Customer customer, String bookName, int orderTick) {
        this.customer = customer;
        this.bookName = bookName;
        this.orderTick = orderTick;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getBookName() {
        return bookName;
    }

    public int getOrderTick() {
        return orderTick;
    }

}
