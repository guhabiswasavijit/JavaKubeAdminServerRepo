package org.server;

import java.io.IOException;
import org.json.JSONObject;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode
public abstract class Command {
	protected String clientId;
	protected String clusterAdminCommand;
	
	public Object constructAndGetCommand(JSONObject jsonClientCommand) throws IOException {
		this.clientId = jsonClientCommand.getString(KubeConstants.CLIENT_ID);
		log.info("Accepted client id {}",clientId);
		return jsonClientCommand;
	}

}
