package bgu.spl.mics.application.passiveObjects;

import java.io.Serializable;

public class OrderSchedule implements Serializable {
   private String bookName;
   private Integer tick;

   public OrderSchedule(String bookName, Integer tick){
        this.bookName = bookName;
        this.tick=tick;
   }

    public String getBookName() {
        return bookName;
    }

    public Integer getTick() {
        return tick;
    }
}
