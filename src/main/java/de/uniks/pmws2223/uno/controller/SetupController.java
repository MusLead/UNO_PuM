package de.uniks.pmws2223.uno.controller;

import de.uniks.pmws2223.uno.App;
import de.uniks.pmws2223.uno.service.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

import java.io.IOException;

public class SetupController implements Controller{
    private final App app;
    private final GameService gameService;
    @FXML
    public TextField nameField;
    @FXML
    public Button logInButton;
    @FXML
    public ChoiceBox choiceBoxBots;

    public SetupController( App app, GameService gameService ) {
        this.app = app;
        this.gameService = gameService;
    }

    @Override
    public String getTitle() {
        return "UNO - Setup";
    }

    @Override
    public void init() {

    }

    @Override
    public Parent render() throws IOException {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/Setup.fxml"));
        loader.setControllerFactory(callback -> this);
//        nameField.setText("Agha");
        Parent parent = loader.load();
        choiceBoxBots.getItems().setAll("1","2","3");
        choiceBoxBots.setValue("1");
        logInButton.setDefaultButton(true);
        logInButton.setOnAction(actionEvent -> {
            app.show(new IngameController(app,gameService,nameField.getText(),
                                          Integer.parseInt((String) choiceBoxBots.getValue())));
        });

        return parent;
    }

    @Override
    public void destroy() {

    }
}
