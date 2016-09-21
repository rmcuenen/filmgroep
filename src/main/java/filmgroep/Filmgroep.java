package filmgroep;

import filmgroep.model.Movie;
import filmgroep.model.Person;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import static javafx.application.Application.launch;
import javafx.scene.web.WebView;

public class Filmgroep extends Application {

    private static final String MOVIE_URL = "https://rmcuenen.github.io/filmgroep/movies.xml";

    private void init(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Filmgroep");
        primaryStage.setResizable(false);
        Group root = new Group();
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles.css");
        primaryStage.setScene(scene);
        Accordion accordion = new Accordion();
        accordion.heightProperty().addListener((ob, ov, nv) -> {
            TitledPane view = accordion.getExpandedPane();
            if (view != null && nv.doubleValue() > ov.doubleValue()) {
                view.setExpanded(false);
                view.setExpanded(true);
            }
        });
        ScrollPane pane = new ScrollPane(accordion);
        pane.setPrefSize(640, 450);
        pane.setHbarPolicy(ScrollBarPolicy.NEVER);
        WebView player = new WebView();
        Region veil = new Region();
        veil.setOpacity(0);
        veil.setOnMouseClicked(e -> {
            player.getEngine().load("about:blank");
            player.setVisible(false);
        });
        veil.visibleProperty().bind(player.visibleProperty());
        player.setMaxSize(480, 320);
        player.setVisible(false);
        StackPane stack = new StackPane();
        BorderPane main = new BorderPane();
        stack.getChildren().addAll(main, veil, player);
        ToolBar toolbar = new ToolBar();
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        spacer.setMinWidth(Region.USE_PREF_SIZE);
        toolbar.getItems().addAll(createSortButton(accordion), spacer, createLegendButton(primaryStage));
        main.setTop(toolbar);
        main.setCenter(pane);
        Task<ObservableList<TitledPane>> task = createLoadingTask(root, pane, player);
        task.valueProperty().addListener((ob, ov, nv) -> {
            accordion.getPanes().addAll(nv);
            root.getChildren().replaceAll(n -> stack);
            primaryStage.sizeToScene();
        });
        new Thread(task).start();
    }

    private Task<ObservableList<TitledPane>> createLoadingTask(Group root, ScrollPane pane, WebView player) throws Exception {
        StackPane stack = new StackPane();
        stack.setPrefSize(640, 520);
        Rectangle veil = new Rectangle(320, 210);
        veil.setArcHeight(25);
        veil.setArcWidth(25);
        veil.setFill(new Color(0, 0, 0, 0.4));
        ProgressIndicator p = new ProgressIndicator();
        p.setStyle("-fx-progress-color: navy;");
        p.setMaxSize(105, 105);
        stack.getChildren().addAll(veil, p);
        root.getChildren().add(stack);
        Task<ObservableList<TitledPane>> task = new GetMoviesTask(pane, player, new URL(MOVIE_URL));
        p.progressProperty().bind(task.progressProperty());
        return task;
    }

    private MenuButton createSortButton(Accordion accordion) {
        Comparator<TitledPane> comparator = (t1, t2) -> {
            final Movie m1 = (Movie) t1.getUserData();
            final Movie m2 = (Movie) t2.getUserData();
            return m1.compareTo(m2);
        };
        MenuItem[] menus = {
            new MenuItem("Alphabetisch sorteren", new ImageView(ImageResources.SORT_ALPHABETICALLY.getImage())),
            new MenuItem("Sorteren op filmavond", new ImageView(ImageResources.SORT_BY_VIEW.getImage()))};
        String[] fields = {"date", "title"};
        AtomicInteger current = new AtomicInteger();
        MenuButton button = new MenuButton(null,
                new ImageView(ImageResources.MENU.getImage()), menus[0]);
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        for (MenuItem menu : menus) {
            menu.setOnAction(e -> {
                final int index = current.updateAndGet(i -> (i + 1) % 2);
                Movie.setSorter(fields[index], index == 0);
                button.getItems().replaceAll(m -> menus[index]);
                accordion.getPanes().sort(comparator);
                final TitledPane view = accordion.getExpandedPane();
                if (view != null) {
                    view.setExpanded(false);
                    view.setExpanded(true);
                }
            });
        }
        return button;
    }

    private Button createLegendButton(Stage primaryStage) {
        Button button = new Button(null, new ImageView(ImageResources.INFO.getImage()));
        button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        Stage stage = new Stage(StageStyle.TRANSPARENT);
        Group rootGroup = new Group();
        Scene scene = new Scene(rootGroup, 320, 210, Color.TRANSPARENT);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(primaryStage);
        stage.setScene(scene);
        Rectangle veil = new Rectangle(320, 210);
        veil.setArcHeight(25);
        veil.setArcWidth(25);
        veil.setFill(Color.NAVY);
        rootGroup.setOnMouseClicked(e -> stage.close());
        GridPane pane = new GridPane();
        pane.getColumnConstraints().addAll(new ColumnConstraints(150), new ColumnConstraints(150));
        pane.setPadding(new Insets(10));
        List<Label> labels = new ArrayList<>();
        double w = 0.0;
        for (Person p : Person.values()) {
            Region icon = new Region();
            SVGPath path = new SVGPath();
            path.setContent("M 0 0 h 7 l -3.5 4 z");
            icon.setShape(path);
            icon.setPrefSize(14, 14);
            icon.setStyle(p.getStyle());
            Text text = new Text(p.name());
            text.applyCss();
            w = Math.max(w, text.getLayoutBounds().getWidth());
            labels.add(new Label(p.name(), icon));
        }
        for (int i = 0; i < labels.size(); i++) {
            Label label = labels.get(i);
            int column = i % 2;
            int row = i / 2;
            label.setPadding(new Insets(0, 0, 0, 4));
            label.setStyle("-fx-text-fill: white;");
            label.setPrefSize(w + 25, 190 / Math.ceil(Person.values().length / 2.0));
            GridPane.setConstraints(label, column, row, 1, 1, HPos.CENTER, VPos.CENTER);
        }
        pane.getChildren().addAll(labels);
        rootGroup.getChildren().addAll(veil, pane);
        button.setOnAction(e -> stage.show());
        return button;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
