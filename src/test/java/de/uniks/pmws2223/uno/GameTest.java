package de.uniks.pmws2223.uno;

import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import org.junit.Test;
import org.testfx.framework.junit.ApplicationTest;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class GameTest extends ApplicationTest {

    private Stage stage;
    private App app;
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Random random = new Random(56);
        app = new App(random);
        app.start(stage);
    }

    /**
     * This is a test where it tests the circulation of the game
     * until a few round and then log out
     */
    @Test
    public void simpleCirculationTest() {
        final String startTitle = "UNO - Setup";
        final String battleTitle = "UNO - Ingame";

        //• Initialen Fenstertitel prüfen
        assertEquals(startTitle,stage.getTitle());

        //• Spielernamen „Alice“ in das dafür vorgesehenen Eingabefeld eingeben.
        clickOn("#nameField");
        final String name = "Alice";
        write(name);
        TextField userName = lookup("#nameField").queryAs(TextField.class);
        assertEquals(name, userName.getText());
        @SuppressWarnings("rawtypes")
        ChoiceBox choiceBoxBots = lookup("#choiceBoxBots").queryAs(ChoiceBox.class);
        clickOn(choiceBoxBots);
        clickOn("3");

        //• Start-Button klicken, um ein Encounter zu starten
        clickOn("#logInButton");

        //• Neuen Fenstertitel prüfen
        assertEquals(battleTitle,stage.getTitle());

        HBox hBox = lookup("#myContainer").queryAs(HBox.class);
        ScrollPane scrollPane = lookup("#myScrollPane").queryAs(ScrollPane.class);

        // Scroll the ScrollPane to the right
        scrollPane.setHvalue(0.5);
        assertEquals(0.5, scrollPane.getHvalue(), 0.001);

        // Scroll the ScrollPane back to the left
        scrollPane.setHvalue(0.0);
        assertEquals(0.0, scrollPane.getHvalue(), 0.001);

        int i = 0;
        while(stage.getTitle().equals(battleTitle) && i < 5) {
            try { // this is for the FXRobot when dealing  withTimer from IngameController init
                if(i!=0) {
                    app.getGameService().getCountDownLatch().await();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if(!stage.getTitle().equals(battleTitle)){
                //it could be in the middle of the play the screen has already game over!
                break;
            }

            HBox deckContainer = lookup("#deckContainer").queryAs(HBox.class);
            StackPane pane = (StackPane) deckContainer.getChildren().get(2);
            Label textDeck = (Label) pane.getChildren().get(1);
            String stringDeck = textDeck.getText();
            Rectangle rectangleDeck = (Rectangle) pane.getChildren().get(0);
            Color colorDeck = (Color) rectangleDeck.getFill();

            int tempt = 0, indexCard = -1;
            // https://www.geeksforgeeks.org/how-to-solve-concurrentmodificationexception-in-java/
            /*
            Conclusion, do not remove the element inside the list
            during the iteration of the list
             */
            for (Node child : hBox.getChildren()) {
                assert  child instanceof StackPane : "Not a card from UnoCard";

                StackPane card = (StackPane) child;
                Label text = (Label) card.getChildren().get(1);
                String string = text.getText();
                Rectangle rectangle = (Rectangle) card.getChildren().get(0);
                Color colorCard = (Color) rectangle.getFill();

                if(string.equals(stringDeck) || colorCard.equals(colorDeck)){
                    indexCard = tempt;
                    break;
                }
                tempt++;
            }
            if(indexCard >= 0) {
                StackPane target = (StackPane) hBox.getChildren().get(indexCard);
                if(target.isVisible()){
                    clickOn(target);
                } else if (!target.isVisible()) {
                    scrollPane.setHvalue(0.5);
                    assertEquals(0.5, scrollPane.getHvalue(), 0.001);
                    if(scrollPane.getHvalue() >= scrollPane.getHmax()){
                        // Scroll the ScrollPane back to the left
                        scrollPane.setHvalue(0.0);
                        assertEquals(0.0, scrollPane.getHvalue(), 0.001);
                    }
                }
            }
            if(stage.getTitle().equals(battleTitle) && hBox.getBackground().getFills().get(0).getFill().equals(Color.AQUA)){
                clickOn("#withdraw");
            }
            i++;

        }

        //• Leave-Button klicken
        clickOn("#logout");

        //• Fenstertitel erneut prüfen
        assertEquals(startTitle,stage.getTitle());

        //• Prüfen, dass das Eingabefeld für den Spielernamen leer ist
        userName = lookup("#nameField").queryAs(TextField.class);
        assertEquals("", userName.getText());
    }

    /**
     * This is a test of the game where the players
     * play until someone win the game
     */
    @Test
    public void gameCirculationTest() {

        final String startTitle = "UNO - Setup";
        final String battleTitle = "UNO - Ingame";

        //• Initialen Fenstertitel prüfen
        assertEquals(startTitle,stage.getTitle());

        //• Spielernamen „Alice“ in das dafür vorgesehenen Eingabefeld eingeben.
        clickOn("#nameField");
        final String name = "Alice";
        write(name);
        TextField userName = lookup("#nameField").queryAs(TextField.class);
        assertEquals(name, userName.getText());
        @SuppressWarnings("rawtypes")
        ChoiceBox choiceBoxBots = lookup("#choiceBoxBots").queryAs(ChoiceBox.class);
        clickOn(choiceBoxBots);
        clickOn("3");

        //• Start-Button klicken, um ein Encounter zu starten
        clickOn("#logInButton");

        //• Neuen Fenstertitel prüfen
        assertEquals(battleTitle,stage.getTitle());

        HBox hBox = lookup("#myContainer").queryAs(HBox.class);
        ScrollPane scrollPane = lookup("#myScrollPane").queryAs(ScrollPane.class);
        int i = 0;
        //play the game!
        while(stage.getTitle().equals(battleTitle)) {
            //check if it is our turn to place/draw the card

            try { // this is for the FX robot I guess
                if(i!=0) {
                    app.getGameService().getCountDownLatch().await();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(!stage.getTitle().equals(battleTitle)){
                //it could be in the middle of the play the screen has already game over!
                break;
            }

            //we want to take an index of the card on the pile
            //therefore the value and the colour of the card
            //compare it later!
            HBox deckContainer = lookup("#deckContainer").queryAs(HBox.class);
            StackPane pane = (StackPane) deckContainer.getChildren().get(2);
            Label textDeck = (Label) pane.getChildren().get(1);
            String stringDeck = textDeck.getText();
            Rectangle rectangleDeck = (Rectangle) pane.getChildren().get(0);
            Color colorDeck = (Color) rectangleDeck.getFill();
            String string;
            Color colorCard;

            int tempt = 0, indexCard = -1;
            // https://www.geeksforgeeks.org/how-to-solve-concurrentmodificationexception-in-java/
            // make sure not changing/removing the element of the list while iterating the List!
                for (Node child : hBox.getChildren()) {
                    //iterate the card, if there is a card that we can place it to discard pile
                    assert child instanceof StackPane : "Not a card from UnoCard";

                    StackPane card = (StackPane) child;
                    Label text = (Label) card.getChildren().get(1);
                    string = text.getText();
                    Rectangle rectangle = (Rectangle) card.getChildren().get(0);
                    colorCard = (Color) rectangle.getFill();

                    if(string.equals(stringDeck) || colorCard.equals(colorDeck)){ //compare the value
                        //in this hBox there is a card with one or more of the same components
                        // with the card deck, save the index, and the remove it later!
                        if(string.equals(stringDeck)){ // does the card has the same value "numbers or action?"
                            assertEquals(stringDeck,string);
                        }
                        if(colorCard.equals(colorDeck)) { // does the card has the same colour
                            assertEquals(colorDeck, colorCard);
                        }
                        indexCard = tempt; // IF WE FOUND THE TARGET, SAVE THE INDEX VALUE!
                        break;
                    }
                    tempt++;
                }
                if(indexCard >= 0) {
                    // remove the card if we found a target when we iterated hBox children
                    // if the target is not visible, or there might be an error
                    // try to scroll the pane
                    StackPane target = (StackPane) hBox.getChildren().get(indexCard);
                    if(target.isVisible()){
                        try{
                            clickOn(target);
                        } catch (RuntimeException be){
                            scrollPane.setHvalue(1);
                            assertEquals(1, scrollPane.getHvalue(), 0.001);
                            if(scrollPane.getHvalue() >= scrollPane.getHmax()){
                                // Scroll the ScrollPane back to the left
                                scrollPane.setHvalue(0.0);
                                assertEquals(0.0, scrollPane.getHvalue(), 0.001);
                            }
                        }
                    } else if (!target.isVisible()) {
                        scrollPane.setHvalue(1);
                        assertEquals(1, scrollPane.getHvalue(), 0.001);
                        clickOn(target);
                        if(scrollPane.getHvalue() >= scrollPane.getHmax()){
                            // Scroll the ScrollPane back to the left
                            scrollPane.setHvalue(0.0);
                            assertEquals(0.0, scrollPane.getHvalue(), 0.001);
                        }
                    }
                }
            if(stage.getTitle().equals(battleTitle) && hBox.getBackground().getFills().get(0).getFill().equals(Color.AQUA)){
                clickOn("#withdraw");
            }
            i++;
        }

        // GAME OVER
        //• Leave-Button klicken
        clickOn("#mainMenuButton");


        //• Fenstertitel erneut prüfen
        assertEquals(startTitle,stage.getTitle());

        //• Prüfen, dass das Eingabefeld für den Spielernamen leer ist
        userName = lookup("#nameField").queryAs(TextField.class);
        assertEquals("", userName.getText());
    }

}
