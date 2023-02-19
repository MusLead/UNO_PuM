package de.uniks.pmws2223.uno.controller;

import de.uniks.pmws2223.uno.App;
import de.uniks.pmws2223.uno.model.Card;
import de.uniks.pmws2223.uno.model.Player;
import de.uniks.pmws2223.uno.service.GameService;
import de.uniks.pmws2223.uno.service.GameServiceException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import static de.uniks.pmws2223.uno.Constants.*;

public class UnoCardController implements Controller{
    private final IngameController ingameController;
    private final GameService gameService;
    private final AtomicInteger indexCardOnClick;
    private final Card card;
    private final Player player;
    @FXML
    private Rectangle cardBody;
    @FXML
    private Label cardValue;

    UnoCardController( IngameController ingameController,GameService gameService, AtomicInteger indexCardOnClick, Card card, Player player ){
        this.ingameController = ingameController;
        this.gameService = gameService;
        this.indexCardOnClick = indexCardOnClick;
        this.card = card;
        this.player = player;
    }

    UnoCardController( IngameController ingameController,GameService gameService, Card card, Player player ){
        this.ingameController = ingameController;
        this.gameService = gameService;
        this.indexCardOnClick = null;
        this.card = card;
        this.player = player;
    }

    @Override
    public String getTitle() {
        //WILL NOT BE CALLED!
        return null;
    }

    @Override
    public void init() {
        //WILL NOT BE CALLED!
    }

    @Override
    public Parent render() throws IOException {
        Parent parent = initializer();

        if(!card.getName().equals(WILDCARD_STRING)){
            chooseColour();
        }

        // Add a mouse event listener to the card body
        cardBody.setOnMouseEntered(event -> {
            // Move the card up by 10 pixels when the mouse enters the card
            if(GameService.isConditionTrue(card,gameService.getEncounter().getCurrentCard())
                    && gameService.getEncounter().getCurrentPlayer().equals(player)) {
                //only if the condition is true set the card to true!
                parent.setTranslateY(-10);
            }
        });

        cardBody.setOnMouseExited(event -> {
            // Move the card back down to its original position when the mouse exits the card
            parent.setTranslateY(0);
        });

        // Add a mouse click event listener to the card body
        if(!card.getName().equals(WILDCARD_STRING)) {
            cardBody.setOnMouseClicked(event -> {
                //WARNING: ONLY IF THE CARD IS NOT WILDCARD! OTHERWISE, DO NOT SET THIS CLICK!

                // set the value only if the card is not action card
                setValue(card.getNumber() == 0 ? card.getName() : Integer.toString(card.getNumber()));
                String value = card.getName() + "," + card.getColour();
                if (indexCardOnClick != null) {
                    indexCardOnClick.set(gameService.findIndexCards(value, card.getColour(), player));
                }
                String result = null;
                try {
                    result = gameService.drawCard(card, player);
                } catch (GameServiceException e) {
                    System.err.println(e.getMessage());
                }
                ingameController.updateGameScreen(card, result);

                // Print the card value to the console when the card is clicked
                System.out.println("Clicked on card " + cardValue.getText());
            });
        }


        return parent;
    }

    private void chooseColour() {
        if(card.getColour().equals(GREEN)){
            setColor(Color.GREEN);
        } else if(card.getColour().equals(BLUE)){
            setColor(Color.BLUE);
        } else if(card.getColour().equals(RED)){
            setColor(Color.RED);
        } else if(card.getColour().equals(YELLOW)){
            setColor(Color.rgb(255, 160, 0));
        }
    }

    public Parent renderWithoutAction() throws IOException {
        Parent parent = initializer();
        chooseColour();
        return parent;
    }

    private Parent initializer() throws IOException {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/UnoCard.fxml"));
        loader.setControllerFactory(callback -> this);
        Parent parent = loader.load();

        setValue(card.getNumber() == 0 ? card.getName() : String.valueOf(card.getNumber()));
        return parent;
    }

    protected void setValue(String value) {
        // Set the card value label text
        cardValue.setText(value);
    }

    public void setColor(Color color) {
        // Set the card body color
        cardBody.setFill(color);
    }

    @Override
    public void destroy() {

    }
}
