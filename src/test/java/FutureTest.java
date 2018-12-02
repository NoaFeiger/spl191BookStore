import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {
    Future<String> f = null;
    @Before
    public void setUp() {
        f = new Future<>();
    }


    @Test
    public void get() {
        f.resolve("good");
        assertEquals(f.get(), "good");
    }

    @Test
    public void resolve() {
       f.resolve("good");
       assertEquals("good",f.get());
    }

    @Test
    public void isDone() {
        assertFalse(f.isDone());
        f.resolve("good");
        assertTrue(f.isDone());
    }

    @Test
    public void get1() {
        assertNull(f.get(5, TimeUnit.SECONDS));
        f.resolve("good");
        assertEquals(f.get(5, TimeUnit.SECONDS), "good");
    }

    @After
    public void tearDown() throws Exception {
    }

}