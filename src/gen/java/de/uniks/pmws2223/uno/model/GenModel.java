package de.uniks.pmws2223.uno.model;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;

import java.util.List;

@SuppressWarnings("unused")
public class GenModel implements ClassModelDecorator {
    public class Player {
        String name;
        int cardsTotal;
        @Link("player")
        List<Card> cards;
        @Link("currentPlayer")
        DrawPile drawPile;
    }

    public class Card {
        String name;
        String colour;
        int Number;
        @Link("cards")
        Player player;
    }

    public class DrawPile {
        Card currentCard;
        @Link("drawPile")
        Player currentPlayer;
    }
    @Override
    public void decorate(ClassModelManager mm) {
        mm.haveNestedClasses(GenModel.class);
    }
}
