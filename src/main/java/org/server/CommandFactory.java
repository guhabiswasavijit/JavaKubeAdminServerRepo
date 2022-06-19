package org.server;

public class CommandFactory {
	private static CommandFactory factory = null;
	static {
		factory = new CommandFactory();
	}
    public static final CommandFactory getInstance() {
    	return factory;
    }
    public final Command getCommand(CommandType cmdType) {
    	if(cmdType.name.equals(CommandType.NODE.name)) {
    		return new NodeCommand();
    	}
    	return null;
    }
    
    
}
