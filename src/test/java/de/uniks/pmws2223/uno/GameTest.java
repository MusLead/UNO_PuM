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

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        Random random = new Random(12);
        App app = new App(random);
        app.start(stage);
    }

    @Test
    public void simpleCirculationTest() throws InterruptedException {
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
        out: while(stage.getTitle().equals(battleTitle) && i < 5) {
            while(!hBox.getBackground().getFills().get(0).getFill().equals(Color.AQUA)){
                //busy waiting ...
                /*
                 * Without sleep, there could be a memory leak, since there are 2 different
                 * threads run together, FXrobot and the source program (controller and services)
                 *
                 * Chat GPT:
                 * FXRobot is a utility class that provides methods for simulating user input,
                 * such as clicking on buttons and typing in text fields, in a JavaFX application.
                 * When you call a method on FXRobot, it executes that action in a separate thread,
                 * not in the thread that your application is running in. This is because JavaFX
                 * has a single UI thread, also known as the "JavaFX Application Thread",
                 * which is responsible for handling user events, updating the UI, and executing
                 * code that interacts with the UI.
                 *
                 * https://openjfx.io/javadoc/16/javafx.graphics/javafx/test/FXRobot.html
                 * */
                Thread.sleep(1000);
                if(!stage.getTitle().equals(battleTitle)){
                    break out;
                }
            }

            HBox deckContainer = lookup("#deckContainer").queryAs(HBox.class);
            StackPane pane = (StackPane) deckContainer.getChildren().get(2);
            Label textDeck = (Label) pane.getChildren().get(1);
            String stringDeck = textDeck.getText();
            Rectangle rectangleDeck = (Rectangle) pane.getChildren().get(0);
            Color colorDeck = (Color) rectangleDeck.getFill();

            int tempt = 0, indexCard = -1;
            // https://www.geeksforgeeks.org/how-to-solve-concurrentmodificationexception-in-java/
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

//        clickOn("#mainMenuButton");
        //• Leave-Button klicken
        clickOn("#logout");

        //• Fenstertitel erneut prüfen
        assertEquals(startTitle,stage.getTitle());

        //• Prüfen, dass das Eingabefeld für den Spielernamen leer ist
        userName = lookup("#nameField").queryAs(TextField.class);
        assertEquals("", userName.getText());
    }


    @Test
    public void gameCirculationTest() throws InterruptedException {

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
        ChoiceBox choiceBoxBots = lookup("#choiceBoxBots").queryAs(ChoiceBox.class);
        clickOn(choiceBoxBots);
        clickOn("3");

        //• Start-Button klicken, um ein Encounter zu starten
        clickOn("#logInButton");

        //• Neuen Fenstertitel prüfen
        assertEquals(battleTitle,stage.getTitle());

        HBox hBox = lookup("#myContainer").queryAs(HBox.class);
        ScrollPane scrollPane = lookup("#myScrollPane").queryAs(ScrollPane.class);

        out: while(stage.getTitle().equals(battleTitle)) {
            while(!hBox.getBackground().getFills().get(0).getFill().equals(Color.AQUA)){
                //busy waiting ...
                /*
                * Without sleep, there could be a memory leak, since there are 2 different
                * threads run together, FXrobot and the source program (controller and services)
                *
                * Chat GPT:
                * FXRobot is a utility class that provides methods for simulating user input,
                * such as clicking on buttons and typing in text fields, in a JavaFX application.
                * When you call a method on FXRobot, it executes that action in a separate thread,
                * not in the thread that your application is running in. This is because JavaFX
                * has a single UI thread, also known as the "JavaFX Application Thread",
                * which is responsible for handling user events, updating the UI, and executing
                * code that interacts with the UI.
                *
                * https://openjfx.io/javadoc/16/javafx.graphics/javafx/test/FXRobot.html
                * */
                Thread.sleep(1000);
                if(!stage.getTitle().equals(battleTitle)){
                    //if the robot stil plays the game but
                    // the screen has changed, then just quit!
                    break out;
                }
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
                for (Node child : hBox.getChildren()) {
                    assert  child instanceof StackPane : " Not a card from UnoCard";

                    StackPane card = (StackPane) child;
                    Label text = (Label) card.getChildren().get(1);
                    string = text.getText();
                    Rectangle rectangle = (Rectangle) card.getChildren().get(0);
                    colorCard = (Color) rectangle.getFill();

                    if(string.equals(stringDeck) || colorCard.equals(colorDeck)){ //compare the value
                        //in this hBox there is a card with one or more of the same components
                        // with the card deck, save the index, and the remove it later!
                        if(string.equals(stringDeck)){
                            assertEquals(stringDeck,string);
                        }
                        if(colorCard.equals(colorDeck)) {
                            assertEquals(colorDeck, colorCard);
                        }
                        indexCard = tempt;
                        break;
                    }
                    tempt++;
                }
                if(indexCard >= 0) {
                    // remove the card if we found a target when we iterate hBox children
                    //if the target is not visible, or there might be an error
                    // try to scroll the pane
                    StackPane target = (StackPane) hBox.getChildren().get(indexCard);
                    if(target.isVisible()){
                        try{
                            clickOn(target);
                        } catch (RuntimeException be){
                            scrollPane.setHvalue(1);
                            assertEquals(1, scrollPane.getHvalue(), 0.001);
//                            clickOn(target);
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
        }

        clickOn("#mainMenuButton");
        //• Leave-Button klicken
//        clickOn("#logout");

        //• Fenstertitel erneut prüfen
        assertEquals(startTitle,stage.getTitle());

        //• Prüfen, dass das Eingabefeld für den Spielernamen leer ist
        userName = lookup("#nameField").queryAs(TextField.class);
        assertEquals("", userName.getText());
    }

}
