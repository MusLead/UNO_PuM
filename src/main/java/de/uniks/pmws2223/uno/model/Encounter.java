package de.uniks.pmws2223.uno.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;
import java.beans.PropertyChangeSupport;

public class Encounter
{
   public static final String PROPERTY_PLAYERS = "players";
   public static final String PROPERTY_CURRENT_PLAYER = "currentPlayer";
   public static final String PROPERTY_CURRENT_CARD = "currentCard";
   private List<Player> players;
   protected PropertyChangeSupport listeners;
   private Player currentPlayer;
   private Card currentCard;

   public List<Player> getPlayers()
   {
      return this.players != null ? Collections.unmodifiableList(this.players) : Collections.emptyList();
   }

   public Encounter withPlayers(Player value)
   {
      if (this.players == null)
      {
         this.players = new ArrayList<>();
      }
      if (!this.players.contains(value))
      {
         this.players.add(value);
         value.setEncounter(this);
         this.firePropertyChange(PROPERTY_PLAYERS, null, value);
      }
      return this;
   }

   public Encounter withPlayers(Player... value)
   {
      for (final Player item : value)
      {
         this.withPlayers(item);
      }
      return this;
   }

   public Encounter withPlayers(Collection<? extends Player> value)
   {
      for (final Player item : value)
      {
         this.withPlayers(item);
      }
      return this;
   }

   public Encounter withoutPlayers(Player value)
   {
      if (this.players != null && this.players.remove(value))
      {
         value.setEncounter(null);
         this.firePropertyChange(PROPERTY_PLAYERS, value, null);
      }
      return this;
   }

   public Encounter withoutPlayers(Player... value)
   {
      for (final Player item : value)
      {
         this.withoutPlayers(item);
      }
      return this;
   }

   public Encounter withoutPlayers(Collection<? extends Player> value)
   {
      for (final Player item : value)
      {
         this.withoutPlayers(item);
      }
      return this;
   }

   public Player getCurrentPlayer()
   {
      return this.currentPlayer;
   }

   public Encounter setCurrentPlayer(Player value)
   {
      if (this.currentPlayer == value)
      {
         return this;
      }

      final Player oldValue = this.currentPlayer;
      if (this.currentPlayer != null)
      {
         this.currentPlayer = null;
         oldValue.setCurrentDiscardPile(null);
      }
      this.currentPlayer = value;
      if (value != null)
      {
         value.setCurrentDiscardPile(this);
      }
      this.firePropertyChange(PROPERTY_CURRENT_PLAYER, oldValue, value);
      return this;
   }

   public Card getCurrentCard()
   {
      return this.currentCard;
   }

   public Encounter setCurrentCard(Card value)
   {
      if (this.currentCard == value)
      {
         return this;
      }

      final Card oldValue = this.currentCard;
      if (this.currentCard != null)
      {
         this.currentCard = null;
         oldValue.setCurrentDiscardPile(null);
      }
      this.currentCard = value;
      if (value != null)
      {
         value.setCurrentDiscardPile(this);
      }
      this.firePropertyChange(PROPERTY_CURRENT_CARD, oldValue, value);
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

   public void removeYou()
   {
      this.setCurrentCard(null);
      this.setCurrentPlayer(null);
      this.withoutPlayers(new ArrayList<>(this.getPlayers()));
   }
}
