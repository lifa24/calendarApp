import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.ExportException;

// the server program
public class Server {
    public static void main(String args[]) {
        // create registry
        try {
            try {
                LocateRegistry.createRegistry(1099);
            } catch (ExportException e) {
                System.out.println(e.getMessage());
            }
            // publish manager object
            ICalendarManager manager = new CalendarManager();
            Naming.bind("rmi://localhost:1099/CalendarManager", manager);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println("Ready...");
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
