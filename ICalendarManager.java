import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ICalendarManager extends Remote {
    ICalendar getCalendar(String user) throws RemoteException;

    List<String> getUsers() throws RemoteException;
}
