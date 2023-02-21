package de.uniks.pmws2223.uno.controller;

import de.uniks.pmws2223.uno.App;
import de.uniks.pmws2223.uno.model.Card;
import de.uniks.pmws2223.uno.model.Encounter;
import de.uniks.pmws2223.uno.model.Player;
import de.uniks.pmws2223.uno.service.GameService;
import de.uniks.pmws2223.uno.service.GameServiceException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import static de.uniks.pmws2223.uno.Constants.*;
import static de.uniks.pmws2223.uno.service.BotService.*;

public class IngameController implements Controller{

    private final App app;
    private final GameService gameService;
    private final List<Player> bots = new ArrayList<>();
    private final List<PropertyChangeListener> botsListeners = new ArrayList<>();
    private final String name;
    private final int totalBots;
    public ScrollPane myScrollPane;
    private PropertyChangeListener playerListeners;
    private PropertyChangeListener currentDeckPilePlayerListener;
    private Timer timer;
    private Player player;

    @FXML
    public HBox enemiesContainer;
    @FXML
    public HBox myContainer;
    @FXML
    public StackPane withdraw;
    @FXML
    public Button logout;
    @FXML
    public HBox deckContainer;
    @FXML
    public Text deckPileText;

    public IngameController( App app, GameService gameService, String name, int totalBots ) {
        this.app = app;
        this.gameService = gameService;
        this.name = name;
        this.totalBots = totalBots;
    }

    @Override
    public String getTitle() {
        return "UNO - Ingame";
    }

