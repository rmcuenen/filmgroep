package filmgroep;

import javafx.scene.image.Image;

public enum ImageResources {

    INFO("/info.png"),
    MENU("/menu.png"),
    SORT_ALPHABETICALLY("/sort_alphabetically.png"),
    SORT_BY_VIEW("/sort_by_view.png");

    private final Image image;

    private ImageResources(String resource) {
        image = new Image(ImageResources.class.getResourceAsStream(resource));
    }

    public Image getImage() {
        return image;
    }

}
