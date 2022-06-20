package org.server;

public enum CommandType {
   NODE("NODE"),
   POD("POD");
   
   public final String name;

   private CommandType(String name) {
       this.name = name;
   }
}
