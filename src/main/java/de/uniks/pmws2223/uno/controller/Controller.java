package de.uniks.pmws2223.uno.controller;

import javafx.scene.Parent;

import java.io.IOException;

/**
 * THIS IS A CONVENTIONAL CONTROLLER FOR JAVAFX PROJECT FROM UNI. KASSEL
 */
public interface Controller {
    /**
     * The title of the Screen
     * @return the string of tittle
     */
    String getTitle();

    /**
     * This method is being used for
     * the operation that does not produce any direct element of
     * javaFX, such as Timer.
     */
    void init();

    /**
     * This will render the main screen with the components that has been implemented within the methods
     * @return the parent of this controller, that will be executed later on
     * @throws IOException throw an exception if loader is not able to return a valid parent
     */
    Parent render() throws IOException;

    /**
     * This method will be called everytime the screen changes or being closed.
     * This method usually implemented inside of App.java
     */
    void destroy();
}