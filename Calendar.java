import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

/**
 * Implementation of ICalendar
 *
 */
public class Calendar extends UnicastRemoteObject
        implements ICalendar, Serializable {
    // the manager
    CalendarManager manager;

    // user name
    String user;

    // the life time
    int life;

    // flag indicates if the interface is online
    boolean online;

    // event buffer
    TreeSet<Event> events = new TreeSet<Event>();

    /**
     * Constructor
     * 
     * @param manager
     *            the manager
     * @throws RemoteException
     */
    public Calendar(CalendarManager manager, String user)
            throws RemoteException {
        this.manager = manager;
        this.user = user;
    }

    synchronized public List<Event> retrieveEvents(String user,
            Interval interval) throws RemoteException {

        List<Event> result = new ArrayList<Event>();
        // local calendar
        if (user == null) {
            for (Event event : events) {
                if (interval.contains(event.interval)) {
                    result.add(event);
                }
            }
        } else {
            // other's calendar
            Calendar other = manager.calendar(user);
            if (other != null) {
                for (Event event : other.events) {
                    if (interval.contains(event.interval)) {
                        if (event.type.equals("public")) {
                            result.add(event);
                        } else if (event.type.equals("private")) {
                            continue;
                        } else if (event.type.equals("group")) {
                            if (event.group.contains(this.user)) {
                                result.add(event);
                            } else {
                                // only can see the time for non-members
                                Event e = new Event(event.interval, "***",
                                        event.type, event.group);
                                result.add(e);
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    synchronized public void scheduleEvent(List<String> users, Event e)
            throws RemoteException {
        if (e.type.equals("group")) {
            // add group event
            for (String name : users) {
                Calendar user = manager.calendar(name);
                for (Event event : user.events.toArray(new Event[0])) {
                    // search for open event that contains the interval of the
                    // group event
                    if (event.type.equals("open")
                            && event.interval.contains(e.interval)) {
                        user.events.add(e);
                        user.events.remove(event);
                        if (event.interval.start < e.interval.start) {
                            user.events.add(new Event(
                                    new Interval(event.interval.start,
                                            e.interval.start),
                                    "open", "open", null));
                        }
                        if (e.interval.end < event.interval.end) {
                            user.events.add(new Event(
                                    new Interval(e.interval.end,
                                            event.interval.end),
                                    "open", "open", null));
                        }
                    }
                }
            }
        } else {
            // check conflicting
            for (Event event : events) {
                if (e.interval.conflict(event.interval)) {
                    throw new RemoteException("Conflict");
                }
            }
            events.add(e);
        }
    }

    // delete events in interval
    synchronized public void deleteEvents(Interval interval)
            throws RemoteException {
        for (Event event : events.toArray(new Event[0])) {
            if (interval.contains(event.interval)) {
                events.remove(event);
            }
        }
    }

    @Override
    synchronized public List<Event> getNotifications() throws RemoteException {
        // get alarms and tell the server the client is alive
        life = 5;
        online = true;
        List<Event> result = new ArrayList<Event>();
        long point = new Date().getTime();
        for (Event event : events) {
            if (event.type.equals("open")) {
                continue;
            }
            if (event.interval.start <= point && !event.notified) {
                event.notified = true;
                result.add(event);
            }
        }
        return result;
    }
}
