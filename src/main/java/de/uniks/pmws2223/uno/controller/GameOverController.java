package de.uniks.pmws2223.uno.controller;

import de.uniks.pmws2223.uno.App;
import de.uniks.pmws2223.uno.model.Player;
import de.uniks.pmws2223.uno.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.IOException;

public class GameOverController implements Controller {
    private final Player player;
    private final App app;
    @FXML
    public Text playerText;
    @FXML
    public Button mainMenuButton;

    GameOverController( Player player, App app) {
        this.player = player;
        this.app = app;
    }

    @Override
    public String getTitle() {
        return "UNO - GameOver";
    }

    @Override
    public void init() {

    }

    @Override
    public Parent render() throws IOException {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/GameOver.fxml"));
        loader.setControllerFactory(callback -> this);
        Parent parent = loader.load();
        playerText.setText(player.getName() + " WON!");
        mainMenuButton.setOnAction(actionEvent -> app.show(new SetupController(app, new GameService())));
        return parent;
    }

    @Override
    public void destroy() {

    }
}
