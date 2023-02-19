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
   public static final String PROPERTY_ENCOUNTER = "encounter";
   public static final String PROPERTY_CARDS = "cards";
   public static final String PROPERTY_CURRENT_DISCARD_PILE = "currentDiscardPile";
   public static final String PROPERTY_TYPE_PLAYER = "typePlayer";
   private String name;
   protected PropertyChangeSupport listeners;
   private Encounter encounter;
   private List<Card> cards;
   private Encounter currentDiscardPile;
   private TypePlayer typePlayer;

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

   public Encounter getEncounter()
   {
      return this.encounter;
   }

   public Player setEncounter(Encounter value)
   {
      if (this.encounter == value)
      {
         return this;
      }

      final Encounter oldValue = this.encounter;
      if (this.encounter != null)
      {
         this.encounter = null;
         oldValue.withoutPlayers(this);
      }
      this.encounter = value;
      if (value != null)
      {
         value.withPlayers(this);
      }
      this.firePropertyChange(PROPERTY_ENCOUNTER, oldValue, value);
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

   public Encounter getCurrentDiscardPile()
   {
      return this.currentDiscardPile;
   }

   public Player setCurrentDiscardPile(Encounter value)
   {
      if (this.currentDiscardPile == value)
      {
         return this;
      }

      final Encounter oldValue = this.currentDiscardPile;
      if (this.currentDiscardPile != null)
      {
         this.currentDiscardPile = null;
         oldValue.setCurrentPlayer(null);
      }
      this.currentDiscardPile = value;
      if (value != null)
      {
         value.setCurrentPlayer(this);
      }
      this.firePropertyChange(PROPERTY_CURRENT_DISCARD_PILE, oldValue, value);
      return this;
   }

   public TypePlayer getTypePlayer()
   {
      return this.typePlayer;
   }

   public Player setTypePlayer(TypePlayer value)
   {
      if (this.typePlayer == value)
      {
         return this;
      }

      final TypePlayer oldValue = this.typePlayer;
      this.typePlayer = value;
      this.firePropertyChange(PROPERTY_TYPE_PLAYER, oldValue, value);
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
      this.setCurrentDiscardPile(null);
      this.setEncounter(null);
      this.withoutCards(new ArrayList<>(this.getCards()));
      this.setTypePlayer(null);
   }
}
