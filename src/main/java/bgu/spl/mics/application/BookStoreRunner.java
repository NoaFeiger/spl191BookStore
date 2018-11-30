package bgu.spl.mics.application;
import bgu.spl.mics.application.passiveObjects.BookInventoryInfo;
import bgu.spl.mics.application.passiveObjects.DeliveryVehicle;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.services.SellingService;
import bgu.spl.mics.application.services.TimeService;
import com.google.gson.*;
import java.io.FileReader;
import java.text.ParseException;
import java.io.FileNotFoundException;


/** This is the Main class of the application. You should parse the input file, 
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class BookStoreRunner {
    public static void main(String[] args) throws FileNotFoundException, ParseException { //TODO CHECK EXCEPTION
        // receive file name from the user
        String fileName = args[0];
        // read json file

        FileReader reader = new FileReader(fileName);
        JsonParser parser = new JsonParser();
        JsonObject jo = (JsonObject) parser.parse(reader);

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

        //
        //load to Inventory TODO

        // initialize source object


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

        JsonObject services = jo.getAsJsonObject("services");
        JsonObject time=services.get("time").getAsJsonObject();
        Double speed=time.get("speed").getAsDouble();
        System.out.println(speed);
        Double duration=time.get("duration").getAsDouble();
        System.out.println(duration);
        // TimeService time_service=new TimeService(speed,duration,"time"); //TODO CHECK NAME
        //load to ResourcesHolder TODO

        Integer selling_amount=services.get("selling").getAsInt();
        System.out.println(selling_amount);
      //  SellingService selling_service=new SellingService(selling_amount,"selling");

    }
}
