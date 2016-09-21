package filmgroep.model;

import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class Title implements Comparable<Title> {

    @XmlValue
    private String title;

    @XmlAttribute(name = "language")
    private String language;

    public String getTitle() {
        return title;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public int hashCode() {
        return 41 * (41 + Objects.hashCode(title)) + Objects.hashCode(language);
    }

    public boolean canEqual(Object other) {
        return (other instanceof Title);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Title) {
            Title that = (Title) obj;
            return that.canEqual(this)
                    && Objects.equals(this.title, that.title)
                    && Objects.equals(this.language, that.language);
        }
        return false;
    }

    @Override
    public int compareTo(Title that) {
        return this.title.compareTo(that.title);
    }

    @Override
    public String toString() {
        return String.format("Title(%s,%s)", title, language);
    }

}
