package bgu.spl.mics;

public class TakeEvent<Boolean> implements Event<Boolean> {
    private String bookname;

    public TakeEvent(String bookname) {
        this.bookname = bookname;
    }

    public String getBookname() {
        return bookname;
    }
}
