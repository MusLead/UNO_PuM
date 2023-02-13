package de.uniks.pmws2223.uno.model;

import org.fulib.builder.ClassModelDecorator;
import org.fulib.builder.ClassModelManager;
import org.fulib.builder.reflect.Link;

import java.util.List;

@SuppressWarnings("unused")
public class GenModel implements ClassModelDecorator {

    public class Player {
        String name;
        @Link()
        TypePlayer typePlayer;
        List<Card> cards;
        @Link("currentPlayer")
        DrawPile currentDrawPile;
        @Link("players")
        DrawPile drawPile;

    }

    public class Card {
        String name;
        @Link()
        Colour colour;
        int Number;
    }

    public class DrawPile {
        @Link()
        Card currentCard;
        @Link("currentDrawPile")
        Player currentPlayer;
        @Link("drawPile")
        List<Player> players;
    }

    public class Colour {
        String name;
    }

    public class TypePlayer {
        String type;
    }

    @Override
    public void decorate(ClassModelManager mm) {
        mm.haveNestedClasses(GenModel.class);
    }
}
