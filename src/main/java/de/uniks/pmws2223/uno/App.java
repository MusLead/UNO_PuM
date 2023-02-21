package de.uniks.pmws2223.uno;

import de.uniks.pmws2223.uno.controller.Controller;
import de.uniks.pmws2223.uno.controller.GameOverController;
import de.uniks.pmws2223.uno.controller.SetupController;
import de.uniks.pmws2223.uno.service.GameService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class App extends Application {
    private Stage stage;
    private Controller controller;

    private final GameService gameService;

    public GameService getGameService() {
        return gameService;
    }

    // !!!
    /* WITHOUT THIS CONSTRUCTOR, IT WILL RETURN SUCH WIERD FAILURE MESSAGE:

        Feb 19, 2023 4:35:38 AM com.sun.javafx.application.PlatformImpl startup
    WARNING: Unsupported JavaFX configuration: classes were loaded from 'unnamed module @239adefd'
    Exception in Application constructor
    Exception in thread "main" java.lang.RuntimeException: Unable to construct Application instance: class de.uniks.pmws2223.uno.App
        at com.sun.javafx.application.LauncherImpl.launchApplication1(LauncherImpl.java:891)
        at com.sun.javafx.application.LauncherImpl.lambda$launchApplication$2(LauncherImpl.java:196)
        at java.base/java.lang.Thread.run(Thread.java:833)
    Caused by: java.lang.NoSuchMethodException: de.uniks.pmws2223.uno.App.<init>()
        at java.base/java.lang.Class.getConstructor0(Class.java:3585)
        at java.base/java.lang.Class.getConstructor(Class.java:2271)
        at com.sun.javafx.application.LauncherImpl.lambda$launchApplication1$8(LauncherImpl.java:802)
        at com.sun.javafx.application.PlatformImpl.lambda$runAndWait$12(PlatformImpl.java:484)
        at com.sun.javafx.application.PlatformImpl.lambda$runLater$10(PlatformImpl.java:457)
        at java.base/java.security.AccessController.doPrivileged(AccessController.java:399)
        at com.sun.javafx.application.PlatformImpl.lambda$runLater$11(PlatformImpl.java:456)
        at com.sun.glass.ui.InvokeLaterDispatcher$Future.run(InvokeLaterDispatcher.java:96)

    Unsupported JavaFX configuration: classes were loaded from 'unnamed module @239adefd'

    Caused by: java.lang.NoSuchMethodException: de.uniks.pmws2223.uno.App.<init>()

    Execution failed for task ':Main.main()'.
    > Process 'command '/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home/bin/java'' finished with non-zero exit value 1

    * Try:
    > Run with --stacktrace option to get the stack trace.
    > Run with --info or --debug option to get more log output.
    > Run with --scan to get full insights.
     */
    @SuppressWarnings("unused")
    public App(){
        this.gameService = new GameService(new Random());
    }
    // !!!

    public App( Random random ) {
        this.gameService = new GameService(random);
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.setScene(new Scene(new Label("Loading...")));
        primaryStage.setTitle("Uno");

        show(new SetupController(this, gameService));
        primaryStage.show();

        primaryStage.setOnCloseRequest(e -> controller.destroy());
    }

    @Override
    public void stop()
    {
        controller.destroy();
    }

    public void show(Controller controller) {
        controller.init();
        try {
            stage.getScene().setRoot(controller.render());

        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        stage.sizeToScene();

        if (this.controller != null) {
            this.controller.destroy();
        }
        this.controller = controller;
        stage.setTitle(controller.getTitle());
        if(controller instanceof GameOverController) {
            getGameService().getCountDownLatch().countDown(); // to make sure the FXRobot do the action further!
        }
    }
}