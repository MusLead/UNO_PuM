package de.uniks.pmws2223.uno.model;
import java.util.Objects;
import java.beans.PropertyChangeSupport;

public class Card
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_NUMBER = "Number";
   public static final String PROPERTY_PLAYER = "player";
   public static final String PROPERTY_COLOUR = "colour";
   private String name;
   private int Number;
   protected PropertyChangeSupport listeners;
   private Player player;
   private Colour colour;

   public String getName()
   {
      return this.name;
   }

   public Card setName(String value)
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

   public int getNumber()
   {
      return this.Number;
   }

   public Card setNumber(int value)
   {
      if (value == this.Number)
      {
         return this;
      }

      final int oldValue = this.Number;
      this.Number = value;
      this.firePropertyChange(PROPERTY_NUMBER, oldValue, value);
      return this;
   }

   public Player getPlayer()
   {
      return this.player;
   }

   public Card setPlayer(Player value)
   {
      if (this.player == value)
      {
         return this;
      }

      final Player oldValue = this.player;
      if (this.player != null)
      {
         this.player = null;
         oldValue.withoutCards(this);
      }
      this.player = value;
      if (value != null)
      {
         value.withCards(this);
      }
      this.firePropertyChange(PROPERTY_PLAYER, oldValue, value);
      return this;
   }

   public Colour getColour()
   {
      return this.colour;
   }

   public Card setColour(Colour value)
   {
      if (this.colour == value)
      {
         return this;
      }

      final Colour oldValue = this.colour;
      this.colour = value;
      this.firePropertyChange(PROPERTY_COLOUR, oldValue, value);
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
      this.setColour(null);
      this.setPlayer(null);
   }
}
