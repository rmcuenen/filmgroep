package filmgroep.model;

import java.util.Optional;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class OptionalAdapter extends XmlAdapter<String, Optional<String>> {

    @Override
    public String marshal(Optional<String> v) throws Exception {
        return v.orElse(null);
    }

    @Override
    public Optional<String> unmarshal(String v) throws Exception {
        if (v == null || v.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(v);
    }

}
