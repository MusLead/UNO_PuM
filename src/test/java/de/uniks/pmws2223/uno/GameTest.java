package de.uniks.pmws2223.uno;

import com.googlecode.junittoolbox.ParallelRunner;
import de.uniks.pmws2223.uno.service.GameServiceTest;
import javafx.geometry.HorizontalDirection;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.ScrollEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.stage.Stage;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.testfx.framework.junit.ApplicationTest;


import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;

//@RunWith(ParallelRunner.class)
public class GameTest extends ApplicationTest {

//    private static AtomicBoolean running = GameServiceTest.running;
//
//    @Before
//    public void setUp() throws InterruptedException {
//        while (running.get()) {
//            System.out.println("waiting..");
//            Thread.sleep(100);
//        }
//    }
    private Stage stage;

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Random random = new Random(122);
        App app = new App(random);
        app.start(stage);
    }

    @Test
    public void gameCirculationTest(){

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

        int i = 0;
        while(i < 5) {
            while(!hBox.getBackground().getFills().get(0).getFill().equals(Color.AQUA)){
                //busy waiting ...
            }
            try {
                for (Node child : hBox.getChildren()) {
                    if (child.isVisible() && child instanceof Button) {
                        try {
                            clickOn(child);
                        } catch (RuntimeException runtimeException) {
                            System.err.println("ups not found");
                            clickOn("#withdraw");
                        }
                    } else if (!child.isVisible()) {
                        //TODO??? what to do if the bot cannot find the button?
                    }
                    if (!hBox.getBackground().getFills().get(0).getFill().equals(Color.AQUA)) {
                        break;
                    }
                }
            } catch (ConcurrentModificationException ignored) {

            }
            if(hBox.getBackground().getFills().get(0).getFill().equals(Color.AQUA)){
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

}
