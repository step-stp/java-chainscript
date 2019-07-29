package com.stratumn.chainscript;

/**
 * A process is a collection of maps (process instances).
 * A map is a collection of links that track the process' progress.
 */
public class Process
{
   private String name;
   private String state;

   /**
    * @param name
    * @param state
    */
   public Process(String name, String state)
   {
      super();
      this.name = name;
      this.state = state;
   }

   /**
    * @return the name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the state
    */
   public String getState()
   {
      return state;
   }

   /**
    * @param state the state to set
    */
   public void setState(String state)
   {
      this.state = state;
   }
}
