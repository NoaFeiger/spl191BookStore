package bgu.spl.mics.application.passiveObjects;

public class OrderSchedule {

    private String book_name;
   private Integer tick;

   public OrderSchedule(String book_name,Integer tick){
        this.book_name=book_name;
        this.tick=tick;
   }

    public String getBook_name() {
        return book_name;
    }

    public void setBook_name(String book_name) {
        this.book_name = book_name;
    }

    public Integer getTick() {
        return tick;
    }

    public void setTick(Integer tick) {
        this.tick = tick;
    }



}
