package bgu.spl.mics;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

public class TickBroadcast implements Broadcast {
    private AtomicInteger tick;
    private Timer timer;

    public TickBroadcast(int tick) {
        super();
        this.tick=new AtomicInteger(tick);
    }

    public AtomicInteger getTick() {
        return tick;
    }
}
