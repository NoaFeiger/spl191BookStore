package bgu.spl.mics;

public class CheckAvailabilityEvent<Integer> implements Event {
    private String bookName;
    public CheckAvailabilityEvent(String bookName) {
        this.bookName = bookName;
    }
    public String getBookName() {
        return bookName;
    }

}
