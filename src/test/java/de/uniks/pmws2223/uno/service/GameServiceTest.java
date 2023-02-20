package de.uniks.pmws2223.uno.service;


import de.uniks.pmws2223.uno.Constants;
import de.uniks.pmws2223.uno.model.Player;
import org.junit.Test;

import java.util.Random;

import static de.uniks.pmws2223.uno.Constants.*;
import static de.uniks.pmws2223.uno.service.BotService.botPlay;
import static org.junit.Assert.*;

public class GameServiceTest {

    /**
     * a simple test on create player
     */
    @Test
    public void CreatePlayer(){
        /*
         * before we start every player needs
         * randomized cards
         */
        System.out.println("Create Player Test");
        GameService gameService = new GameService();

        Player player = gameService.createPlayer(HUMAN, "Agha");
        assertEquals(7,player.getCards().size());

        Player bot = gameService.createPlayer(BOT, "Bot1");
        assertEquals(7, bot.getCards().size());
    }

    /**
     * this is a simple test
     * if a player gives a card, test the total of the card the player has
     * will the bot also be able to play?
     * <p>
     * VARIABLE INITIALIZE FAILURE? : Random should be defined globally
     * to avoid a difference of the result
     */
    @Test
    public void placeCard() throws GameServiceException {

        System.out.println("Draw Card Test");
        Random random1 = new Random(100);
        GameService gameService = new GameService(random1);
        gameService.getEncounter().setCurrentCard(gameService.getConstants().DRAW_TWO_BLUE);

        final Player player = gameService.createPlayer(HUMAN, "Agha");
        final Player bot1 = gameService.createPlayer(BOT, "Bot1");
        final Player bot2 = gameService.createPlayer(BOT, "Bot2");
        final Player bot3 = gameService.createPlayer(BOT, "Bot3");

        String value;
        System.out.println("Card on deck: " + gameService.getEncounter().getCurrentCard());
        assertEquals(DRAW_TWO,gameService.getEncounter().getCurrentCard().getName());

        value = gameService.placeCard(player.getCards().get(5), player);

        if(!value.equals(NOT_VALID)) {
            assertEquals(6, player.getCards().size());
            assertEquals(DRAW_TWO + "," + gameService.getConstants().DRAW_TWO_YELLOW.getColour(), value);
            assertEquals(9, bot1.getCards().size());

            value = botPlay(bot1, gameService);
            assertEquals(6,player.getCards().size());
            assertEquals(DRAW_TWO+","+ gameService.getConstants().DRAW_TWO_BLUE.getColour(),value);
            assertEquals(8,bot1.getCards().size());

            value = botPlay(bot2, gameService);
            assertEquals(6,player.getCards().size());
            assertEquals(gameService.getConstants().BLUE_8+","+ gameService.getConstants().BLUE_8.getColour(),value);
            assertEquals(7,bot3.getCards().size());
        } else {
            gameService.withdraw(player);

            value = botPlay(bot1, gameService);
            assertEquals(8,player.getCards().size());
            assertEquals(REVERSE+","+ gameService.getConstants().REVERSE_BLUE.getColour(),value);
            assertEquals(6,bot1.getCards().size());

        }

    }

    /**
     * test if player withdraw, they will get more card
     * @throws GameServiceException otherwise throw an exception
     */
    @Test
    public void withdraw() throws GameServiceException {
        GameService gameService = new GameService();
        Player player = gameService.createPlayer(HUMAN, "Agha");
        assertEquals(7,player.getCards().size());
        gameService.withdraw(player);
        assertEquals(8,player.getCards().size());
    }

    /**
     * Test if the card is in the card, and return the index of the card
     */
    @Test
    public void findIndexCard(){
        // check if the function returns the same int as the chosen card
        GameService gameService = new GameService();
        Player player = gameService.createPlayer(HUMAN, "Agha");
        assertEquals(7,player.getCards().size());
        int index = gameService.findIndexCards(player.getCards().get(2), player);
        assertEquals(2,index);
    }

    /**
     * Test if this method can add more card the player
     * @throws GameServiceException otherwise it throws an error
     */
    @Test
    public void addRandomCard() throws GameServiceException {
        Random random1 = new Random(100);
        GameService gameService = new GameService(random1);
        Player player = gameService.createPlayer(HUMAN, "Agha");
        gameService.addRandomCard(player);
        assertEquals(8,player.getCards().size());
    }


    /**
     * test if this function can return true
     * if the player choose a right card to place
     * on discard pile
     */
    @Test
    public void isConditionTrue() {
        Random random1 = new Random(100);
        GameService gameService = new GameService(random1);
        System.out.println(gameService.getEncounter().getCurrentCard());
        Player player = gameService.createPlayer(HUMAN, "Agha");
        assertTrue(GameService.isConditionTrue(player.getCards().get(2), gameService.getEncounter().getCurrentCard()));
    }

    /**
     * test if this method will make the next player as
     * current player in Encounter
     * @throws GameServiceException otherwise it throws an error
     */
    @Test
    @SuppressWarnings("unused")
    public void nextCurrentPlayer() throws GameServiceException {
        Random random1 = new Random(100);
        GameService gameService = new GameService(random1);
        Player player = gameService.createPlayer(HUMAN, "Agha");
        Player bot = gameService.createPlayer(BOT,"asd");
        gameService.nextCurrentPlayer();
        assertEquals(bot.getName(),gameService.getEncounter().getCurrentPlayer().getName());
    }

    /**
     * Test if the player can set the wildcard colour!
     * @throws GameServiceException otherwise it throws an error
     */
    @Test
    public void setWildcardColour() throws GameServiceException {
        Random random1 = new Random(100);
        GameService gameService = new GameService(random1);
        Player player = gameService.createPlayer(HUMAN,"asdf");
        player.withCards(new Constants().WILDCARD);
        assertNotNull(gameService.setWildcardColour(player.getCards().get(7),BLUE).getColour());
    }
}
