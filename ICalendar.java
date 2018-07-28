import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ICalendar extends Remote {
    List<Event> retrieveEvents(String user, Interval interval)
            throws RemoteException;

    void scheduleEvent(List<String> users, Event e) throws RemoteException;

    void deleteEvents(Interval interval) throws RemoteException;

    List<Event> getNotifications() throws RemoteException;

}
