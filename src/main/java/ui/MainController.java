package ui;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import model.Player;
import model.Team;
import service.TournamentRegistration;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class MainController {
    @FXML private TextField teamNameField;
    @FXML private VBox playersBox;
    @FXML private TilePane cardsPane;
    @FXML private Label countdownLabel;
    @FXML private Button createTeamButton; // Bouton pour créer l'équipe

    private final TournamentRegistration reg = new TournamentRegistration();
    private int playerCount = 0;
    private final LocalDateTime deadline = LocalDateTime.of(2025, 5, 16, 23, 59);

    @FXML
    public void initialize() {
        addPlayerRow();
        startCountdown();
        // Le bouton reste actif, la vérification de la date limite se fait uniquement lors de la création d'équipe
    }

    private void startCountdown() {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> updateCountdown())
        );
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void updateCountdown() {
        LocalDateTime now = LocalDateTime.now();
        java.time.Duration duration = java.time.Duration.between(now, deadline);

        Platform.runLater(() -> {
            if (!duration.isNegative()) {
                long days = duration.toDays();
                long hours = duration.toHours() % 24;
                long minutes = duration.toMinutes() % 60;
                long seconds = duration.getSeconds() % 60;

                countdownLabel.setText(
                        String.format("⏳ Fin des inscriptions dans : %d j %02d h %02d min %02d s",
                                days, hours, minutes, seconds)
                );
            } else {
                countdownLabel.setText("❌ Inscriptions terminées.");
            }
        });
    }

    @FXML
    private void onAddPlayer() {
        if (!lastRowComplete()) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Veuillez remplir le dernier joueur avant d'en ajouter un autre.");
            return;
        }
        if (playerCount >= 6) {
            showAlert(Alert.AlertType.WARNING, "Attention", "Nombre maximal de 6 joueurs atteint.");
            return;
        }
        addPlayerRow();
    }

    private boolean lastRowComplete() {
        if (playerCount == 0) return true;
        HBox last = (HBox) playersBox.getChildren().get(playerCount - 1);
        TextField fn = (TextField) last.getChildren().get(0);
        TextField ln = (TextField) last.getChildren().get(1);
        return !fn.getText().trim().isEmpty() && !ln.getText().trim().isEmpty();
    }

    private void addPlayerRow() {
        HBox row = new HBox(5);
        TextField fn = new TextField(); fn.setPromptText("Prénom"); fn.getStyleClass().add("sport-input");
        TextField ln = new TextField(); ln.setPromptText("Nom"); ln.getStyleClass().add("sport-input");
        Button remove = new Button("×");
        remove.setOnAction(e -> {
            playersBox.getChildren().remove(row);
            playerCount--;
        });
        row.getChildren().addAll(fn, ln, remove);
        playersBox.getChildren().add(row);
        playerCount++;
    }

    @FXML
    private void onCreateTeam() {
        // Vérification de la date limite : on autorise l'affichage de l'UI mais on bloque la création
        if (LocalDateTime.now().isAfter(deadline)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "La date limite est dépassée. Vous ne pouvez plus créer de nouvelle équipe.");
            return;
        }

        String name = teamNameField.getText().trim();
        if (name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom de l'équipe est requis.");
            return;
        }
        if (playerCount < 6 || !lastRowComplete()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez ajouter exactement 6 joueurs remplis.");
            return;
        }

        Set<String> seen = new HashSet<>();
        Team t = new Team(name);
        for (Node node : playersBox.getChildren()) {
            HBox r = (HBox) node;
            String fn = ((TextField) r.getChildren().get(0)).getText().trim();
            String ln = ((TextField) r.getChildren().get(1)).getText().trim();
            String key = fn.toLowerCase() + ":" + ln.toLowerCase();

            if (seen.contains(key)) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Joueur dupliqué dans l'équipe : " + fn + " " + ln);
                return;
            }
            for (Team existingTeam : reg.listTeams()) {
                for (Player p : existingTeam.getPlayers()) {
                    if (p.getFirstName().equalsIgnoreCase(fn) && p.getLastName().equalsIgnoreCase(ln)) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Le joueur " + fn + " " + ln + " est déjà inscrit dans une autre équipe.");
                        return;
                    }
                }
            }
            seen.add(key);
            t.addPlayer(new Player(fn, ln));
        }

        try {
            reg.registerTeam(t);
            refreshCards();
            clearForm();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void refreshCards() {
        cardsPane.getChildren().clear();
        for (Team team : reg.listTeams()) {
            VBox card = new VBox(6);
            card.getStyleClass().add("team-card");
            Label title = new Label(team.getName());
            title.getStyleClass().add("team-name");
            card.getChildren().add(title);
            for (Player p : team.getPlayers()) {
                Label pl = new Label("• " + p.getFirstName() + " " + p.getLastName());
                pl.getStyleClass().add("player-name");
                card.getChildren().add(pl);
            }
            cardsPane.getChildren().add(card);
        }
    }

    private void clearForm() {
        teamNameField.clear();
        playersBox.getChildren().clear();
        playerCount = 0;
        addPlayerRow();
    }

    private void showAlert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
