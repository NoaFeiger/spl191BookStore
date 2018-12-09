package bgu.spl.mics.application;
import bgu.spl.mics.MessageBus;
import bgu.spl.mics.MessageBusImpl;
import bgu.spl.mics.MicroService;
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
            System.out.println(bookTitle);
            Integer amount = book_info.get("amount").getAsInt();
            System.out.println(amount);
            Integer price = book_info.get("price").getAsInt();
            System.out.println(price);
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
            System.out.println(license);
            Integer speed = source.get("speed").getAsInt();
            System.out.println(speed);
            vehicle_array[i] = new DeliveryVehicle(license, speed);
        }

        //load to ResourcesHolder
        ResourcesHolder.getInstance().load(vehicle_array);

        JsonObject services = jo.getAsJsonObject("services");
        JsonObject time=services.get("time").getAsJsonObject();
        long speed=time.get("speed").getAsLong();
        System.out.println(speed);
        Integer duration=time.get("duration").getAsInt();
        System.out.println(duration);

        Integer selling_amount=services.get("selling").getAsInt();
        countServices = countServices + selling_amount;
        System.out.println(selling_amount);
        for (int i = 0; i < selling_amount; i++) {
            SellingService selling_service=new SellingService("selling " + i);
            servicesToRun.add(new Thread(selling_service));
        }

        Integer inventory_amount=services.get("inventoryService").getAsInt();
        countServices = countServices + inventory_amount;
        System.out.println(inventory_amount);
        for (int i = 0; i < inventory_amount; i++) {
            InventoryService inventory_service=new InventoryService("inventoryService " + i);
            servicesToRun.add(new Thread(inventory_service));
        }

        Integer logistic_amount=services.get("logistics").getAsInt();
        countServices = countServices + logistic_amount;
        System.out.println(logistic_amount);
        for (int i = 0; i < logistic_amount; i++) {
            LogisticsService logistic_service=new LogisticsService("logisticService " + i);
            servicesToRun.add(new Thread(logistic_service));
        }

        Integer resourcesService_amount=services.get("resourcesService").getAsInt();
        countServices = countServices + resourcesService_amount;
        System.out.println(resourcesService_amount);
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
            System.out.println(id);
            String name1 = customer.get("name").getAsString();
            System.out.println(name1);
            String address = customer.get("address").getAsString();
            System.out.println(address);
            Integer distance = customer.get("distance").getAsInt();
            System.out.println(distance);

            JsonObject credit_card=customer.get("creditCard").getAsJsonObject();
            Integer credit_num=credit_card.get("number").getAsInt();
            System.out.println(credit_num);
            Integer amount=credit_card.get("amount").getAsInt();
            System.out.println(amount);

            JsonArray order_scedule=customer.get("orderSchedule").getAsJsonArray();
            LinkedList<OrderSchedule> orders_list=new LinkedList<>();
            for (int j = 0; j< order_scedule.size(); j++) {
                JsonObject order = order_scedule.get(j).getAsJsonObject();
                String bookTitle_scedule = order.get("bookTitle").getAsString();
                System.out.println(bookTitle_scedule);
                Integer tick = order.get("tick").getAsInt();
                System.out.println(tick);
                orders_list.add(new OrderSchedule(bookTitle_scedule, tick));
            }
            customers[i] = new Customer(id,name1,address,distance,credit_num,amount,orders_list);
            CustomersMap.put(customers[i].getId(), customers[i]);
            APIService api_service = new APIService(customers[i], "APIService " + i);
            servicesToRun.add(new Thread(api_service));
        }
        System.out.println("Before1");
        TimeService time_service=new TimeService(speed,duration,"time",countServices);
        System.out.println("Before2");
        Thread timeThread = new Thread(time_service);
        timeThread.start();
        System.out.println("Before3");
        try {
            Thread.sleep(5000); //todo check
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Before");
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
        DeserializeHashmMap(args[1]);
        DeserializeConcurrentHashmMap(args[2]);

    }

    private static void printCustomersMap(HashMap customers, String filename) {
        try
        {
            FileOutputStream fos =
                    new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(customers);
            oos.close();
            fos.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    private static void DeserializeHashmMap(String filename) {
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