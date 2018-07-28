import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Interval implements Serializable {
    long start;
    long end;

    static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public Interval(Date start, Date end) {
        this.start = start.getTime();
        this.end = end.getTime();
    }

    public Interval(long start, long end) {
        this.start = start;
        this.end = end;
    }

    // check if a time point is in the interval
    public boolean in(long point) {
        return point >= start && point <= end;
    }

    // check if a interval contains the other interval
    public boolean contains(Interval other) {
        return in(other.start) && in(other.end);
    }

    // check if a interval conflicts with other interval
    public boolean conflict(Interval other) {
        return (in(other.start) && !in(other.end))
                || (!in(other.start) && in(other.end));
    }

    @Override
    public String toString() {
        return String.format("[%s - %s]", df.format(start), df.format(end));
    }
}
