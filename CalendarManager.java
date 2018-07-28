import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Implementation of ICalendarManager
 */
public class CalendarManager extends UnicastRemoteObject
        implements ICalendarManager, Serializable {
    Map<String, Calendar> calendars = new HashMap<String, Calendar>();

    /**
     * Constructor
     * 
     * @throws RemoteException
     */
    public CalendarManager() throws RemoteException {
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                synchronized (CalendarManager.this) {
                    // if not alive for 5 second, the client is offline
                    for (Calendar calendar : calendars.values()) {
                        calendar.life--;
                        if (calendar.life <= 0) {
                            calendar.online = false;
                        }
                    }
                }
            }
        };
        // start timer for check life time
        Timer timer = new Timer();
        timer.schedule(task, 1000, 1000);
    }

    @Override
    public ICalendar getCalendar(String user) throws RemoteException {
        // if calendar for user does not exist, create a calendar
        Calendar calendar = calendars.get(user);
        if (calendar == null) {
            calendar = new Calendar(this, user);
            calendars.put(user, calendar);
        } else {
            // only one client can be online for one calendar
            if (calendar.online) {
                throw new RemoteException("Duplicate");
            }
        }
        calendar.life = 5;
        calendar.online = true;
        return calendar;
    }

    public Calendar calendar(String user) {
        Calendar calendar = calendars.get(user);
        return calendar;
    }

    @Override
    public List<String> getUsers() throws RemoteException {
        List<String> users = new ArrayList<String>();
        users.addAll(calendars.keySet());
        return users;
    }

}
