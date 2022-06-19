package org.server;

import java.io.IOException;
import org.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class NodeCommand extends Command {

	public String constructAndGetCommand(JSONObject jsonClientCommand) throws IOException {
		StringBuilder commandBuilder =  new StringBuilder();
		final JSONObject clientCmd = (JSONObject) super.constructAndGetCommand(jsonClientCommand);
		commandBuilder.append(clientCmd.getString(KubeConstants.CLUSTER_ADMIN_COMMAND));
		commandBuilder.append(KubeConstants.EMPTY_STRING);
		commandBuilder.append(clientCmd.getString(KubeConstants.NODE_NAME));
		log.info("Accepted client command {}",clientCmd);
		return commandBuilder.toString();
	}

}
