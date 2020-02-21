package bgu.spl.mics;

public class TakeEvent<Boolean> implements Event<Boolean> {
    private String bookName;

    public TakeEvent(String bookName) {
        this.bookName = bookName;
    }

    public String getBookName() {
        return bookName;
    }
}
