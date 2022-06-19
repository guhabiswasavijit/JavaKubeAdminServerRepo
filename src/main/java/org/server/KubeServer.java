package org.server;
import java.io.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import lombok.extern.slf4j.Slf4j;

import java.net.*;
import java.nio.charset.StandardCharsets;
@Slf4j
public class KubeServer {
	
	private static int BOUND = 10;
	private static int CORE_POOL_SIZE = 10;
	private static int MAX_POOL_SIZE = 10;
	private static int THREAD_WAITING_TIME = 100;
	private static BlockingQueue<Runnable> queue = null;
	private static ExecutorService service = null;
	static {
		 queue = new ArrayBlockingQueue<>(BOUND);
		 service = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,THREAD_WAITING_TIME,TimeUnit.MILLISECONDS,queue,KubeThreadFactory.getInstance(),new GrowPolicy());
	}
	
	public static void main(String[] args) throws IOException {
		int port = Integer.parseInt(args[0]);
		try(ServerSocket serverSocket = new ServerSocket(port)){
			KubeServer server = new KubeServer();
			server.start(serverSocket);
		}
	}
	public void start(ServerSocket serverSocket) {
		while (true)
		{
			try(Socket socket = serverSocket.accept()){
				InputStream jsonCmd = socket.getInputStream();
				String clientCmd = IOUtils.toString(jsonCmd, StandardCharsets.UTF_8);
				log.info("Accepted client commmand {}",clientCmd);
				final JSONObject jsonClientCommand = new JSONObject(clientCmd);
				CommandType cmdType = jsonClientCommand.getEnum(CommandType.class, jsonClientCommand.getString(KubeConstants.COMMAND_TYPE));
				CommandFactory factory = CommandFactory.getInstance();
				NodeCommand nodeCmd = (NodeCommand)factory.getCommand(cmdType);
				DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
				Runnable handler = new ClientHandler(nodeCmd.constructAndGetCommand(jsonClientCommand),outputStream);
				service.submit(handler);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
	}
 	class ClientHandler implements Runnable
	{
 		 private final ThreadLocal<String> RUN_DATE = new ThreadLocal<String>(){
            protected String initialValue(){
            	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
            	return simpleDateFormat.format(new Date());
            }
         };
		final DataOutputStream dataOutPutStream;
		final String cmdString;
		public ClientHandler(String inputCommand,DataOutputStream t_dataOutPutStream){
			this.dataOutPutStream = t_dataOutPutStream;
			this.cmdString = inputCommand;
		}

		@Override
		public void run(){
           log.info("running command {} on date {}",this.cmdString,RUN_DATE);
		}
	}


}
