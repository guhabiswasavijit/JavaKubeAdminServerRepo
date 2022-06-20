package org.server;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class NodeCommand extends Command {

	public String constructAndGetCommand(JSONObject jsonClientCommand) throws IOException {
		StringBuilder commandBuilder =  new StringBuilder();
		final JSONObject clientCmd = (JSONObject) super.constructAndGetCommand(jsonClientCommand);
		commandBuilder.append(clientCmd.getString(KubeConstants.CLUSTER_ADMIN_COMMAND));
		try {
			String nodeName = clientCmd.getString(KubeConstants.NODE_NAME);
			commandBuilder.append(KubeConstants.EMPTY_STRING);
			commandBuilder.append(nodeName);
		}
		catch(JSONException ex) {
			log.error("node name not found {}",ex);
		}
		log.info("Accepted node command {}",commandBuilder.toString());
		return commandBuilder.toString();
	}

}
