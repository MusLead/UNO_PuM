package de.uniks.pmws2223.uno;

import de.uniks.pmws2223.uno.model.DrawPile;
import de.uniks.pmws2223.uno.model.Player;
import de.uniks.pmws2223.uno.service.BotService;
import de.uniks.pmws2223.uno.service.GameService;
import de.uniks.pmws2223.uno.service.GameServiceException;
import org.junit.Test;

import java.util.Random;

import static de.uniks.pmws2223.uno.Constants.*;
import static org.junit.Assert.*;

public class GameServiceTest {
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
    @Test
    public void drawCard() throws GameServiceException {

        System.out.println("Draw Card Test");
        Random random = new Random(100);
        DrawPile drawPile = new DrawPile().setCurrentCard(DRAW_TWO_BLUE);
        GameService gameService = new GameService(random,drawPile);

        final Player player = gameService.createPlayer(HUMAN, "Agha");
        final Player bot1 = gameService.createPlayer(BOT, "Bot1");
        final Player bot2 = gameService.createPlayer(BOT, "Bot2");
        final Player bot3 = gameService.createPlayer(BOT, "Bot3");

        String value;
        value = gameService.drawCard(player.getCards().get(6), player);
        assertEquals(6,player.getCards().size());
        assertEquals(DRAW_TWO,value);
        assertEquals(9,bot1.getCards().size());

        value = BotService.botDrawCard(bot1.getCards().get(0), bot1, gameService);
        assertEquals(6,player.getCards().size());
        assertEquals(DRAW_TWO,value);
        assertEquals(8,bot1.getCards().size());

        value = BotService.botDrawCard(bot2.getCards().get(0), bot2, gameService);
        assertEquals(6,player.getCards().size());
        assertEquals(BLUE_9.getName(),value);
        assertEquals(7,bot3.getCards().size());
    }


}
