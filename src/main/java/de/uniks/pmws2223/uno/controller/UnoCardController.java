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
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import static de.uniks.pmws2223.uno.Constants.*;

public class UnoCardController implements Controller{
    private final IngameController ingameController;
    private final GameService gameService;
    private final AtomicInteger indexCardOnClick;
    private final Card card;
    private final Player player;
    public StackPane cardPane;
    @FXML
    private Rectangle cardBody;
    @FXML
    private Label cardValue;

    /**
     * This Constructor is being used if the card has Ability (to be chosen by the player) within it.
     * For this project, this constructor is only being used if this card
     * has an action (if it is clicked then it will be removed and sent to discard pile
     * @param ingameController the controller, in which the Card will be shown
     * @param gameService the game-service that is being used for the game
     * @param indexCardOnClick the location of this card on the list. will be use for removing the card later on
     * @param card the detail of the card
     * @param player the player who has this card
     */
    UnoCardController( IngameController ingameController,GameService gameService, AtomicInteger indexCardOnClick, Card card, Player player ){
        this.ingameController = ingameController;
        this.gameService = gameService;
        this.indexCardOnClick = indexCardOnClick;
        this.card = card;
        this.player = player;
    }

    /**
     * This constructor is being used if the card does not have any Ability (to be chosen by the player).
     * this constructor is associated with a function renderWithoutAction()
     * @param ingameController the controller, in which the Card will be shown
     * @param gameService the game-service that is being used for the game
     * @param card the detail of the card
     * @param player the player who has this card
     */
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

    /**
     * This render is used only if the card has an ability to be chosen by the player
     * @return The card with the pane of stackPane
     * @throws IOException if the render does not return a correct parent
     */
    @Override
    public Parent render() throws IOException {
        Parent parent = initializer();

        if(!card.getName().equals(WILDCARD_STRING)){
            chooseColour();
        }

        // Add a mouse event listener to the card body
        parent.setOnMouseEntered(event -> {
            // Move the card up by 10 pixels when the mouse enters the card
            if(card.getName().equals(WILDCARD_STRING)||GameService.isConditionTrue(card,gameService.getEncounter().getCurrentCard())
                    && gameService.getEncounter().getCurrentPlayer().equals(player)) {
                //only if the condition is true set the card to true!
                parent.setTranslateY(-10);
            }
        });

        parent.setOnMouseExited(event -> {
            // Move the card back down to its original position when the mouse exits the card
            parent.setTranslateY(0);
        });

        // Add a mouse click event listener to the card body
        if(!card.getName().equals(WILDCARD_STRING)) {
            parent.setOnMouseClicked(event -> {
                //WARNING: ONLY IF THE CARD IS NOT WILDCARD! OTHERWISE, DO NOT SET THIS setOnMouseClicked!

                // set the value only if the card is not action card
                setValue(card.getNumber() == 0 ? card.getName() : Integer.toString(card.getNumber()));

                // try to place the card on the discard pile
                try {
                    ingameController.discardedCardAction(Objects.requireNonNull(indexCardOnClick), card, player);
                } catch (GameServiceException e) {
                    System.err.println(e.getMessage());
                }

                // Print the card value to the console when the card is clicked
                // System.out.println("Clicked on card " + cardValue.getText()); //DEBUG
            });
        }

        return parent;
    }

    /**
     * This method is being used to choose a colour of this card that
     * will be shown to the screen
     */
    private void chooseColour() {
        if(card.getColour().equals(GREEN)){
            setColor(Color.GREEN);
        } else if(card.getColour().equals(BLUE)){
            setColor(Color.BLUE);
        } else if(card.getColour().equals(RED)){
            setColor(Color.RED);
        } else if(card.getColour().equals(YELLOW)){
            setColor(Color.ORANGE);
        }
    }

    /**
     * This function return a card without any action.
     * For this project it will be used for updating the Discard Pile
     * @return The card with the pane of stackPane
     * @throws IOException if the render does not return a correct parent
     */
    public Parent renderWithoutAction() throws IOException {
        Parent parent = initializer();
        chooseColour();
        return parent;
    }

    /**
     * Initialize the parent of this controller. IMPORTANT: This initializer should only be
     * implemented inside render functions.
     * @return the initialized parent
     * @throws IOException if the parent is not valid to be loaded
     */
    private Parent initializer() throws IOException {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/UnoCard.fxml"));
        loader.setControllerFactory(callback -> this);
        Parent parent = loader.load();

        setValue(card.getNumber() == 0 ? card.getName() : String.valueOf(card.getNumber()));
        return parent;
    }

    /**
     * set the value of the shown card within the Controller package.
     * @param value the value that will be shown.
     */
    protected void setValue(String value) {
        // Set the card value label text
        cardValue.setText(value);
    }

    /**
     * Set the colour of the card within the Controller package
     * @param color the colour that will be shown
     */
    public void setColor(Color color) {
        // Set the card body color
        cardBody.setFill(color);
    }

    @Override
    public void destroy() {

    }
}
