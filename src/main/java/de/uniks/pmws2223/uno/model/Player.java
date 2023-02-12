package de.uniks.pmws2223.uno.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Player
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_CARDS_TOTAL = "cardsTotal";
   public static final String PROPERTY_DRAW_PILE = "drawPile";
   public static final String PROPERTY_CARDS = "cards";
   private String name;
   private int cardsTotal;
   private DrawPile drawPile;
   private List<Card> cards;
   protected PropertyChangeSupport listeners;

   public String getName()
   {
      return this.name;
   }

   public Player setName(String value)
   {
      if (Objects.equals(value, this.name))
      {
         return this;
      }

      final String oldValue = this.name;
      this.name = value;
      this.firePropertyChange(PROPERTY_NAME, oldValue, value);
      return this;
   }

   public int getCardsTotal()
   {
      return this.cardsTotal;
   }

   public Player setCardsTotal(int value)
   {
      if (value == this.cardsTotal)
      {
         return this;
      }

      final int oldValue = this.cardsTotal;
      this.cardsTotal = value;
      this.firePropertyChange(PROPERTY_CARDS_TOTAL, oldValue, value);
      return this;
   }

   public DrawPile getDrawPile()
   {
      return this.drawPile;
   }

   public Player setDrawPile(DrawPile value)
   {
      if (this.drawPile == value)
      {
         return this;
      }

      final DrawPile oldValue = this.drawPile;
      if (this.drawPile != null)
      {
         this.drawPile = null;
         oldValue.setCurrentPlayer(null);
      }
      this.drawPile = value;
      if (value != null)
      {
         value.setCurrentPlayer(this);
      }
      this.firePropertyChange(PROPERTY_DRAW_PILE, oldValue, value);
      return this;
   }

   public List<Card> getCards()
   {
      return this.cards != null ? Collections.unmodifiableList(this.cards) : Collections.emptyList();
   }

   public Player withCards(Card value)
   {
      if (this.cards == null)
      {
         this.cards = new ArrayList<>();
      }
      if (!this.cards.contains(value))
      {
         this.cards.add(value);
         value.setPlayer(this);
         this.firePropertyChange(PROPERTY_CARDS, null, value);
      }
      return this;
   }

   public Player withCards(Card... value)
   {
      for (final Card item : value)
      {
         this.withCards(item);
      }
      return this;
   }

   public Player withCards(Collection<? extends Card> value)
   {
      for (final Card item : value)
      {
         this.withCards(item);
      }
      return this;
   }

   public Player withoutCards(Card value)
   {
      if (this.cards != null && this.cards.remove(value))
      {
         value.setPlayer(null);
         this.firePropertyChange(PROPERTY_CARDS, value, null);
      }
      return this;
   }

   public Player withoutCards(Card... value)
   {
      for (final Card item : value)
      {
         this.withoutCards(item);
      }
      return this;
   }

   public Player withoutCards(Collection<? extends Card> value)
   {
      for (final Card item : value)
      {
         this.withoutCards(item);
      }
      return this;
   }

   public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue)
   {
      if (this.listeners != null)
      {
         this.listeners.firePropertyChange(propertyName, oldValue, newValue);
         return true;
      }
      return false;
   }

   public PropertyChangeSupport listeners()
   {
      if (this.listeners == null)
      {
         this.listeners = new PropertyChangeSupport(this);
      }
      return this.listeners;
   }

   @Override
   public String toString()
   {
      final StringBuilder result = new StringBuilder();
      result.append(' ').append(this.getName());
      return result.substring(1);
   }

   public void removeYou()
   {
      this.setDrawPile(null);
      this.withoutCards(new ArrayList<>(this.getCards()));
   }
}
