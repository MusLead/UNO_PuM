package de.uniks.pmws2223.uno.model;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class DrawPile
{
   public static final String PROPERTY_CURRENT_PLAYER = "currentPlayer";
   public static final String PROPERTY_CURRENT_CARD = "currentCard";
   public static final String PROPERTY_PLAYERS = "players";
   private Player currentPlayer;
   protected PropertyChangeSupport listeners;
   private Card currentCard;
   private List<Player> players;

   public Player getCurrentPlayer()
   {
      return this.currentPlayer;
   }

   public DrawPile setCurrentPlayer(Player value)
   {
      if (this.currentPlayer == value)
      {
         return this;
      }

      final Player oldValue = this.currentPlayer;
      if (this.currentPlayer != null)
      {
         this.currentPlayer = null;
         oldValue.setCurrentDrawPile(null);
      }
      this.currentPlayer = value;
      if (value != null)
      {
         value.setCurrentDrawPile(this);
      }
      this.firePropertyChange(PROPERTY_CURRENT_PLAYER, oldValue, value);
      return this;
   }

   public Card getCurrentCard()
   {
      return this.currentCard;
   }

   public DrawPile setCurrentCard(Card value)
   {
      if (this.currentCard == value)
      {
         return this;
      }

      final Card oldValue = this.currentCard;
      this.currentCard = value;
      this.firePropertyChange(PROPERTY_CURRENT_CARD, oldValue, value);
      return this;
   }

   public List<Player> getPlayers()
   {
      return this.players != null ? Collections.unmodifiableList(this.players) : Collections.emptyList();
   }

   public DrawPile withPlayers(Player value)
   {
      if (this.players == null)
      {
         this.players = new ArrayList<>();
      }
      if (!this.players.contains(value))
      {
         this.players.add(value);
         value.setDrawPile(this);
         this.firePropertyChange(PROPERTY_PLAYERS, null, value);
      }
      return this;
   }

   public DrawPile withPlayers(Player... value)
   {
      for (final Player item : value)
      {
         this.withPlayers(item);
      }
      return this;
   }

   public DrawPile withPlayers(Collection<? extends Player> value)
   {
      for (final Player item : value)
      {
         this.withPlayers(item);
      }
      return this;
   }

   public DrawPile withoutPlayers(Player value)
   {
      if (this.players != null && this.players.remove(value))
      {
         value.setDrawPile(null);
         this.firePropertyChange(PROPERTY_PLAYERS, value, null);
      }
      return this;
   }

   public DrawPile withoutPlayers(Player... value)
   {
      for (final Player item : value)
      {
         this.withoutPlayers(item);
      }
      return this;
   }

   public DrawPile withoutPlayers(Collection<? extends Player> value)
   {
      for (final Player item : value)
      {
         this.withoutPlayers(item);
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

   public void removeYou()
   {
      this.setCurrentCard(null);
      this.setCurrentPlayer(null);
      this.withoutPlayers(new ArrayList<>(this.getPlayers()));
   }
}
