import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.io.*;

import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.*;

public class InventoryTest {
    Inventory inv = null;
    @Before
    public void setUp() throws Exception {
        inv=Inventory.getInstance();
    }

    @Test
    public void getInstance() {
        Inventory check=Inventory.getInstance();
        assertNotNull(check);
        assertEquals(inv,check);
    }

    // gets an array and initialize books
    @Test
    public void load() {
        BookInventoryInfo[] inventory=new BookInventoryInfo[1];
        inventory[0] = new BookInventoryInfo("caspion",5,50 );
        inv.load(inventory);
        assertEquals(50,inv.checkAvailabiltyAndGetPrice("caspion"));
        assertNotEquals(30,inv.checkAvailabiltyAndGetPrice("noa"));
    }

    @Test
    public void take() {
        BookInventoryInfo[] inventory=new BookInventoryInfo[1];
        inventory[0] = new BookInventoryInfo("caspion",1,50 );
        inv.load(inventory);
        inv.take("caspion");
        assertEquals(-1,inv.checkAvailabiltyAndGetPrice("caspion"));

    }

    @Test
    public void checkAvailabiltyAndGetPrice() {
        BookInventoryInfo[] inventory=new BookInventoryInfo[1];
        inventory[0] = new BookInventoryInfo("caspion",1,50 );
        inv.load(inventory);
        assertEquals(50,inv.checkAvailabiltyAndGetPrice("caspion"));
        assertNotEquals(40,inv.checkAvailabiltyAndGetPrice("caspion"));
    }

    @Test
    public void printInventoryToFile() {
        BookInventoryInfo[] inventory=new BookInventoryInfo[1];
        inventory[0] = new BookInventoryInfo("caspion",1,50 );
        inv.load(inventory);
        inv.printInventoryToFile("hashmap.ser");
        ConcurrentHashMap<String, BookInventoryInfo> map = null;
        try
        {
            FileInputStream fis = new FileInputStream("hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (ConcurrentHashMap) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
            return;
        }catch(ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }
        assertEquals(inventory[0], map.get("caspion"));
    }


    @After
    public void tearDown() throws Exception {
    }
}