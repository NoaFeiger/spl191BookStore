package bgu.spl.mics.application.services;
import java.util.concurrent.CountDownLatch;

public class CountdownLatchWraper {
    CountDownLatch count;
    int numServices;

    private static class SingletonHolder {
        private static CountdownLatchWraper instance=new CountdownLatchWraper();
    }

    private CountdownLatchWraper(){
    }

    public void initializeNumServices(int num){
        numServices=num;
        count = new CountDownLatch(num);
    }
    public static CountdownLatchWraper getInstance(){
        return SingletonHolder.instance;
    }
    public void countDown(){
        count.countDown();
    }
    public void await(){
        try {
            count.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
