package filmgroep.model;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "MovieList")
public class MovieList {

    public static MovieList create(URL url) throws Exception {
        try (InputStream input = url.openStream()) {
            final JAXBContext ctx = JAXBContext.newInstance(MovieList.class);
            final Unmarshaller unmarshaller = ctx.createUnmarshaller();
            return (MovieList) unmarshaller.unmarshal(input);
        }
    }

    @XmlElement(name = "Movie", required = true)
    private final List<Movie> movieList = new ArrayList<>();

    public List<Movie> getMovieList() {
        return movieList;
    }

    public Stream<Movie> movies() {
        return movieList.parallelStream();
    }

    public void write(Path path) throws Exception {
        final JAXBContext ctx = JAXBContext.newInstance(MovieList.class);
        final Marshaller marshaller = ctx.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(this, Files.newOutputStream(path));
    }

}
