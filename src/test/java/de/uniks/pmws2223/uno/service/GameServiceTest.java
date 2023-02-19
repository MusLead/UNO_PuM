package de.uniks.pmws2223.uno.service;


import de.uniks.pmws2223.uno.model.Card;
import de.uniks.pmws2223.uno.model.Player;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.uniks.pmws2223.uno.Constants.*;
import static de.uniks.pmws2223.uno.service.BotService.botPlay;
import static org.junit.Assert.assertEquals;

public class GameServiceTest {

    private final Random random = new Random(100);
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
     * VARIABLE INITIALIZE FAILURE : Random should be defined globally
     * to avoid a difference of the result
     */
    @Test
    public void drawCard() throws GameServiceException {

        System.out.println("Draw Card Test");
        GameService gameService = new GameService(random);
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
     * This is a test between 3 robots and player.
     * Assumption: if this test run smoothly,
     * then the game will do the round also without a problem
     */
    @Test
    public void roundTest() throws GameServiceException {
        System.out.println("roundTest");
        Random random = new Random(234243); //TODO set a seed before submission!, make sure you are ok with this seed!
        GameService gameService = new GameService(random);

        final Player player = gameService.createPlayer(HUMAN,"Agha");
        final Player bot1 = gameService.createPlayer(BOT, "Bot1");
        final Player bot2 = gameService.createPlayer(BOT, "Bot2");
        final Player bot3 = gameService.createPlayer(BOT, "Bot3");

        //player plays first
        while(bot1.getCards().size() != 0 && bot2.getCards().size() != 0 && bot3.getCards().size() != 0 && player.getCards().size() != 0) {
            String status = null;

            Player encounterPlayerBefore = gameService.getEncounter().getCurrentPlayer();
            System.out.println("player is playing: " + gameService.getEncounter().getCurrentPlayer());

            if(gameService.getEncounter().getCurrentPlayer().equals(player)){
                status = playerDraw(random, gameService, player);
            } else if(gameService.getEncounter().getCurrentPlayer().equals(bot1)){
                status = botPlay(bot1, gameService);
            } else if(gameService.getEncounter().getCurrentPlayer().equals(bot2)){
                status = botPlay(bot2, gameService);
            } else if(gameService.getEncounter().getCurrentPlayer().equals(bot3)) {
                status = botPlay(bot3, gameService);
            }

            Player encounterPlayerNow = gameService.getEncounter().getCurrentPlayer();


            if(status != null && status.contains(REVERSE) && gameService.getDirection().equals(LEFT_WAY)){
                Player nowPlayer = gameService.getEncounter().getCurrentPlayer();
                Player expectedBeforePlayer = null;
                int indexMax = gameService.getEncounter().getPlayers().size();
                for (int i = 0; i < indexMax; i++) {
                    Player iteratePlayer = gameService.getEncounter().getPlayers().get(i);
                    if(iteratePlayer.equals(nowPlayer)){
                        int nextIndex = (i + 1) % indexMax;
                        expectedBeforePlayer = gameService.getEncounter().getPlayers().get(nextIndex);
                    }
                }
                assertEquals(expectedBeforePlayer,encounterPlayerBefore);
                assertEquals(nowPlayer,encounterPlayerNow);
            }

            System.out.println("player has: " + player.getCards().size() +
                                       " cards | bot1 has: " + bot1.getCards().size() + " cards | " +
                    "bot2 has: " + bot2.getCards().size() + " cards | " +
                    "bot3 has: " + bot3.getCards().size() + " cards "); //DEBUG

        }

    }

    /** TODO: Submit this? really?
     * This function is only being used for the player type human to draw the card
     * This function actually let the player choose the card randomly. It looks through the List of cards
     * until every possible index has been reached, then say that the player does not have the card that
     * suitable to place the card on discard pile
     * @param random the index that currently will be looking to
     * @param gameService the game-service
     * @param player the player who wants to place a card
     * @return string, whether it successful or nor
     * @throws GameServiceException from GameService, there could be some mistake being thrown
     */
    private static String playerDraw( Random random, GameService gameService, Player player ) throws GameServiceException {
        String status;
        Card drawnCard;
        int maxSized = player.getCards().size();
        List<Integer> takenIndex = new ArrayList<>();

        int index = random.nextInt(maxSized);
        takenIndex.add(index);
        if(player.getCards().get(index).equals(gameService.getConstants().WILDCARD)){
            int indexW = random.nextInt(COLOURS.length);
            //System.out.println(WILDCARD); //DEBUG
            player.getCards().get(0).setColour(COLOURS[indexW]);
        }
        drawnCard= player.getCards().get(index);
        status = gameService.placeCard(drawnCard, player); //HERE PLAYER DRAW THE CARD

        if(status.equals(NOT_VALID)){
            System.out.println("\nCard on deck:" + gameService.getEncounter().getCurrentCard() + ", " +
                                       gameService.getEncounter().getCurrentCard().getColour()
                                       + "\nPlayer's Card: " + player.getCards() + "\nTaken Index: ");
            int tempt = 1;

            int rand = index;
            System.out.print(rand+ ", ");
            while(tempt < maxSized){
                rand = random.nextInt(maxSized);
                drawnCard= player.getCards().get(rand);
                status = gameService.placeCard(drawnCard, player); //HERE PLAYER DRAW THE CARD
                if(!status.equals(NOT_VALID)){
                    //if the status is something else than NOT_VALID,
                    // meaning the card is successfully drawn
                    break;
                }
                if(!isContains(takenIndex, rand)){
                    //only increase to tempt if takenIndex does not
                    //contain the rand. meaning rand is not yet being used for indexing the card
                    //to be drawn the card.
                    System.out.print(rand+ ", ");
                    takenIndex.add(rand);
                    tempt++;
                }
           }

            System.out.print(rand);

            if(status.equals(NOT_VALID)) {
                System.out.println("\n(GameServiceTest.java:207) No suitable card!\n"); //DEBUG
                //after a while, it might not find any card
                //withdraw from the game, and then take the card,
                // then find a next player to play
                gameService.withdraw(player);
                status = WITHDRAW;
            }
        }
        System.out.println();
        return status;
    }

    /**
     * This function looks through the list if the rand number is in the list
     * For this purpose of the test it is being used to look if we have ever added the
     * number (rand) to the list. if yes then set true, otherwise set to false.
     * @param takenIndex the list of numbers (represent of the index that the random has taken)
     * @param rand the random number 0 until
     * @return boolean
     */
    private static boolean isContains( List<Integer> takenIndex, int rand ) {
        for (Integer index : takenIndex) {
            if(index == rand){
                return true;
            }
        }
        return false;
    }

    /**
     * This is a test between a robot and  a player. with seeds
     * Assumption: if this test run smoothly,
     * then the game will do the round also without a problem
     */
    public void roundTestTwoPlayers(int seed) throws GameServiceException {
        //TODO before submission try to change the seeds accordingly!
        Random random = new Random(seed); //TODO set a seed before submission!
        GameService gameService = new GameService(random);

        final Player player = gameService.createPlayer(HUMAN,"Agha");
        final Player bot1 = gameService.createPlayer(BOT, "Bot1");
        System.out.println("roundTestTwoPlayers");

        //player plays first
        while(bot1.getCards().size() != 0 && player.getCards().size() != 0) {
            String status = null;

            Player encounterPlayerBefore = gameService.getEncounter().getCurrentPlayer();
            System.out.print(seed + ": player is playing: " + encounterPlayerBefore + " "); //DEBUG

            if(gameService.getEncounter().getCurrentPlayer().equals(player)){

                status = playerDraw(random, gameService, player);
                System.out.print(status + " "); // DEBUG

            } else if(gameService.getEncounter().getCurrentPlayer().equals(bot1)){
                if(bot1.getCards().get(0).equals(gameService.getConstants().WILDCARD)){
                    bot1.getCards().get(0).setColour(BLUE);
                }
                status = botPlay(bot1, gameService);
                System.out.print(status + " "); // DEBUG
            }

            Player encounterPlayerNow = gameService.getEncounter().getCurrentPlayer();

            //TODO check if the cards

            if(status != null && status.contains(REVERSE) && gameService.getDirection().equals(LEFT_WAY)){
                Player nowPlayer = gameService.getEncounter().getCurrentPlayer();
                assertEquals(nowPlayer,encounterPlayerNow);
                assertEquals(nowPlayer, encounterPlayerBefore);
            }

            System.out.println("player has: " + player.getCards().size() +
                                       " cards | bot1 has: " + bot1.getCards().size() + " cards |\n "); //DEBUG

        }

    }

    /**
     * This is a test between a robot and  a player.
     * Assumption: if this test run smoothly,
     * then the game will do the round also without a problem
     * </P>
     * Motivation: because there is a case where if we skip, the person that we skip is still the same person
     * that is why wee need to test for two players too.
     */
    @Test
    public void roundTestTwoPlayers() throws GameServiceException {
        //TODO before submission try to change the seeds accordingly!
        GameService gameService = new GameService(random);
        final Player player = gameService.createPlayer(HUMAN,"Agha");
        final Player bot1 = gameService.createPlayer(BOT, "Bot1");
        System.out.println("roundTestTwoPlayers");

        //player plays first
        while(bot1.getCards().size() != 0 && player.getCards().size() != 0) {
            String status = null;

            Player encounterPlayerBefore = gameService.getEncounter().getCurrentPlayer();
            System.out.print("player is playing: " + gameService.getEncounter().getCurrentPlayer() + " "); //DEBUG

            if(gameService.getEncounter().getCurrentPlayer().equals(player)){
                status = playerDraw(random, gameService, player);
                System.out.print(status + " "); // DEBUG
            } else if(gameService.getEncounter().getCurrentPlayer().equals(bot1)){
                status = botPlay(bot1, gameService);
                System.out.print(status + " "); // DEBUG
            }

            Player encounterPlayerNow = gameService.getEncounter().getCurrentPlayer();

            if(status != null && status.contains(REVERSE) && gameService.getDirection().equals(LEFT_WAY)){
                Player nowPlayer = gameService.getEncounter().getCurrentPlayer();
                assertEquals(nowPlayer, encounterPlayerBefore);
                assertEquals(nowPlayer,encounterPlayerNow);
            }

            System.out.println("player has: " + player.getCards().size() +
                                       " cards | bot1 has: " + bot1.getCards().size() + " cards |\n "); //DEBUG

        }

    }

    @Test
    public void roundTestTwoPlayersSeed() throws GameServiceException, IOException, InterruptedException {
        int i = 0;
        while (i < 1000){
            i++;
            roundTestTwoPlayers(i);
            // this lines of code down below solved a problem of:
            // Cannot invoke "de.uniks.pmws2223.uno.model.Colour.getName()" because the return value of "de.uniks.pmws2223.uno.model.Card.getColour()" is null

            /*
                Hypotese: why there are wierd null and another object come out of the terminal:
                memory leak: chat GPT
                To solve memory leaks in Java, you can follow these steps:

                Identify the source of the leak: Use a memory profiler tool to identify which objects or data structures are consuming excessive amounts of memory.
                Review your code: Once you have identified the source of the leak, review your code to identify the root cause. Common causes of memory leaks include:
                Creating objects unnecessarily: Avoid creating objects that are not needed or that could be reused instead of creating new ones.
                Holding onto references: Avoid holding onto references to objects that are no longer needed. Use weak references or null out references when they are no longer needed to allow the garbage collector to reclaim the memory.
                Incorrect use of data structures: Some data structures, such as lists, can cause memory leaks if not used correctly. Make sure you understand how data structures work and use them appropriately.
                Release resources: If your code uses resources such as file handles or database connections, make sure you release them properly when they are no longer needed. Use try-with-resources statements or finally blocks to ensure that resources are always released, even if an exception occurs.
                Use the garbage collector: Java has a garbage collector that automatically frees memory that is no longer needed. Make sure your code is written to allow the garbage collector to do its job properly. Avoid holding onto references to objects unnecessarily or using object pooling unnecessarily.
                Test and monitor: After making changes to your code, test it thoroughly to ensure that the memory leak has been fixed. Also, monitor the application over time to ensure that memory usage remains stable and does not continue to grow.
                By following these steps, you can identify and solve memory leaks in your Java application.
             */
            System.gc();
            Thread.sleep(100);
            Runtime.getRuntime().exec("clear");
        }
    }

}
