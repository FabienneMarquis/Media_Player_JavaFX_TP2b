package controller;

import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javafx.util.Callback;
import javafx.util.Duration;
import modele.PlayList;
import modele.Song;

import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.List;

public class Controller_musique implements Initializable {

    @FXML
    private Text titreChanson;

    @FXML
    private Text volumePourcentage;

    @FXML
    private Slider sliderVolume;

    @FXML
    private Text currentTimeSong;

    @FXML
    private Text timeTotalSong;

    @FXML
    private Button btnReview;

    @FXML
    private Button btnfastforward;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Button btnPlay;

    @FXML
    private Button btnPause;

    @FXML
    private Button btnStop;

    @FXML
    private CheckBox checkBoxReplay;

    @FXML
    private TextArea textAreaMusique;

    @FXML
    private VBox rectAnchor;

    @FXML
    private Rectangle rectangleBlack;

    @FXML
    private Rectangle animatedRect;

    @FXML
    private ListView<Song> playListView;

    private ObservableList<Song> observableList = FXCollections
            .synchronizedObservableList(FXCollections.observableArrayList());

    private MediaPlayer mediaPlayer;

    private Song song;

    private Media media;

    private Dragboard dragBoard;

    private java.util.List<Song> listSong;

    private PlayList playList;

    @FXML
    void fastForwardsong(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(30)));
    }

    @FXML
    void pauseSong(ActionEvent event) {
        mediaPlayer.pause();
        btnPause.setDisable(true);
        btnPlay.setDisable(false);
    }

    @FXML
    void playSong(ActionEvent event) {
        btnPause.setDisable(false);
        btnStop.setDisable(false);
        btnPlay.setDisable(true);
        btnReview.setDisable(false);
        btnfastforward.setDisable(false);
        mediaPlayer.play();


    }

    @FXML
    void replaySong(ActionEvent event) {
        if (checkBoxReplay.isSelected()) mediaPlayer.getOnRepeat();
        //pas sur si ça marche
        if (!checkBoxReplay.isSelected()) mediaPlayer.setOnEndOfMedia(mediaPlayer.getOnEndOfMedia());
    }

    @FXML
    void reviewSong(ActionEvent event) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(-30)));
    }

    @FXML
    void stopSong(ActionEvent event) {
        btnPause.setDisable(true);
        btnPlay.setDisable(false);
        mediaPlayer.stop();
        btnReview.setDisable(true);
        btnfastforward.setDisable(true);
    }

    @FXML
    void volumeSong(ActionEvent event) {
        mediaPlayer.setVolume(sliderVolume.getValue());

    }

    public void quit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Avertissement");
        alert.setHeaderText("Quitter");
        alert.setContentText("Voulez-vous Quitter le programme?");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.get() == ButtonType.OK) {
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    void dragDetected(MouseEvent event) {
        dragBoard = textAreaMusique.startDragAndDrop(TransferMode.ANY);

        ClipboardContent content = new ClipboardContent();
        content.putUrl(song.getUrlSong().toString());
        dragBoard.setContent(content);

        event.consume();

    }

    @FXML
    void dragDone(DragEvent event) {
        event.getDragboard().clear();
        event.consume();
    }

    @FXML
    void dragDropped(DragEvent event) {
        if ((event.getDragboard().hasFiles())) {
            try {
                URL url = new URL(event.getDragboard().getUrl());
                String urlClean = url.toString().replace(" ", "%20");
                System.out.print(urlClean);
                song = new Song(urlClean);
                playList.addSong(song);
                media = new Media(song.getUrlSong());
                initializeMedia(media);
                setFileName();
                setPlayListView();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Seuls les fichiers .mp3 et .wav sont accepté");
            alert.show();
        }
    }


    @FXML
    void dragEntered(DragEvent event) {
        if (event.getGestureSource() != textAreaMusique &&
                event.getDragboard().hasFiles()) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(1);
            textAreaMusique.setEffect(colorAdjust);
        }
        event.consume();
    }

    @FXML
    void dragExited(DragEvent event) {
        textAreaMusique.setEffect(null);
        event.consume();
    }

    @FXML
    void dragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        event.consume();
    }

    private void setFileName() {
        System.out.println(media.getSource());
        String[] songURLSplit = media.getSource().split("/");
        String name = songURLSplit[songURLSplit.length - 1].replace("%20", " ");
        titreChanson.setText(name);
    }

    private void initializeMedia(Media media) {
        if (mediaPlayer == null)
            mediaPlayer = new MediaPlayer(media);
        else {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.dispose();
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.play();
            } else {
                mediaPlayer.dispose();
                mediaPlayer = new MediaPlayer(media);
            }
        }
        setFileName();
        mediaPlayer.statusProperty().addListener(((observable1, oldValue1, newValue1) -> {
            switch (mediaPlayer.getStatus()) {
                case READY:
                    onReady();
                    setPlayListView();
                    break;
                case PAUSED:
                    btnPause.setDisable(true);
                    btnStop.setDisable(false);
                    btnPlay.setDisable(false);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnPlay.requestFocus();
                        }
                    });
                    break;
                case PLAYING:
                    btnPause.setDisable(false);
                    btnStop.setDisable(false);
                    btnPlay.setDisable(true);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnPause.requestFocus();
                        }
                    });
                    break;
                case STOPPED:
                    btnPause.setDisable(true);
                    btnStop.setDisable(true);
                    btnPlay.setDisable(false);
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            btnStop.requestFocus();
                        }
                    });
                    break;
            }
        }));
    }

    private void onReady() {
        btnPause.setDisable(true);
        btnStop.setDisable(true);
        btnPlay.setDisable(false);

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer = new MediaPlayer(media);
            System.out.println("end of file");

            if (!checkBoxReplay.isSelected()) {
                mediaPlayer.seek(Duration.millis(0));
                mediaPlayer.stop();
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    btnPlay.requestFocus();
                }
            });
        });
        mediaPlayer.volumeProperty().bind(sliderVolume.valueProperty().divide(100));

        timeTotalSong.setText((((int) mediaPlayer.getTotalDuration().toHours()) > 0 ? (
                (int) mediaPlayer.getTotalDuration().toHours()) % 24 + ":" : "") +
                ((int) mediaPlayer.getTotalDuration().toMinutes()) % 60 + ":" +
                ((int) mediaPlayer.getTotalDuration().toSeconds()) % 60);
        currentTimeSong.textProperty().setValue((((int) mediaPlayer.getCurrentTime().toHours()) > 0 ? (
                (int) mediaPlayer.getCurrentTime().toHours()) % 24 + ":" : "") +
                ((int) mediaPlayer.getCurrentTime().toMinutes()) % 60 + ":" +
                ((int) mediaPlayer.getCurrentTime().toSeconds()) % 60);

        song.setMetadate(media);

        textAreaMusique.setText(song.getInfoSong());
    }

    public void setPlayListView(){
        observableList = playList.createPlaylist();
        playListView.setItems(observableList);
        playListView.setCellFactory(new Callback<ListView<Song>, ListCell<Song>>() {
            @Override
            public ListCell<Song> call(
                    ListView<Song> param) {
                return new ListCell<Song>() {
                    @Override
                    protected void updateItem(Song t,
                                              boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            Platform.runLater(() -> setText(t.getArtist()));
                        }
                    }
                };
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //creation play liste
        playList = new PlayList();
        playList.createPlaylist();

        //creation de la song de départ
        String nameFileSongBase = "Deftones - Change (In The House Of Flies).mp3";
        song = new Song(getClass().getResource("/" + nameFileSongBase).toString());
        media = new Media(song.getUrlSong());
        initializeMedia(media);

        volumePourcentage.setText(String.valueOf(50));
        sliderVolume.valueProperty().addListener((observable, oldValue, newValue) -> {
            volumePourcentage.setText(String.valueOf((int) sliderVolume.getValue()));
            if (!mediaPlayer.isMute()) {
                mediaPlayer.setVolume(Math.pow((double) sliderVolume.getValue() / 100, 3));
            }
        });

        titreChanson.textProperty().addListener((observable, oldValue, newValue) -> {
            titreChanson.setText(song.getSongFileName());
        });

        progressBar.progressProperty().setValue(mediaPlayer.getCurrentTime().toSeconds() / media.getDuration().toSeconds());
        mediaPlayer.currentTimeProperty().addListener(((observable, oldValue, newValue) -> {
            progressBar.progressProperty().setValue(mediaPlayer.getCurrentTime().toSeconds() / media.getDuration().toSeconds());
        }));

        mediaPlayer.currentTimeProperty().addListener(((observable, oldValue, newValue) -> {
            currentTimeSong.textProperty().setValue((((int) mediaPlayer.getCurrentTime().toHours()) > 0 ?
                    ((int) mediaPlayer.getCurrentTime().toHours()) % 24 + ":" : "") +
                    ((int) mediaPlayer.getCurrentTime().toMinutes()) % 60 + ":" +
                    ((int) mediaPlayer.getCurrentTime().toSeconds()) % 60);
        }));

        mediaPlayer.onEndOfMediaProperty().addListener((observable, oldValue, newValue) -> {
            if (checkBoxReplay.isSelected()) {
                mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            } else {
                mediaPlayer.stop();
                progressBar.progressProperty().setValue(0);
                btnPause.setDisable(true);
                btnStop.setDisable(true);
                btnPlay.setDisable(false);
            }
        });

        textAreaMusique.textProperty().addListener(((observable, oldValue, newValue) -> {
            textAreaMusique.textProperty().setValue(song.getInfoSong());
        }));

        playListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Song>() {
            @Override
            public void changed(ObservableValue<? extends Song> observable, Song oldValue, Song newValue) {
                song=newValue;
                media = new Media(song.getUrlSong());
                initializeMedia(media);
                setFileName();
            }
        });


        btnPause.setDisable(true);
        btnStop.setDisable(true);
        btnReview.setDisable(true);
        btnfastforward.setDisable(true);
        animatedRect.heightProperty().bind(rectAnchor.heightProperty());
        animatedRect.widthProperty().bind(rectAnchor.widthProperty());

        //setter la vitesse avec le volume et le play avec le bouton play pause et stop

        FillTransition ft = new FillTransition(Duration.millis(3000), rectangleBlack, Color.BLACK, Color.WHITE);
        ft.setCycleCount(-1);
        ft.setAutoReverse(true);

        ft.play();
    }
}