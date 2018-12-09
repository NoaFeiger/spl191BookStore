package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Customer;

public class BookOrderEvent<T> implements Event<T> {
    private Customer customer;
    private String bookname;
    private int orderTick;

    public BookOrderEvent(Customer customer, String bookname, int orderTick) {
        this.customer = customer;
        this.bookname = bookname;
        this.orderTick = orderTick;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getBookname() {
        return bookname;
    }

    public int getOrderTick() {
        return orderTick;
    }

}
