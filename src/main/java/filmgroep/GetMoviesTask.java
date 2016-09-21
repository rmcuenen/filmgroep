package filmgroep;

import filmgroep.model.Movie;
import filmgroep.model.MovieList;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;

public class GetMoviesTask extends Task<ObservableList<TitledPane>> {

    private final ScrollPane scroll;
    private final WebView player;
    private final URL listURI;

    public GetMoviesTask(ScrollPane scroll, WebView player, URL listURL) {
        this.scroll = scroll;
        this.player = player;
        this.listURI = listURL;
    }

    @Override
    protected ObservableList<TitledPane> call() throws Exception {
        final List<Movie> movieList = MovieList.create(listURI).getMovieList();
        final ObservableList<TitledPane> movies = FXCollections.observableArrayList();
        int progress = 0;
        for (Movie movie : movieList) {
            updateProgress(progress++, movieList.size());
            final StringBuilder sb = new StringBuilder();
            sb.append(movie.getTitle().getTitle()).append(" (")
                    .append(movie.getReleaseDate().getYear()).append(')');
            final TitledPane pane = new TitledPane(sb.toString(), createMoviePane(movie));
            pane.setUserData(movie);
            pane.setAnimated(false);
            pane.expandedProperty().addListener((ob, ov, nv) -> {
                if (nv) {
                    Collections.sort(movieList);
                    final double i = movieList.indexOf(movie);
                    final double y = i / movieList.size() + i / 10920.0;
                    scroll.setVvalue(y);
                }
            });
            pane.layoutBoundsProperty().addListener((ob, ov, nv) -> {
                final Node arrow = pane.lookup(".arrow");
                arrow.setEffect(null);
                arrow.setStyle(movie.getBy().getStyle());
            });
            movies.add(pane);
            Thread.sleep(5);
        }
        return movies;
    }

    private StackPane createMoviePane(Movie movie) {
        StackPane stack = new StackPane();
        BorderPane moviePane = new BorderPane();
        moviePane.setPrefSize(620, 390);
        moviePane.setStyle("-fx-background-color: snow;");
        stack.getChildren().add(moviePane);
        Image im = new Image(String.format(Movie.POSTER_URL_FORMAT, movie.posterUrl().orElse("")),
                220, 0, true, true, true);
        ImageView view = new ImageView(im);
        HBox poster = new HBox(view);
        im.errorProperty().addListener((ob, ov, nv) -> view.setImage(new WritableImage(220, 313)));
        im.heightProperty().addListener((ob, ov, nv) -> {
            final double margin = (390.0 - nv.doubleValue()) / 2.0;
            poster.setPadding(new Insets(margin, 10, margin, 10));
        });
        GridPane info = new GridPane();
        info.setAlignment(Pos.BASELINE_LEFT);
        info.setHgap(10);
        info.setPadding(new Insets(15));
        Label title = new Label(movie.getTitle().getTitle());
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        title.setWrapText(true);
        info.add(title, 0, 0, 2, 1);
        StringBuilder sb = new StringBuilder();
        movie.alternatives().ifPresent(l -> l.stream().forEach(t -> sb.append(t.getTitle()).append(' ')));
        Text subtitle = new Text(sb.toString());
        subtitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 8));
        info.add(subtitle, 0, 1, 2, 1);
        Region fill = new Region();
        fill.setPrefHeight(15);
        info.add(fill, 0, 2, 2, 1);
        info.add(new Label("Filmavond:"), 0, 3);
        info.add(new Label(movie.getDate().toString()), 1, 3);
        info.add(new Label("Speelduur:"), 0, 4);
        info.add(new Label(movie.getRunningTime() + " minuten"), 1, 4);
        info.add(new Label("Première:"), 0, 5);
        info.add(new Label(movie.getReleaseDate().toString()), 1, 5);
        Label pl = new Label("Plot:");
        GridPane.setValignment(pl, VPos.TOP);
        info.add(pl, 0, 6);
        Label plot = new Label(movie.getPlot());
        plot.setPrefWidth(240);
        plot.setWrapText(true);
        info.add(plot, 1, 6);
        if (movie.hasTrailer()) {
            Hyperlink trailer = new Hyperlink("Trailer");
            trailer.setOnAction(e -> {
                player.getEngine().load(String.format(Movie.TRAILER_URL_FORMAT, movie.getTrailer()));
                player.setVisible(true);
            });
            trailer.setAlignment(Pos.BOTTOM_LEFT);
            GridPane.setVgrow(trailer, Priority.ALWAYS);
            info.add(trailer, 0, 7, 2, 1);
        }
        moviePane.setLeft(poster);
        moviePane.setCenter(info);
        return stack;
    }
}
