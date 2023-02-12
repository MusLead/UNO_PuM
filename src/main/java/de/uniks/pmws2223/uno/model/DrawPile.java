package de.uniks.pmws2223.uno.model;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class DrawPile
{
   public static final String PROPERTY_CURRENT_CARD = "currentCard";
   public static final String PROPERTY_CURRENT_PLAYER = "currentPlayer";
   private Card currentCard;
   private Player currentPlayer;
   protected PropertyChangeSupport listeners;

   public Card getCurrentCard()
   {
      return this.currentCard;
   }

   public DrawPile setCurrentCard(Card value)
   {
      if (Objects.equals(value, this.currentCard))
      {
         return this;
      }

      final Card oldValue = this.currentCard;
      this.currentCard = value;
      this.firePropertyChange(PROPERTY_CURRENT_CARD, oldValue, value);
      return this;
   }

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
         oldValue.setDrawPile(null);
      }
      this.currentPlayer = value;
      if (value != null)
      {
         value.setDrawPile(this);
      }
      this.firePropertyChange(PROPERTY_CURRENT_PLAYER, oldValue, value);
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
      this.setCurrentPlayer(null);
   }
}
