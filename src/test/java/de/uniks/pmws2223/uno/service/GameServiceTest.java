package de.uniks.pmws2223.uno.service;


import de.uniks.pmws2223.uno.model.Card;
import de.uniks.pmws2223.uno.model.Encounter;
import de.uniks.pmws2223.uno.model.Player;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static de.uniks.pmws2223.uno.Constants.*;
import static de.uniks.pmws2223.uno.service.BotService.botDrawCard;
import static org.junit.Assert.assertEquals;

public class GameServiceTest {

    private Random random = new Random(100);
    @Test
    public void CreatePlayer(){
        /*
         * before we start every player needs
         * randomized cards
         */
        System.out.println("Create Player Test");
        GameService gameService = new GameService();
//        GameService.reset_ALL_CARD(); // because of the sharing memory, we need to set all Null
        Player player = gameService.createPlayer(HUMAN, "Agha");
        assertEquals(7,player.getCards().size());

        Player bot = gameService.createPlayer(BOT, "Bot1");
        assertEquals(7, bot.getCards().size());


    }
    /**
     * this is a simple test
     * if a player gives a card, test the total of the card the player has
     * will the bot also be able to play?
     *
     * VARIABLE INITIALIZE FAILURE : Random should be defined globally
     * to avoid a difference of the result
     */
    @Test
    public void drawCard() throws GameServiceException {

        System.out.println("Draw Card Test");
        GameService gameService = new GameService(random);
        Encounter drawPile = gameService.getEncounter().setCurrentCard(gameService.getConstants().DRAW_TWO_BLUE);

        final Player player = gameService.createPlayer(HUMAN, "Agha");
        final Player bot1 = gameService.createPlayer(BOT, "Bot1");
        final Player bot2 = gameService.createPlayer(BOT, "Bot2");
        final Player bot3 = gameService.createPlayer(BOT, "Bot3");

        String value;
        System.out.println("Card on deck: " + gameService.getEncounter().getCurrentCard());
        assertEquals(DRAW_TWO,gameService.getEncounter().getCurrentCard().getName());

        value = gameService.drawCard(player.getCards().get(5), player);

        if(!value.equals(NOT_VALID)) {
            assertEquals(6, player.getCards().size());
            assertEquals(DRAW_TWO + "," + gameService.getConstants().DRAW_TWO_YELLOW.getColour(), value);
            assertEquals(9, bot1.getCards().size());

            value = botDrawCard(bot1, gameService);
            assertEquals(6,player.getCards().size());
            assertEquals(DRAW_TWO+","+ gameService.getConstants().DRAW_TWO_BLUE.getColour(),value);
            assertEquals(8,bot1.getCards().size());

            value = botDrawCard(bot2, gameService);
            assertEquals(6,player.getCards().size());
            assertEquals(gameService.getConstants().BLUE_8+","+ gameService.getConstants().BLUE_8.getColour(),value);
            assertEquals(7,bot3.getCards().size());
        } else {
            gameService.withdraw(player);

            value = botDrawCard(bot1, gameService);
            assertEquals(8,player.getCards().size());
            assertEquals(REVERSE+","+ gameService.getConstants().REVERSE_BLUE.getColour(),value);
            assertEquals(6,bot1.getCards().size());

        }

//        GameService.reset_ALL_CARD();
    }

