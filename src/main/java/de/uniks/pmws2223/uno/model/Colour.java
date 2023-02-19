package de.uniks.pmws2223.uno.model;
import java.util.Objects;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Collection;

public class Colour
{
   public static final String PROPERTY_NAME = "name";
   public static final String PROPERTY_CARDS = "cards";
   private String name;
   protected PropertyChangeSupport listeners;
   private List<Card> cards;

   public String getName()
   {
      return this.name;
   }

   public Colour setName(String value)
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

   public List<Card> getCards()
   {
      return this.cards != null ? Collections.unmodifiableList(this.cards) : Collections.emptyList();
   }

   public Colour withCards(Card value)
   {
      if (this.cards == null)
      {
         this.cards = new ArrayList<>();
      }
      if (!this.cards.contains(value))
      {
         this.cards.add(value);
         value.setColour(this);
         this.firePropertyChange(PROPERTY_CARDS, null, value);
      }
      return this;
   }

   public Colour withCards(Card... value)
   {
      for (final Card item : value)
      {
         this.withCards(item);
      }
      return this;
   }

   public Colour withCards(Collection<? extends Card> value)
   {
      for (final Card item : value)
      {
         this.withCards(item);
      }
      return this;
   }

   public Colour withoutCards(Card value)
   {
      if (this.cards != null && this.cards.remove(value))
      {
         value.setColour(null);
         this.firePropertyChange(PROPERTY_CARDS, value, null);
      }
      return this;
   }

   public Colour withoutCards(Card... value)
   {
      for (final Card item : value)
      {
         this.withoutCards(item);
      }
      return this;
   }

   public Colour withoutCards(Collection<? extends Card> value)
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
      this.withoutCards(new ArrayList<>(this.getCards()));
   }
}
