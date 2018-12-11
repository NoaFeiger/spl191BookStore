package bgu.spl.mics;
import java.util.concurrent.atomic.AtomicInteger;

public class TickBroadcast implements Broadcast {
    private AtomicInteger tick;

    public TickBroadcast(int tick) {
        super();
        this.tick=new AtomicInteger(tick);
    }

    public AtomicInteger getTick() {
        return tick;
    }
}
