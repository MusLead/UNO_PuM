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
import java.util.Arrays;
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
    private final List<StackPane> cardPanes = new ArrayList<>();
    public ScrollPane myScrollPane;
    private PropertyChangeListener playerListeners;
    private PropertyChangeListener currentDeckPilePlayerListener;
    private Timer timer;
    private Player player;

//    private final List<Rectangle> cards = new ArrayList<>();
//    private int cardCount = 0;


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

    @Override
    public void init() {
        timer = new Timer(500, e -> Platform.runLater(() -> {
            Player checkPlayer = gameService.getEncounter().getCurrentPlayer();
            if(checkPlayer.getTypePlayer().equals(BOT)){
                for (Player bot : bots) {
                    if(bot.getName().equals(checkPlayer.getName())){
                        try {
                            updateGameScreen(gameService.getEncounter().getCurrentCard(),botDrawCard(bot,gameService));
                            if(bot.getCards().size() == 0){
                                showGameOverScene(bot);
                            }
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
    // Note: the timer will start if the player draws the card!
    // (player card's listener detect some changes on the list)
    @Override
    public Parent render() throws IOException {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/Game.fxml"));
        loader.setControllerFactory(callback -> this);
        Parent parent = loader.load();
        myContainer.getChildren().clear(); // before gives a card, clear all card on the deck!

        showPlayer();
        showBots();

        logout.setOnAction(actionEvent -> app.show(new SetupController(app, new GameService(new Random()))));

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

    private void showPlayer() {

        player = gameService.createPlayer(HUMAN, name);

        AtomicInteger indexCardOnClick = new AtomicInteger();
        for (Card card : player.getCards()) {
            if(card.getName().equals(WILDCARD_STRING)){
                showWildCard(card,player,indexCardOnClick);
            } else {
                StackPane cardPane = showCards(indexCardOnClick,card);
                cardPanes.add(cardPane);
                myContainer.getChildren().add(cardPane);
            }
        }

        playerListeners = news -> {
//            System.out.println("News from player cards: " + news); //DEBUG

                if(player.getCards().size() == 0) {
                    showGameOverScene(player);
                }

                if(news.getOldValue() != null && news.getNewValue() == null) {
                    /*
                      if the old value not null and new value is null it means that the
                      player successfully draw the card then
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
                    if(((Card) news.getNewValue()).getName().contains(WILDCARD_STRING)){
                        showWildCard(((Card) news.getNewValue()),player, indexCardOnClick);
                    } else {
                        StackPane newCardPane = showCards(indexCardOnClick, ((Card) news.getNewValue()));
                        cardPanes.add(newCardPane);
                        myContainer.getChildren().add(newCardPane);
                    }
                }

                // start the timer for the robot to  work!
                timer.start();
        };
        player.listeners().addPropertyChangeListener(Player.PROPERTY_CARDS, playerListeners);

        currentDeckPilePlayerListener = ev -> showCurrentPlayerWithColour((Encounter) ev.getNewValue(), myContainer);
        player.listeners().addPropertyChangeListener(Player.PROPERTY_CURRENT_DISCARD_PILE, currentDeckPilePlayerListener);

        setWithdrawButton(player);

        if(player.getCurrentDiscardPile() != null){ // start of the play, check if there is a bot who plays first!
            showCurrentPlayerWithColour(player.getCurrentDiscardPile(), (Pane) myScrollPane.getContent());
        }
    }

    private void showCurrentPlayerWithColour( Encounter encounter, Pane pane ) {
        if(encounter != null){
            pane.setBackground(new Background(
                    new BackgroundFill(Color.AQUA,
                                       CornerRadii.EMPTY, Insets.EMPTY)));
        }
        if(encounter == null){
            pane.setBackground(new Background(
                    new BackgroundFill(Color.WHITESMOKE,
                                       CornerRadii.EMPTY, Insets.EMPTY)));

        }
    }

    private void showGameOverScene(Player player) {
        System.out.println(player.getName() + " WON!");
        myContainer.getChildren().clear();
        app.show(new GameOverController(player,app));
    }

    private void showWildCard( Card card, Player player, AtomicInteger indexCardOnClick ) {
//        this wildcard should be change!
        //TODO the buttons should be placed inside of the StackPane
        UnoCardController unoCardController = new UnoCardController(this,gameService,indexCardOnClick,card,player);
        StackPane stackPane;
        try {
            stackPane = (StackPane) unoCardController.render();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        VBox vBox = new VBox();
        vBox.setSpacing(5.0);
//        Text wildCardText = new Text("WILDCARD");
//        vBox.getChildren().add(wildCardText);


        for (int i = 0; i < 4; i++) {
            Button colour = new Button(COLOURS[i].getName());
            int finalI = i;
            colour.setOnAction(actionEvent -> {
                // draw the wild card to the deck!
                try {
                    Card choosenCard = card.setColour(COLOURS[finalI]);
                    drawCardAction(indexCardOnClick,choosenCard);
                } catch (GameServiceException e) {
                    System.err.println(e.getMessage());
                }
            });
            vBox.getChildren().add(colour);
        }
        vBox.setAlignment(Pos.CENTER);
        stackPane.getChildren().add(vBox);
        stackPane.setAlignment(Pos.CENTER);
        myContainer.getChildren().add(stackPane);
    }

    private StackPane showCards( AtomicInteger indexCardOnClick, Card card )  {
        UnoCardController unoCardController = new UnoCardController(this,gameService,indexCardOnClick,card,player);
        StackPane stackPane;
        try {
            stackPane = (StackPane) unoCardController.render();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stackPane;
    }

    private void drawCardAction( AtomicInteger indexCardOnClick, Card card) throws GameServiceException {
        String value = card.getName() + "," + card.getColour();
        indexCardOnClick.set(gameService.findIndexCards(value, card.getColour(), player));
        String result = gameService.drawCard(card, player);
        updateGameScreen(card, result);
    }

    protected void updateGameScreen( Card card, String result ) {
        if(result != null && result.equals(SUCCESS)){
            // meaning there is a player who has successfully drawn all their cards
            timer.stop(); // set timer to stop so that no robots draw their card!
            return; // do not print anything
        }
        if(gameService.getEncounter().getCurrentCard().getName().equals(WILDCARD_STRING)){
            card.setColour(gameService.getEncounter().getCurrentCard().getColour());
        }
        UnoCardController unoCardController = new UnoCardController(this,gameService,gameService.getEncounter().getCurrentCard(), gameService.getEncounter().getCurrentPlayer());
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
        System.out.println(result + " " +
                                   card.getColour() + " currentPlayer: " + gameService.getEncounter().getCurrentPlayer()); //DEBUG
//        deckPileText.setText(gameService.getEncounter().getCurrentCard() + ", " +
//                                     gameService.getEncounter().getCurrentCard().getColour()
//                                     + "\ncurrentPlayer: " + gameService.getEncounter().getCurrentPlayer()); //DEBUG
        deckPileText.setText("Your Turn: " + gameService.getEncounter().getCurrentPlayer()); //DEBUG
    }

    private void setWithdrawButton( Player player ) {
        withdraw.setOnMouseClicked(actionEvent -> {
            try {
                gameService.withdraw(player);
                updateGameScreen(gameService.getEncounter().getCurrentCard(),WITHDRAW);
            } catch (GameServiceException e) {
                System.err.println(e.getMessage());
            }
        });
//        withdraw.setOnAction(actionEvent -> {
//            try {
//                gameService.withdraw(player);
//                updateGameScreen(gameService.getEncounter().getCurrentCard(),WITHDRAW);
//            } catch (GameServiceException e) {
//                System.err.println(e.getMessage());
//            }
//        });
    }

    private void showBots() {
        final int[] cardCount = {0,0,0};
        for (int i = 0; i < totalBots; i++) {
            StackPane pane = new StackPane();
            pane.setAlignment(Pos.CENTER_LEFT);
            Player newBot = gameService.createPlayer(BOT,"Bot"+(i+1));
            int finalI = i;
            List<Rectangle> cards = new ArrayList<>();
            for (int j = 0; j < newBot.getCards().size(); j++) {
                addCard(pane,j,cards);
            }

            //change the text on the container (the total of the card!)
            PropertyChangeListener botListener = news -> {
                System.out.println(Arrays.toString(cardCount) + " News from bot"+finalI+" cards: " + news); //DEBUG
                if(news.getOldValue() != null && news.getNewValue() == null){
                    removeCard(pane, newBot.getCards().size(),cards);
                }
                if(news.getOldValue() == null && news.getNewValue() != null){
                    addCard(pane, newBot.getCards().size(),cards);
                }
                VBox thisContainer = (VBox) enemiesContainer.getChildren().get(finalI);
                Text text = (Text) thisContainer.getChildren().get(0);
                text.setText(newBot.getName() +": "+ newBot.getCards().size());
            };
            newBot.listeners().addPropertyChangeListener(Player.PROPERTY_CARDS, botListener);
            bots.add(newBot); botsListeners.add(botListener);

            VBox monsterContainer = new VBox();
            monsterContainer.setAlignment(Pos.CENTER);
            monsterContainer.setId("Bot"+(i+1));
            monsterContainer.getChildren().addAll(new Text(newBot.getName() +": "+ newBot.getCards().size()),pane);

            enemiesContainer.getChildren().add(monsterContainer);
            PropertyChangeListener drawPileBotListener = e -> showCurrentPlayerWithColour((Encounter) e.getNewValue(), monsterContainer);
            newBot.listeners().addPropertyChangeListener(Player.PROPERTY_CURRENT_DISCARD_PILE, drawPileBotListener );
            botsListeners.add(drawPileBotListener);

            if(newBot.getCurrentDiscardPile() != null){ // start of the play, check if there is a bot who plays first!
                showCurrentPlayerWithColour(newBot.getCurrentDiscardPile(),monsterContainer);
            }
//            debugButton(newBot, monsterContainer);
        }
    }

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

    private void removeCard( StackPane root, int cardCount, List<Rectangle> cards ) {
        if (cardCount > 0) {
            Rectangle card = cards.remove(cardCount);
            root.getChildren().remove(card);
        }
    }

    /**
     * This function only be used to debug if the robot draws the card correctly.
     * We suggest to use this function, only if the robot is not being controlled automatically
     * using i.e. Timer
     * @param newBot The bot who draws the card
     * @param monsterContainer which container in javaFX
     */
    @SuppressWarnings("unused")
    private void debugButton( Player newBot, VBox monsterContainer ) {
        Button button = new Button("draw");
        button.setOnAction(actionEvent -> {
            try {
                String result = botDrawCard(newBot, gameService);
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
