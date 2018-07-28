import java.io.Serializable;
import java.util.List;

public class Event implements Serializable, Comparable<Event> {
    public Interval interval;
    public String description;
    public String type;
    public List<String> group;

    // if the event has been notified to it's client, notified flag will be true
    public boolean notified;

    public Event(Interval interval, String description, String type,
            List<String> group) {
        this.interval = interval;
        this.description = description;
        this.type = type;
        this.group = group;
    }

    // public Event copy() {
    // Event event = new Event(interval, description, type, group);
    // return event;
    // }

    @Override
    public String toString() {
        return String.format("%s %s", interval, description);
    }

    @Override
    public int compareTo(Event o) {
        return (int) (interval.start - o.interval.start);
    }
}
