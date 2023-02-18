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
    private final List<Button> buttons = new ArrayList<>();
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
    public Button withdraw;
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
        timer = new Timer(2500, e -> Platform.runLater(() -> {
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

            }
        }));
        timer.start();
    }

    @Override
    public Parent render() throws IOException {
        final FXMLLoader loader = new FXMLLoader(App.class.getResource("view/Game.fxml"));
        loader.setControllerFactory(callback -> this);
        Parent parent = loader.load();

        showPlayer();
        showBots();

        logout.setOnAction(actionEvent -> {
            app.show(new SetupController(app,new GameService(new Random())));
        });

        updateGameScreen(gameService.getEncounter().getCurrentCard(),gameService.getEncounter().getCurrentCard().getName());

        return parent;
    }

    @Override
    public void destroy() {
        timer.stop();
        player.listeners().removePropertyChangeListener(Player.PROPERTY_CARDS, playerListeners);
        player.listeners().removePropertyChangeListener(Player.PROPERTY_CURRENT_DRAW_PILE,currentDeckPilePlayerListener);
        for (int i = 0; i < totalBots; i++) {
            bots.get(i).listeners().removePropertyChangeListener(Player.PROPERTY_CARDS,botsListeners.get(i));
            bots.get(i).listeners().removePropertyChangeListener(Player.PROPERTY_CURRENT_DRAW_PILE,botsListeners.get(i+totalBots));
        }
    }


    private void showPlayer() {

        player = gameService.createPlayer(HUMAN, name);

        AtomicInteger indexCardOnClick = new AtomicInteger();
        for (Card card : player.getCards()) {
            if(card.getName().equals(WILDCARD_STRING)){
                showWildCard(card,player,indexCardOnClick);
            } else {
                Button button = showCards(indexCardOnClick, card);
                buttons.add(button);
                myContainer.getChildren().add(button);
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
                        Button newButton = showCards(indexCardOnClick, ((Card) news.getNewValue()));
                        buttons.add(newButton);
                        myContainer.getChildren().add(newButton);
                    }
                }
        };
        player.listeners().addPropertyChangeListener(Player.PROPERTY_CARDS, playerListeners);

        currentDeckPilePlayerListener = ev -> {
            showCurrentPlayerWithColour((Encounter) ev.getNewValue(), myContainer);
        };
        player.listeners().addPropertyChangeListener(Player.PROPERTY_CURRENT_DRAW_PILE, currentDeckPilePlayerListener);

        setWithdrawButton(player);

        if(player.getCurrentDrawPile() != null){ // start of the play, check if there is a bot who plays first!
            showCurrentPlayerWithColour(player.getCurrentDrawPile(), (Pane) myScrollPane.getContent());
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
        VBox vBox = new VBox();
        vBox.setSpacing(5.0);
        Text wildCardText = new Text("WILDCARD");
        vBox.getChildren().add(wildCardText);
        for (int i = 0; i < 4; i++) {
            Button colour = new Button(COLOURS[i].getName());
            int finalI = i;
            colour.setOnAction(actionEvent -> {
                // draw the wild card to the deck!
                // 15.02.2023
                // LOGIC FAILURE: BECAUSE OF THE PROPERTY LISTENER, THE SCREEN WILL BE REMOVE 2 TIMES!
//                myContainer.getChildren().remove(vBox);
                try {
                    Card choosenCard = card.setColour(COLOURS[finalI]);
                    indexCardOnClick.set(gameService.findIndexCards(card.getName(),card.getColour(), player));
                    String value = gameService.drawCard(choosenCard, player);
                    updateGameScreen(gameService.getEncounter().getCurrentCard(),value);
                } catch (GameServiceException e) {
                    System.err.println(e.getMessage());
                }
            });
            vBox.getChildren().add(colour);
        }
        myContainer.getChildren().add(vBox);
    }

    private Button showCards( AtomicInteger indexCardOnClick, Card card ) {
        Button button = new Button(card.getName() + "," + card.getColour());
        button.setOnAction(actionEvent -> {
            try {
                indexCardOnClick.set(gameService.findIndexCards(button.getText(), card.getColour(), player));
                String result = gameService.drawCard(card, player);
                updateGameScreen(card, result);
            } catch (GameServiceException e) {
                System.err.println(e.getMessage());
            }
        });
        return button;
    }

    private void updateGameScreen( Card card, String result ) {
        System.out.println(result + " " +
                                   card.getColour() + " currentPlayer: " + gameService.getEncounter().getCurrentPlayer());
        deckPileText.setText(gameService.getEncounter().getCurrentCard() + ", " +
                                     gameService.getEncounter().getCurrentCard().getColour()
                                     + "\ncurrentPlayer: " + gameService.getEncounter().getCurrentPlayer());
    }

    private void setWithdrawButton( Player player ) {
        withdraw.setOnAction(actionEvent -> {
            try {
                gameService.withdraw(player);
                updateGameScreen(gameService.getEncounter().getCurrentCard(),WITHDRAW);
            } catch (GameServiceException e) {
                System.err.println(e.getMessage());
            }
        });
    }

    private void showBots() {
        for (int i = 0; i < totalBots; i++) {
            Player newBot = gameService.createPlayer(BOT,"Bot"+(i+1));
            int finalI = i;

            //change the text on the container (the total of the card!)
            PropertyChangeListener botListener = news -> {
                VBox thisContainer = (VBox) enemiesContainer.getChildren().get(finalI);
                Text text = (Text) thisContainer.getChildren().get(0);
                text.setText(newBot.getName() +": "+ newBot.getCards().size());
            };
            newBot.listeners().addPropertyChangeListener(Player.PROPERTY_CARDS, botListener);
            bots.add(newBot); botsListeners.add(botListener);

            VBox monsterContainer = new VBox();
            monsterContainer.setAlignment(Pos.CENTER); monsterContainer.setId("Bot"+(i+1));
            monsterContainer.getChildren().add(new Text(newBot.getName() +": "+ newBot.getCards().size()));
            enemiesContainer.getChildren().add(monsterContainer);


            PropertyChangeListener drawPileBotListener = e -> {
                showCurrentPlayerWithColour((Encounter) e.getNewValue(), monsterContainer);
            } ;
            newBot.listeners().addPropertyChangeListener(Player.PROPERTY_CURRENT_DRAW_PILE, drawPileBotListener );
            botsListeners.add(drawPileBotListener);

            if(newBot.getCurrentDrawPile() != null){ // start of the play, check if there is a bot who plays first!
                showCurrentPlayerWithColour(newBot.getCurrentDrawPile(),monsterContainer);
            }
//            debugButton(newBot, monsterContainer);
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
