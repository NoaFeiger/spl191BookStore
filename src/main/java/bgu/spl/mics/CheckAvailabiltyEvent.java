package bgu.spl.mics;

public class CheckAvailabiltyEvent<Integer> implements Event {
    private String bookname;
    public CheckAvailabiltyEvent (String bookname) {
        this.bookname = bookname;
    }
    public String getBookname() {
        return bookname;
    }

}
