package de.uniks.pmws2223.uno.service;

import de.uniks.pmws2223.uno.model.Player;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static de.uniks.pmws2223.uno.Constants.*;
import static de.uniks.pmws2223.uno.Constants.REVERSE;
import static de.uniks.pmws2223.uno.service.BotService.botDrawCard;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicBoolean;

public class BotServiceTest {

    @Test
    public void roundBotTest() throws GameServiceException {
        /*
         * this is a test between robot.
         * Assumption: if this test run smoothly,
         * then the robot will do the round also without a problem
         */
        System.out.println("roundBotTest");
        Random random = new Random(234243); //TODO set a seed before submission!
//        GameService.reset_ALL_CARD();
        GameService gameService = new GameService(random);


        final Player bot1 = gameService.createPlayer(BOT, "Bot1");
        final Player bot2 = gameService.createPlayer(BOT, "Bot2");
        final Player bot3 = gameService.createPlayer(BOT, "Bot3");

        //Bot1 plays first
        while(bot1.getCards().size() != 0 && bot2.getCards().size() != 0 && bot3.getCards().size() != 0) {
            String status = null;

            Player encounterPlayerBefore = gameService.getEncounter().getCurrentPlayer();

            if(gameService.getEncounter().getCurrentPlayer().equals(bot1)){
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

//            if(status.contains(REVERSE)){
//                assertEquals(bot2,encounterPlayerBefore);
//                assertEquals(bot1,encounterPlayerNow);
//            }

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

//                System.out.println("bot1 has: " + bot1.getCards().size() + " cards | " +
//                        "bot2 has: " + bot2.getCards().size() + " cards | " +
//                        "bot3 has: " + bot3.getCards().size() + " cards "); //DEBUG
        }

    }

    @Test
    public void roundBotTestround() throws GameServiceException {
        for (int i = 0; i < 1000; i++) {
            roundBotTest();
        }
    }
}
