import java.rmi.RemoteException;
import java.util.Timer;

// the client program
public class Client {

    // main method
    public static void main(String args[]) {
        if (args.length < 1) {
            System.out.println("Usage: java Client username");
            return;
        }
        UserInterface client = new UserInterface(args[0]);
        Timer timer = new Timer();
        // get alarms every 2 seconds
        timer.schedule(client, 2000, 2000);
        try {
            client.start();
        } catch (RemoteException e) {
            System.out.println(e.getMessage());
            System.exit(-1);
        }

    }
}
