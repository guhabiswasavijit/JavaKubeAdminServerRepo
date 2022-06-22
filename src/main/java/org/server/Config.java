package org.server;

import java.util.ResourceBundle;

public class Config {
    private static ResourceBundle props = null;
    
    static {
    	props = ResourceBundle.getBundle("application");
    }
	public static String getProcessRunDirectoty(){
		return props.getString("processRunDirectory");
	}

}
