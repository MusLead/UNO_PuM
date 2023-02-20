package de.uniks.pmws2223.uno.service;

import de.uniks.pmws2223.uno.model.Player;
import org.junit.Test;

import java.util.Random;

import static de.uniks.pmws2223.uno.Constants.*;
import static de.uniks.pmws2223.uno.Constants.REVERSE;
import static de.uniks.pmws2223.uno.service.BotService.botPlay;
import static org.junit.Assert.assertEquals;

public class BotServiceTest {

    /**
     * This is a test between robot.
     * Assumption: if this test run smoothly,
     * then the robot will do the round also without a problem
     */
    @Test
    public void roundBotTest() throws GameServiceException {
        System.out.println("roundBotTest");
        Random random = new Random(242342);
        GameService gameService = new GameService(random);


        final Player bot1 = gameService.createPlayer(BOT, "Bot1");
        final Player bot2 = gameService.createPlayer(BOT, "Bot2");
        final Player bot3 = gameService.createPlayer(BOT, "Bot3");

        //Bot1 plays first
        while(bot1.getCards().size() != 0 && bot2.getCards().size() != 0 && bot3.getCards().size() != 0) {
            String status = null;

            Player encounterPlayerBefore = gameService.getEncounter().getCurrentPlayer();
            System.out.println("Your turn: " + encounterPlayerBefore); //DEBUG

            if(gameService.getEncounter().getCurrentPlayer().equals(bot1)){
                status = botPlay(bot1, gameService);
            } else if(gameService.getEncounter().getCurrentPlayer().equals(bot2)){
                status = botPlay(bot2, gameService);
            } else if(gameService.getEncounter().getCurrentPlayer().equals(bot3)) {
                status = botPlay(bot3, gameService);
            }
            System.out.println("status: "+status);
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
            } else if(gameService.getDirection().equals(RIGHT_WAY) && !gameService.getEncounter().getCurrentCard().getName().equals(SKIP)){
                Player nowPlayer = gameService.getEncounter().getCurrentPlayer();
                Player expectedBeforePlayer = null;
                int indexMax = gameService.getEncounter().getPlayers().size();
                for (int i = 0; i < indexMax; i++) {
                    Player iteratePlayer = gameService.getEncounter().getPlayers().get(i);
                    if(iteratePlayer.equals(nowPlayer)){
                        int nextIndex = (i - 1) < 0 ? indexMax - 1 : (i - 1);
                        expectedBeforePlayer = gameService.getEncounter().getPlayers().get(nextIndex);
                    }
                }
                assertEquals(expectedBeforePlayer,encounterPlayerBefore);
                assertEquals(nowPlayer,encounterPlayerNow);
            }

/*
               // System.out.println("bot1 has: " + bot1.getCards().size() + " cards | " +
               //         "bot2 has: " + bot2.getCards().size() + " cards | " +
               //         "bot3 has: " + bot3.getCards().size() + " cards "); //DEBUG
*/
        }

    }
}
