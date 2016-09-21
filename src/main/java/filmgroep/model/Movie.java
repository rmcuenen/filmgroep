package filmgroep.model;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlSchemaType;

@XmlAccessorType(XmlAccessType.FIELD)
public class Movie implements Comparable<Movie> {

    public static final String POSTER_URL_FORMAT = "https://upload.wikimedia.org/wikipedia/en/%s";
    public static final String TRAILER_URL_FORMAT = "http://www.youtube.com/embed/%s?autoplay=1";
    private static final AtomicReference<Field> FIELD_SORTER = new AtomicReference<>();
    private static final AtomicBoolean REVERSED = new AtomicBoolean();

    static {
        setSorter("date", true);
    }

    public static void setSorter(String fieldName, boolean reversed) {
        try {
            final Field field = Movie.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            FIELD_SORTER.set(field);
            REVERSED.set(reversed);
        } catch (NoSuchFieldException | SecurityException ex) {
            throw new IllegalArgumentException(String.format("Cannot find field '%s'", fieldName), ex);
        }
    }

    @XmlElement(name = "Date", required = true)
    @XmlSchemaType(name = "date")
    private LocalDate date;

    @XmlElement(name = "By", required = true)
    private Person by;

    @XmlElement(name = "Title", required = true)
    private Title title;

    @XmlElement(name = "Title", required = true)
    @XmlElementWrapper(name = "Alternatives")
    private List<Title> alternatives;

    @XmlElement(name = "ReleaseDate", required = true)
    @XmlSchemaType(name = "date")
    private LocalDate releaseDate;

    @XmlElement(name = "RunningTime", required = true)
    @XmlSchemaType(name = "positiveInteger")
    private int runningTime;

    @XmlElement(name = "Plot", required = true)
    private String plot;

    @XmlElement(name = "PosterUrl")
    @XmlSchemaType(name = "anyURI")
    private Optional<String> posterUrl = Optional.empty();

    @XmlElement(name = "Trailer")
    private Optional<String> trailer = Optional.empty();

    public LocalDate getDate() {
        return date;
    }

    public Person getBy() {
        return by;
    }

    public Title getTitle() {
        return title;
    }

    public boolean hasAlternatives() {
        return alternatives != null;
    }

    public List<Title> getAlternatives() {
        if (alternatives == null) {
            throw new NoSuchElementException();
        }
        return alternatives;
    }

    public Optional<List<Title>> alternatives() {
        return Optional.ofNullable(alternatives);
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public int getRunningTime() {
        return runningTime;
    }

    public String getPlot() {
        return plot;
    }

    public boolean hasPosterUrl() {
        return posterUrl.isPresent();
    }

    public String getPosterUrl() {
        return posterUrl.get();
    }

    public Optional<String> posterUrl() {
        return posterUrl;
    }

    public boolean hasTrailer() {
        return trailer.isPresent();
    }

    public String getTrailer() {
        return trailer.get();
    }

    public Optional<String> trailer() {
        return trailer;
    }

    @Override
    public int hashCode() {
        return 41 * (41 * (41 * (41 * (41 * (41 * (41 * (41 * (41 + Objects.hashCode(date))
                + Objects.hashCode(by)) + Objects.hashCode(title)) + Objects.hashCode(alternatives))
                + Objects.hashCode(releaseDate)) + Objects.hashCode(runningTime))
                + Objects.hashCode(plot)) + Objects.hashCode(posterUrl)) + Objects.hashCode(trailer);
    }

    public boolean canEqual(Object other) {
        return (other instanceof Movie);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Movie) {
            Movie that = (Movie) obj;
            return that.canEqual(this)
                    && Objects.equals(this.date, that.date)
                    && Objects.equals(this.by, that.by)
                    && Objects.equals(this.title, that.title)
                    && Objects.equals(this.alternatives, that.alternatives)
                    && Objects.equals(this.releaseDate, that.releaseDate)
                    && Objects.equals(this.runningTime, that.runningTime)
                    && Objects.equals(this.plot, that.plot)
                    && Objects.equals(this.posterUrl, that.posterUrl)
                    && Objects.equals(this.trailer, that.trailer);
        }
        return false;
    }

    @Override
    public int compareTo(Movie that) {
        try {
            final Field field = FIELD_SORTER.get();
            final Comparable c1 = (Comparable) field.get(this);
            final Comparable c2 = (Comparable) field.get(that);
            return REVERSED.get() ? c2.compareTo(c1) : c1.compareTo(c2);
        } catch (IllegalArgumentException | IllegalAccessException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public String toString() {
        return String.format("Movie(%s,%s,%s,%s,%s,%s,%s,%s,%s)", date, by, title,
                alternatives, releaseDate, runningTime, plot, posterUrl, trailer);
    }

}
