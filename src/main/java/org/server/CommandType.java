package org.server;

public enum CommandType {
   NODE("node"),
   POD("pod");
   
   public final String name;

   private CommandType(String name) {
       this.name = name;
   }
}