    /**
     * This init contains timer that will only be set if the player who plays the game not a human.
     * Note: the timer will start if the player draws the card!
     * (player card's listener detect some changes on the list)
     */
    @Override
    public void init() {
        /*
         * this the time if this robot and the next player also robot who will
         * place a card on the discard pile
         */
        // Note: the timer will start if the player draws the card!
        // (player card's listener detect some changes on the list)
        timer = new Timer(500, e -> Platform.runLater(() -> {
            Player checkPlayer = gameService.getEncounter().getCurrentPlayer();
            if(checkPlayer.getTypePlayer().equals(BOT)){
                for (Player bot : bots) {
                    if(bot.getName().equals(checkPlayer.getName())){
                        try {
                            updateGameScreen(gameService.getEncounter().getCurrentCard(), botPlay(bot, gameService));
                        } catch (GameServiceException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }

            } else {
                timer.stop();
            }
        }));
    }

    @Override
    public Parent render() throws IOException {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/Game.fxml"));
        loader.setControllerFactory(callback -> this);
        Parent parent = loader.load();

        // before gives a card, clear all card on the deck!
        myContainer.getChildren().clear();

        showPlayer();
        showBots();

        logout.setOnAction(actionEvent -> app.show(new SetupController(app, new GameService(new Random()))));

        // show who should play the game first!
        updateGameScreen(gameService.getEncounter().getCurrentCard(),gameService.getEncounter().getCurrentCard().getName());


        return parent;
    }

    @Override
    public void destroy() {
        timer.stop();
        player.listeners().removePropertyChangeListener(Player.PROPERTY_CARDS, playerListeners);
        player.listeners().removePropertyChangeListener(Player.PROPERTY_CURRENT_DISCARD_PILE,currentDeckPilePlayerListener);
        for (int i = 0; i < totalBots; i++) {
            bots.get(i).listeners().removePropertyChangeListener(Player.PROPERTY_CARDS,botsListeners.get(i));
            bots.get(i).listeners().removePropertyChangeListener(Player.PROPERTY_CURRENT_DISCARD_PILE,botsListeners.get(i+totalBots));
        }
    }

    /**
     * This method gives the player 7 randomized cards. It contains listeners, that
     * determine who currently plays the game or if the card is being placed
     * or the player draw a new card
     */
    private void showPlayer()  {
        // give the player 7 random cards!
        player = gameService.createPlayer(HUMAN, name);

        // this Integer let the program knows which card has been placed on the discard pile
        AtomicInteger indexCardOnClick = new AtomicInteger();

        //for starter, iterate all card that player has
        for (Card card : player.getCards()) {
            showUserCard(indexCardOnClick, card);
        }

        // set player listeners, if the card is being placed or the player draw a new card
        playerListeners = news -> {
            // System.out.println("News from player cards: " + news); //DEBUG

            // If the player does not have any card, means the game is over, and player won the game
            if(player.getCards().size() == 0) {
                showGameOverScene(player);
            }

            if(news.getOldValue() != null && news.getNewValue() == null) {
                /*
                  if the old value not null and new value is null it means that the
                  player successfully place the card then
                 */
                if(myContainer.getChildren().size() != 0) {
                    myContainer.getChildren().remove(indexCardOnClick.get());
                }
            }

            if(news.getOldValue() == null && news.getNewValue() != null) {
                /*
                    if the player withdraw from the game,
                    the old value is null and get new value will be the new one
                 */
                showUserCard(indexCardOnClick,(Card) news.getNewValue());
            }

            // start the timer for the robot to  work!
            timer.start();
        };
        player.listeners().addPropertyChangeListener(Player.PROPERTY_CARDS, playerListeners);

        // This listener determine who currently plays the game
        currentDeckPilePlayerListener = ev -> showCurrentPlayerWithColour((Encounter) ev.getNewValue(), myContainer);
        player.listeners().addPropertyChangeListener(Player.PROPERTY_CURRENT_DISCARD_PILE, currentDeckPilePlayerListener);

        // for the user's player to be able to withdraw from the game
        setWithdrawButton(player);

        if(player.getCurrentDiscardPile() != null){ // start of the play, check if there is a bot who plays first!
            showCurrentPlayerWithColour(player.getCurrentDiscardPile(), (Pane) myScrollPane.getContent());
        }
    }

    /**
     * This function let the user have a card (whether normal card or wildcard) and add them
     * to the myContainer.
     * @param indexCardOnClick the index position of the card, used to now where the card is located
     * @param card the new card
     */
    private void showUserCard( AtomicInteger indexCardOnClick, Card card ) {
        StackPane cardPane;
        if(card.getName().equals(WILDCARD_STRING)){
            try {
                cardPane = showWildCard(card, player, indexCardOnClick);
            } catch (GameServiceException e) {
                // possibility: if the card is not wildcard, but it is still being called to the WildCard!
                throw new RuntimeException(e);
            }
        } else {
            cardPane = showCard(indexCardOnClick, card);
        }
        myContainer.getChildren().add(cardPane);
    }

    /**
     * This method let the User know who is their turn now. usually this method is being implemented
     * within a listener, where everytime (current discard pile null or not null) will decide who should play
     * or should wait for their turn.
     * If the discard pile is not null, then set the container to colour blue, to marks that it is their turn
     * to play
     * @param currentDiscard the currentDiscard status null or not null
     * @param pane set the pane to blue indicates that it is the player turn to play, otherwise they have to wait.
     */
    private void showCurrentPlayerWithColour( Encounter currentDiscard, Pane pane ) {
        if(currentDiscard != null){
            pane.setBackground(new Background(
                    new BackgroundFill(Color.AQUA,
                                       CornerRadii.EMPTY, Insets.EMPTY)));
        }
        if(currentDiscard == null){
            pane.setBackground(new Background(
                    new BackgroundFill(Color.WHITESMOKE,
                                       CornerRadii.EMPTY, Insets.EMPTY)));

        }
    }

    /**
     * This method let the user know that the game is over. This method
     * also change the screen to GameOver scene, so that no one able to play the games!
     * @param player who won the game
     */
    private void showGameOverScene(Player player) {
        System.out.println(player.getName() + " WON!");
        myContainer.getChildren().clear();
        app.show(new GameOverController(player,app));
    }

    /**
     * If this method is being called, that means, the user has a chance to place a wildcard when it is their turn.
     * this wildcard let the player decide which color the player wants to place on the discard pile.
     * @param wildCard the current Wildcard
     * @param player who has the card
     * @param indexCardOnClick an integer, locate where this card is in the player's container placed.
     *                         It will be used later in the listeners for removing this card.
     * @throws GameServiceException if the card is not wildcard, throw some exception!
     */
    private StackPane showWildCard( Card wildCard, Player player, AtomicInteger indexCardOnClick ) throws GameServiceException {

        if(!wildCard.getName().equals(WILDCARD_STRING)){
            throw new GameServiceException("NOT WILDCARD, INVALID");
        }

        //show the physical card to the user
        UnoCardController unoCardController = new UnoCardController(this,gameService,indexCardOnClick,wildCard,player);
        StackPane stackPane;
        try {
            stackPane = (StackPane) unoCardController.render();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // to add the button
        VBox vBox = new VBox();
        vBox.setSpacing(5.0);

        // iterates all possible colour and show them to the user
        for (int i = 0; i < 4; i++) {
            Button colour = new Button();

            setColourWildcardButton(i, colour);

            int finalI = i;
            colour.setOnMouseEntered(mouseDragEvent -> { //on hover
                System.out.println("hover...");
                if(COLOURS[finalI].equals(BLUE)){
                    colour.setStyle("-fx-background-color: #0066CC; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-height: 20px; -fx-pref-width: 70px;");
                } else if(COLOURS[finalI].equals(RED)){
                    colour.setStyle("-fx-background-color: #e74949; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-height: 20px; -fx-pref-width: 70px;");
                } else if(COLOURS[finalI].equals(GREEN)){
                    colour.setStyle("-fx-background-color: #409d43; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-height: 20px; -fx-pref-width: 70px;");
                } else if(COLOURS[finalI].equals(YELLOW)){
                    colour.setStyle("-fx-background-color: #dbbd63; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-height: 20px; -fx-pref-width: 70px;");
                }
            });

            colour.setOnMouseExited(mouseEvent -> {
                System.out.println("exit... i:" + finalI);
                setColourWildcardButton(finalI, colour);
            });

            colour.setOnAction(actionEvent -> { //not on hover
                // if the player choose a colour on the wildcard that they have
                // put the wild card to the discard deck!
                try {
                    Card choosenCard = gameService.setWildcardColour(wildCard,COLOURS[finalI]);
                    discardedCardAction(indexCardOnClick, choosenCard);
                } catch (GameServiceException e) {
                    System.err.println(e.getMessage());
                }
            });
            vBox.getChildren().add(colour);
        }

        // put the card with the possible colour buttons in (vBox) on a stackPane, then add them to the
        // user container
        vBox.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(vBox);
        stackPane.setAlignment(Pos.CENTER);
        return stackPane;
    }

    private static void setColourWildcardButton( int i, Button colour ) {
        if(COLOURS[i].equals(BLUE)){
            colour.setStyle("-fx-background-color: blue; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-height: 20px; -fx-pref-width: 70px;");
        } else if(COLOURS[i].equals(RED)){
            colour.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-height: 20px; -fx-pref-width: 70px;");
        } else if(COLOURS[i].equals(GREEN)){
            colour.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-height: 20px; -fx-pref-width: 70px;");
        } else if(COLOURS[i].equals(YELLOW)){
            colour.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-height: 20px; -fx-pref-width: 70px;");
        }
    }

    /**
     * This method shows to the User''s player the card that they have.
     * WARNING: These card has action, when it is being clicked, the card can be removed from the container.
     * make sure only use this method to shows card that will be used by the User's player.
     * @param indexCardOnClick an integer, locate where this card is in the player's container placed.
     *                         It will be used later in the listeners for removing this card.
     * @param card the detail of the card
     * @return a card with a container of stackPane
     */
    private StackPane showCard( AtomicInteger indexCardOnClick, Card card )  {
        UnoCardController unoCardController = new UnoCardController(this,gameService,indexCardOnClick,card,player);
        StackPane stackPane;
        try {
            stackPane = (StackPane) unoCardController.render();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stackPane;
    }

    /**
     * This Method let the User knows who plays next and which card is on the discard pile
     * at this moment of time. Only after this User's player place a card.
     * This method is being implemented in showWildCard() and
     * @param indexCardOnClick an integer, locate where this card is in the player's container placed.
     *                         It will be used later in the listeners for removing this card.
     * @param card the detail of the card
     * @throws GameServiceException if there is some unexpected behavior from the card
     */
    protected void discardedCardAction( AtomicInteger indexCardOnClick, Card card) throws GameServiceException {
        indexCardOnClick.set(gameService.findIndexCards(card, player));
        String result = gameService.placeCard(card, player);
        updateGameScreen(card, result);
    }

    /**
     * This method update the status who turns to play
     * and the current card on discard pile
     * @param card current card on discard pile
     * @param result to know if the game is over or not, if yes it should have value SUCCESS
     */
    protected void updateGameScreen( Card card, String result ) {
        if(result != null && result.equals(SUCCESS)){
            // meaning there is a player who has successfully placed all their cards
            timer.stop(); // set timer to stop so that no robots try to place their card!
            return; // do not print anything
        }
        if(gameService.getEncounter().getCurrentCard().getName().equals(WILDCARD_STRING)){
            // If the card is wildcard, set the colour. It might not have been set
            card.setColour(gameService.getEncounter().getCurrentCard().getColour());
        }

        UnoCardController unoCardController = new UnoCardController(this,gameService,gameService.getEncounter().getCurrentCard(), gameService.getEncounter().getCurrentPlayer());

        // this lines of code make sure that the card on the Discard pile updated
        // the card cannot be clicked, therefore we use unoCardController.renderWithoutAction()
        try {
            StackPane cardPane = (StackPane) unoCardController.renderWithoutAction();
            if(deckContainer.getChildren().size() >=3) {
                // delete all element from index 2 to (size-1)
                deckContainer.getChildren().remove(deckContainer.getChildren().size() - 1);
            }
            deckContainer.getChildren().add(cardPane);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //DEBUG IF-STATEMENT
        assert result != null;
        if(!result.equals(NOT_VALID) && !result.equals(WITHDRAW)) {
            System.out.println(result + " " +
                                       card.getColour() + " currentPlayer: " + gameService.getEncounter().getCurrentPlayer()); //DEBUG
        } else if(result.equals(NOT_VALID)){
            //19.02.2023
            //this error messsage can come because of the GameTest.java -> memory leak because of the busy waiting.
            //make sure that the FXRobot can run synchronously with the timer or the other that could lead to run in another thread
            System.err.println(NOT_VALID + ": "+card.getName() + " " +
                                       card.getColour() + " | On discard pile: "+ gameService.getEncounter().getCurrentCard()
                                       +" currentPlayer: " + gameService.getEncounter().getCurrentPlayer()); //DEBUG
        } else { // this statement is always true if the result equals withdraw
            System.err.println(WITHDRAW + ": " + "| On discard pile: "+ gameService.getEncounter().getCurrentCard()
                                       +" currentPlayer: " + gameService.getEncounter().getCurrentPlayer()); //DEBUG
            // show the users who play the games now
            deckPileText.setText("The Player before, WITHDRAW\nYour Turn: " + gameService.getEncounter().getCurrentPlayer());
            return;
        }

        // show the users who play the games now
        deckPileText.setText("Your Turn: " + gameService.getEncounter().getCurrentPlayer());

    }

    /**
     * This method makes the withdrawal/draw pile to be able
     * to give another card, if the user decide to draw the card, not placing
     * any card on discard pile
     * @param player who draws the card
     */
    private void setWithdrawButton( Player player ) {
        withdraw.setOnMouseClicked(actionEvent -> {
            //withdraw only if the player turns to play!
            if(gameService.getEncounter().getCurrentPlayer().equals(player)) {
                try {
                    gameService.withdraw(player);
                    // to let the user knows whose turn to play
                    updateGameScreen(gameService.getEncounter().getCurrentCard(), WITHDRAW);
                } catch (GameServiceException e) {
                    System.err.println(e.getMessage());
                    // IT MEANS ALL CARD HAS BEEN TAKEN
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * This method shows all the robots
     * that user wants to play with. It contains listener, that
     * detect if the robots decide to place or draw the card. their card
     * could increase or decreased. also it contains listener, that
     * determine who currently plays the game or if the card is being placed
     */
    private void showBots() {
        for (int i = 0; i < totalBots; i++) {
            //for starter, the robot will be given 7 random cards each
            Player newBot = gameService.createPlayer(BOT,"Bot"+(i+1));

            // show the cards to the screen using stackPane as the main container!
            StackPane pane = new StackPane();
            pane.setAlignment(Pos.CENTER_LEFT);
            List<Rectangle> cards = new ArrayList<>();
            for (int j = 0; j < newBot.getCards().size(); j++) {
                addCard(pane,j,cards);
            }

            // This listener change the text on the container (the total of the card!)
            // if the robot decide to draw or palace the card
            int finalI = i;
            PropertyChangeListener botListener = news -> {
               // System.out.println(" News from bot"+finalI+" cards: " + news); //DEBUG
                if(newBot.getCards().size() == 0){
                    showGameOverScene(newBot);
                }
                //this if-statement let the user now how many cards do they have (physically)
                if(news.getOldValue() != null && news.getNewValue() == null){
                    removeCard(pane, newBot.getCards().size(),cards);
                }
                if(news.getOldValue() == null && news.getNewValue() != null){
                    addCard(pane, newBot.getCards().size(),cards);
                }

                // update the text
                VBox thisContainer = (VBox) enemiesContainer.getChildren().get(finalI);
                Text text = (Text) thisContainer.getChildren().get(0);
                text.setText(newBot.getName() +": "+ newBot.getCards().size());
            };
            newBot.listeners().addPropertyChangeListener(Player.PROPERTY_CARDS, botListener);
            bots.add(newBot); botsListeners.add(botListener);

            //Make an Info Container that shows how many cards the robot has
            VBox botInfoContainer = new VBox();
            botInfoContainer.setAlignment(Pos.CENTER);
            botInfoContainer.setId("Bot"+(i+1));
            //this line of codes add the text and the stack pane that we created earlier.
            botInfoContainer.getChildren().addAll(new Text(newBot.getName() +": "+ newBot.getCards().size()),pane);

            //add each info container that has been created to the enemiesContainer
            enemiesContainer.getChildren().add(botInfoContainer);

            // This listener is being used to know who should place/draw their card.
            // If the discard pile is not null, then set the container to colour blue, to marks that it is their turn
            PropertyChangeListener drawPileBotListener = e -> showCurrentPlayerWithColour((Encounter) e.getNewValue(), botInfoContainer);
            newBot.listeners().addPropertyChangeListener(Player.PROPERTY_CURRENT_DISCARD_PILE, drawPileBotListener );
            botsListeners.add(drawPileBotListener);

            if(newBot.getCurrentDiscardPile() != null){
                // Start of the play, check if there is a bot who plays first!
                showCurrentPlayerWithColour(newBot.getCurrentDiscardPile(),botInfoContainer);
            }

        }
    }

    /**
     * This method is being used for the enemies of the player. The User should only know
     * how many cards do they have. everytime the enemies draw the card, this method will be called.
     * @param root the container pane of the cards
     * @param cardCount how many cards currently on deck
     * @param cards add the card to the list, to be used in removeCard
     */
    private void addCard(StackPane root, int cardCount, List<Rectangle> cards) {
        int cardWidth = 80;
        int cardHeight = 120;
        Rectangle card = new Rectangle(cardWidth, cardHeight, Color.rgb(152,0,75));
        card.setArcHeight(20);
        card.setArcWidth(20);
        card.setStroke(Color.WHITESMOKE);
        card.setStrokeWidth(1);

        // Set the X and Y position based on the index and gap
        int gap = 5;
        double x = cardCount * gap;
        card.setTranslateX(x);
        card.setTranslateY(0);

        root.getChildren().addAll(card);
        cards.add(card);
    }

    /**
     * This method is being used for the enemies of the player. The User should only know
     * how many cards do they have. everytime the enemies placed the card to the discard pile,
     * this method will be called.
     * @param root the container pane of the cards
     * @param cardCount how many cards currently on deck
     * @param cards remove the card from the list, that has been added before from the addCard
     */
    private void removeCard( StackPane root, int cardCount, List<Rectangle> cards ) {
        if (cardCount > 0) {
            Rectangle card = cards.remove(cardCount);
            root.getChildren().remove(card);
        }
    }

    /**
     * This function only be used to debug if the robot places the card correctly.
     * We suggest to use this function, only if the robot is not being controlled automatically
     * using i.e. using Button instead of Timer.
     * @param newBot The bot who draws the card
     * @param monsterContainer which container in javaFX
     */
    @SuppressWarnings("unused")
    private void debugButton( Player newBot, VBox monsterContainer ) {
        Button button = new Button("draw");
        button.setOnAction(actionEvent -> {
            try {
                String result = botPlay(newBot, gameService);
                updateGameScreen(gameService.getEncounter().getCurrentCard(),result);
                if(newBot.getCards().size() == 0){
                    showGameOverScene(newBot);
                }
            } catch (GameServiceException e) {
                System.err.println(e.getMessage());
            }
        });
        monsterContainer.getChildren().add(button);
    }

}