    //TODO PROBLEM! WITH playerDraw! Please make sure to set the drawPlayer not with recursion!
    /**
     * This is a test between robot and player.
     * Assumption: if this test run smoothly,
     * then the game will do the round also without a problem
     */
    @Test
    public void roundTest() throws GameServiceException {
        System.out.println("roundTest");
//        Random random = new Random(234243); //TODO set a seed before submission!, make sure you are ok with this seed!
        GameService gameService = new GameService(random);
//        GameService.reset_ALL_CARD();

        final Player player = gameService.createPlayer(HUMAN,"Agha");
        final Player bot1 = gameService.createPlayer(BOT, "Bot1");
        final Player bot2 = gameService.createPlayer(BOT, "Bot2");
        final Player bot3 = gameService.createPlayer(BOT, "Bot3");

        int trialCount = 0;
        //player plays first
        while(bot1.getCards().size() != 0 && bot2.getCards().size() != 0 && bot3.getCards().size() != 0 && player.getCards().size() != 0) {
            String status = null;

            Player encounterPlayerBefore = gameService.getEncounter().getCurrentPlayer();
            System.out.println("player is playing: " + gameService.getEncounter().getCurrentPlayer());

            if(gameService.getEncounter().getCurrentPlayer().equals(player)){
                status = playerDraw(random, gameService, player);
            } else if(gameService.getEncounter().getCurrentPlayer().equals(bot1)){
                if(bot1.getCards().get(0).equals(gameService.getConstants().WILDCARD)){
//                    System.out.println(WILDCARD); //DEBUG
                    bot1.getCards().get(0).setColour(BLUE);
                }
                status = botDrawCard(bot1,gameService);
            } else if(gameService.getEncounter().getCurrentPlayer().equals(bot2)){
                if(bot2.getCards().get(0).equals(gameService.getConstants().WILDCARD)){
//                    System.out.println(WILDCARD); //DEBUG
                    bot2.getCards().get(0).setColour(BLUE);
                }
                status = botDrawCard(bot2,gameService);
            } else if(gameService.getEncounter().getCurrentPlayer().equals(bot3)) {
                if(bot3.getCards().get(0).equals(gameService.getConstants().WILDCARD)){
//                    System.out.println(WILDCARD); //DEBUG
                    bot3.getCards().get(0).setColour(BLUE);
                }
                status = botDrawCard(bot3,gameService);
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

            trialCount++;
//            assert trialCount < 100 : "TIMED OUT";
        }

    }


    private static String playerDraw( Random random, GameService gameService, Player player ) throws GameServiceException {
        //TODO FOR THESE SUBMISSION, PLEASE THINK DO NOT USE RECURSION!
        // BETTER USE A SPECIFIC INDEX WHO LOOKS THROUGH THE CARD!
        String status;
        Card drawnCard;
        int maxSized = player.getCards().size();
        List<Integer> takenIndex = new ArrayList<>();

        int index = random.nextInt(maxSized);
        takenIndex.add(index);
        if(player.getCards().get(index).equals(gameService.getConstants().WILDCARD)){
            int indexW = random.nextInt(COLOURS.length);
//                    System.out.println(WILDCARD); //DEBUG
            player.getCards().get(0).setColour(COLOURS[indexW]);
        }
        drawnCard= player.getCards().get(index);
        status = gameService.drawCard(drawnCard, player); //HERE PLAYER DRAW THE CARD
        if(status.equals(NOT_VALID)){
            System.out.println("\nCard on deck:" + gameService.getEncounter().getCurrentCard() + ", " +
                                       gameService.getEncounter().getCurrentCard().getColour()
                                       + "\nPlayer's Card: " + player.getCards() + "\nTaken Index: ");
            int tempt = 1;
            // FAILURE? Assumption
            /*
            The random value
             */
            int rand = index;
            System.out.print(rand+ ", ");
            while(tempt < maxSized){
                rand = random.nextInt(maxSized);
                drawnCard= player.getCards().get(rand);
                status = gameService.drawCard(drawnCard, player); //HERE PLAYER DRAW THE CARD
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
                System.out.println("\n(GameServiceTest.java:207) No suitable card!\n");
                //after the while, it might not find any card
                //withdraw from the game, and then take the card,
                // then find a next player to play
                gameService.withdraw(player);
                status = WITHDRAW;
            }
        }
        System.out.println();
        return status;
    }

    private static boolean isContains( List<Integer> takenIndex, int rand ) {
        for (Integer index : takenIndex) {
            if(index == rand){
                return true;
            }
        }
        return false;
    }


    public void roundTestTwoPlayers(int seed) throws GameServiceException {
        //TODO before submission try to change the seeds accordingly!
        Random random = new Random(seed); //TODO set a seed before submission!
        GameService gameService = new GameService(random);
//        GameService.reset_ALL_CARD();
        final Player player = gameService.createPlayer(HUMAN,"Agha");
        final Player bot1 = gameService.createPlayer(BOT, "Bot1");
        System.out.println("roundTestTwoPlayers");

        int trialCount = 0;
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
                status = botDrawCard(bot1,gameService);
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

            trialCount++;
//            assert trialCount < 150 : "TIMED OUT";
        }

    }

    @Test
    public void roundTestTwoPlayers() throws GameServiceException {
        //TODO before submission try to change the seeds accordingly!
//        Random random = new Random(49); //TODO set a seed before submission!
        GameService gameService = new GameService(random);
//        GameService.reset_ALL_CARD();
        final Player player = gameService.createPlayer(HUMAN,"Agha");
        final Player bot1 = gameService.createPlayer(BOT, "Bot1");
        System.out.println("roundTestTwoPlayers");

        int trialCount = 0;
        //player plays first
        Card drawnCard = null;
        while(bot1.getCards().size() != 0 && player.getCards().size() != 0) {
            String status = null;

            Player encounterPlayerBefore = gameService.getEncounter().getCurrentPlayer();
            System.out.print("player is playing: " + gameService.getEncounter().getCurrentPlayer() + " "); //DEBUG

            if(gameService.getEncounter().getCurrentPlayer().equals(player)){

                status = playerDraw(random, gameService, player);

                System.out.print(status + " "); // DEBUG

            } else if(gameService.getEncounter().getCurrentPlayer().equals(bot1)){
                if(bot1.getCards().get(0).equals(gameService.getConstants().WILDCARD)){
                    bot1.getCards().get(0).setColour(BLUE);
                }
                status = botDrawCard(bot1,gameService);
                System.out.print(status + " "); // DEBUG
            }

            Player encounterPlayerNow = gameService.getEncounter().getCurrentPlayer();

            if(player.getCards().contains(drawnCard)) {
                throw new GameServiceException("The card is not being removed :" + player.getCards() + ", " + drawnCard);
            }

            if(status != null && status.contains(REVERSE) && gameService.getDirection().equals(LEFT_WAY)){
                Player nowPlayer = gameService.getEncounter().getCurrentPlayer();
                assertEquals(nowPlayer, encounterPlayerBefore);
                assertEquals(nowPlayer,encounterPlayerNow);
            }

            System.out.println("player has: " + player.getCards().size() +
                                       " cards | bot1 has: " + bot1.getCards().size() + " cards |\n "); //DEBUG

            trialCount++;
//            assert trialCount < 150 : "TIMED OUT";
        }

    }

    @Test
    public void roundTestTwoPlayersSeed() throws GameServiceException, IOException {
        int i = 0;
        while (i < 1000){
//            GameService.reset_ALL_CARD();
            i++;
            //TODO assert only if there is element that is still there!
            roundTestTwoPlayers(i);
//            GameService.reset_ALL_CARD();
            Runtime.getRuntime().exec("clear");
        }
    }

}
