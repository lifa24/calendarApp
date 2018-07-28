import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;

//The text-based user interface
public class UserInterface extends TimerTask {
    String name;
    Scanner scanner = new Scanner(System.in);
    ICalendarManager manager;
    ICalendar calendar;
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    /**
     * Constructor
     * 
     * @param name
     *            user name
     */
    public UserInterface(String name) {
        this.name = name;
    }

    /**
     * helper method for inputing date
     * 
     * @param prompt
     * @return
     */
    Date inputDate(String prompt) {
        while (true) {
            System.out.print(prompt + ":(yyyy-MM-dd HH:mm) ");
            String s = scanner.nextLine();
            try {
                return df.parse(s);
            } catch (Exception e) {
            }
        }
    }

    /**
     * helper method for inputing interval
     * 
     * @return
     */
    Interval inputInterval() {
        Date start = inputDate("Start time");
        Date end = inputDate("End time");
        Interval interval = new Interval(start, end);
        return interval;
    }

    /**
     * helper method for inputing user names
     * 
     * @return
     */
    List<String> inputNames() {
        System.out.println("Input user names one in a line, blank line to end");
        List<String> names = new ArrayList<String>();
        while (true) {
            String name = scanner.nextLine().trim();
            if (name.isEmpty()) {
                break;
            }
            names.add(name);
        }
        return names;
    }

    /**
     * show calendar for a user
     * 
     * @param name
     */
    void showCalendar(String name) {
        if (this.name.equals(name)) {
            name = null;
        }
        List<Event> events;
        try {
            events = calendar.retrieveEvents(name, inputInterval());
            for (Event event : events) {
                System.out.println(event);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * entrance of client
     * 
     * @throws RemoteException
     */
    public void start() throws RemoteException {
        try {
            manager = (ICalendarManager) Naming
                    .lookup("rmi://localhost:1099/CalendarManager");
            calendar = manager.getCalendar(name);
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        System.out.println("                    Calendar User Interface");
        while (true) {
            // show menu
            System.out.println("1.Show users");
            System.out.println("2.Show my calendar");
            System.out.println("3.Show other's calendar");
            System.out.println("4.Add public event");
            System.out.println("5.Add private event");
            System.out.println("6.Add open event");
            System.out.println("7.Add group event");
            System.out.println("8.Delete Event");
            System.out.println("0.Exit");
            System.out.print("Selection: ");

            int selection = -1;
            while (true) {
                try {
                    String s = scanner.nextLine();
                    selection = Integer.parseInt(s);
                } catch (Exception e) {

                }
                if (selection >= 0 && selection <= 8) {
                    break;
                }
            }
            switch (selection) {
            case 1:
                try {
                    System.out.println(manager.getUsers());
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
                break;

            case 2:
                showCalendar(null);
                System.out.println();
                break;

            case 3:
                System.out.print("User name: ");
                String name = scanner.nextLine();
                showCalendar(name);
                System.err.println();
                break;

            case 4: {
                Interval interval = inputInterval();
                System.out.print("Description: ");
                String description = scanner.nextLine();
                Event event = new Event(interval, description, "public", null);
                try {
                    calendar.scheduleEvent(null, event);
                } catch (RemoteException e) {
                    System.out.println("Conflict");
                }
                break;
            }
            case 5: {
                Interval interval = inputInterval();
                System.out.print("Description: ");
                String description = scanner.nextLine();
                Event event = new Event(interval, description, "private", null);
                try {
                    calendar.scheduleEvent(null, event);
                } catch (RemoteException e) {
                    System.out.println("Conflict");
                }
                break;
            }

            case 6: {
                Interval interval = inputInterval();
                Event event = new Event(interval, "open", "open", null);
                try {
                    calendar.scheduleEvent(null, event);
                } catch (RemoteException e) {
                    System.out.println("Conflict");
                }
                break;
            }

            case 7: {
                Interval interval = inputInterval();
                System.out.print("Description: ");
                String description = scanner.nextLine();
                List<String> names = inputNames();
                Event event = new Event(interval, description, "group", names);
                try {
                    calendar.scheduleEvent(names, event);
                } catch (RemoteException e) {
                    System.out.println("Conflict");
                }
                break;
            }

            case 8: {
                Interval interval = inputInterval();
                try {
                    calendar.deleteEvents(interval);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            }

            case 0:
                System.exit(0);
                break;

            default:
                break;
            }
        }

    }

    @Override
    public void run() {
        // timer task
        List<Event> events;
        try {
            // get alarms
            events = calendar.getNotifications();
            if (events.isEmpty()) {
                return;
            }
            System.out.println("************Alarm************");
            for (Event event : events) {
                System.out.println(event);
            }
            System.out.println();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
