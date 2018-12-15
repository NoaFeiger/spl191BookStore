package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.*;

import java.io.*;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/** This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) throws FileNotFoundException, ParseException { //TODO CHECK EXCEPTION
        // todo check if including timeservice
        int countServices = 0;
        // receive file name from the user
        String fileName = args[0];
        // read json file

        FileReader reader = new FileReader(fileName);
        JsonParser parser = new JsonParser();
        JsonObject jo = (JsonObject) parser.parse(reader);
        //
        LinkedList<Thread> servicesToRun = new LinkedList<>();
        HashMap<Integer, Customer> CustomersMap = new HashMap<>();
        //initialize bookInventoryInfo
        JsonArray je_bookInfo = jo.get("initialInventory").getAsJsonArray();
        BookInventoryInfo[] bookInventoryInfos = new BookInventoryInfo[je_bookInfo.size()];
        for (int i = 0; i < je_bookInfo.size(); i++) {
            JsonObject book_info = (JsonObject) je_bookInfo.get(i);
            String bookTitle=book_info.get("bookTitle").getAsString();
            Integer amount = book_info.get("amount").getAsInt();
            Integer price = book_info.get("price").getAsInt();
            bookInventoryInfos[i] = new BookInventoryInfo(bookTitle, amount,price);
        }

        //load to Inventory
        Inventory.getInstance().load(bookInventoryInfos);

        JsonArray je_resources=jo.get("initialResources").getAsJsonArray();
        JsonArray vehicles= je_resources.get(0).getAsJsonObject().get("vehicles").getAsJsonArray();
        DeliveryVehicle[] vehicle_array = new DeliveryVehicle[vehicles.size()];
        for (int i = 0; i < vehicles.size(); i++) {
            JsonObject source = (JsonObject) vehicles.get(i);
            Integer license = source.get("license").getAsInt();
            Integer speed = source.get("speed").getAsInt();
            vehicle_array[i] = new DeliveryVehicle(license, speed);
        }

        //load to ResourcesHolder
        ResourcesHolder.getInstance().load(vehicle_array);

        JsonObject services = jo.getAsJsonObject("services");
        JsonObject time=services.get("time").getAsJsonObject();
        long speed=time.get("speed").getAsLong();
        Integer duration=time.get("duration").getAsInt();

        Integer selling_amount=services.get("selling").getAsInt();
        countServices = countServices + selling_amount;
        for (int i = 0; i < selling_amount; i++) {
            SellingService selling_service=new SellingService("selling " + i);
            servicesToRun.add(new Thread(selling_service));
        }

        Integer inventory_amount=services.get("inventoryService").getAsInt();
        countServices = countServices + inventory_amount;
        for (int i = 0; i < inventory_amount; i++) {
            InventoryService inventory_service=new InventoryService("inventoryService " + i);
            servicesToRun.add(new Thread(inventory_service));
        }

        Integer logistic_amount=services.get("logistics").getAsInt();
        countServices = countServices + logistic_amount;
        for (int i = 0; i < logistic_amount; i++) {
            LogisticsService logistic_service=new LogisticsService("logisticService " + i);
            servicesToRun.add(new Thread(logistic_service));
        }

        Integer resourcesService_amount=services.get("resourcesService").getAsInt();
        countServices = countServices + resourcesService_amount;
        for (int i = 0; i < resourcesService_amount; i++) {
            ResourceService resource_service=new ResourceService("resourcesService " + i);
            servicesToRun.add(new Thread(resource_service));
        }

        JsonArray customers_array = services.get("customers").getAsJsonArray();
        Customer[] customers = new Customer[customers_array.size()];
        countServices = countServices + customers_array.size();
        for (int i = 0; i < customers_array.size(); i++) {
            JsonObject customer = customers_array.get(i).getAsJsonObject();
            int id=customer.get("id").getAsInt(); //TODO CHECK INT
            String name1 = customer.get("name").getAsString();
            String address = customer.get("address").getAsString();
            Integer distance = customer.get("distance").getAsInt();

            JsonObject credit_card=customer.get("creditCard").getAsJsonObject();
            Integer credit_num=credit_card.get("number").getAsInt();
            Integer amount=credit_card.get("amount").getAsInt();

            JsonArray order_scedule=customer.get("orderSchedule").getAsJsonArray();
            LinkedList<OrderSchedule> orders_list=new LinkedList<>();
            for (int j = 0; j< order_scedule.size(); j++) {
                JsonObject order = order_scedule.get(j).getAsJsonObject();
                String bookTitle_scedule = order.get("bookTitle").getAsString();
                Integer tick = order.get("tick").getAsInt();
                orders_list.add(new OrderSchedule(bookTitle_scedule, tick));
            }
            customers[i] = new Customer(id,name1,address,distance,credit_num,amount,orders_list);
            CustomersMap.put(customers[i].getId(), customers[i]);
            APIService api_service = new APIService(customers[i], "APIService " + i);
            servicesToRun.add(new Thread(api_service));
        }
        TimeService time_service=new TimeService(speed,duration,"time",countServices);
        Thread timeThread = new Thread(time_service);
        timeThread.start();
        synchronized (time_service) {
            while (!time_service.getReady()) {
                try {
                   time_service.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        for (Thread thread : servicesToRun) {
            thread.start();
        }
        try {
            timeThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (Thread thread : servicesToRun) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        printCustomersMap(CustomersMap, args[1]);
        Inventory.getInstance().printInventoryToFile(args[2]);
        MoneyRegister.getInstance().printOrderReceipts(args[3]);
        printMoneyRegister(MoneyRegister.getInstance(), args[4]);
        DeserializeHashMap(args[1]);
        DeserializeHashMap(args[2]);
        DeserializeOrderReceipts(args[3]);
        DeserializeMoneyRegister(args[4]);


    }
    private static void DeserializeOrderReceipts(String filename) {
        List list = new LinkedList();
        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (List) ois.readObject();
            ois.close();
        }
        catch(IOException ioe)
        {
            System.out.println("NOT WRITING");
            ioe.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Iterator iterator = list.iterator();
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
        }
    }
    private static void DeserializeMoneyRegister(String filename) {
        MoneyRegister moneyRegister = null;
        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            moneyRegister = (MoneyRegister) ois.readObject();
            ois.close();
        }
        catch(IOException ioe)
        {
            System.out.println("NOT WRITING");
            ioe.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

//        Iterator iterator = list.iterator();
//        while(iterator.hasNext()) {
//            System.out.println(iterator.next());
//        }
        System.out.println(moneyRegister.getTotalEarnings());
    }
    private static void printCustomersMap(HashMap customers, String filename) {
        try
        {
            File f = new File(filename);
            FileOutputStream fos =
                    new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(customers);
            oos.close();
            fos.close();
        }catch(IOException ioe)
        {
            System.out.println("NOT WRITING");
            ioe.printStackTrace();
        }
    }
    private static void printMoneyRegister(MoneyRegister moneyRegister, String filename) {
        try
        {
            File f = new File(filename);
            FileOutputStream fos =
                    new FileOutputStream(f);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(moneyRegister);
            oos.close();
            fos.close();
        }catch(IOException ioe)
        {
            System.out.println("NOT WRITING");
            ioe.printStackTrace();
        }
    }
    private static void DeserializeHashMap(String filename) {
        HashMap map;
        try
        {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
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
        System.out.println("Deserialized HashMap..");
        // Display content using Iterator
        Set set = map.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            Map.Entry mentry = (Map.Entry)iterator.next();
            System.out.print("key: "+ mentry.getKey() + " & Value: ");
            System.out.println(mentry.getValue());
        }
    }
    private static void DeserializeConcurrentHashmMap(String filename) {
        ConcurrentHashMap map;
        try
        {
            FileInputStream fis = new FileInputStream(filename);
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
        System.out.println("Deserialized HashMap..");
        // Display content using Iterator
        Set set = map.entrySet();
        Iterator iterator = set.iterator();
        while(iterator.hasNext()) {
            ConcurrentHashMap.Entry mentry = (ConcurrentHashMap.Entry)iterator.next();
            System.out.print("key: "+ mentry.getKey() + " & Value: ");
            System.out.println(mentry.getValue());
        }
    }
}