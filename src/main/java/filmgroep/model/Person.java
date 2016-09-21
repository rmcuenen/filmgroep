package filmgroep.model;

public enum Person {

    Guido("#84aff4, #2758b3"),
    Marieke("#ff9ae7, #ff10c1"),
    Mirianne("#ec9b9d, #d73c3f"),
    Raymond("#f9e20a, #ff6b0a"),
    Willem("#59e738, #0aa908");

    private static final String STYLE_TEMPLATE = "-fx-background-insets: -1.4, 0, 1, 2; -fx-background-radius: 7px; -fx-background-color: linear-gradient(to left bottom, %s);";

    private final String style;

    private Person(String gradient) {
        this.style = String.format(STYLE_TEMPLATE, gradient);
    }

    public String getStyle() {
        return style;
    }

}
