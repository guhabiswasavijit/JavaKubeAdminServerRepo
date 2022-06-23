package org.server;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import lombok.extern.slf4j.Slf4j;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
@Slf4j
public class KubeServer {
	
	private static int BOUND = 10;
	private static int CORE_POOL_SIZE = 10;
	private static int MAX_POOL_SIZE = 10;
	private static int THREAD_WAITING_TIME = 100;
	private static BlockingQueue<Runnable> queue = null;
	private static ExecutorService service = null;
	private static Semaphore mutex;
	private static CyclicBarrier barrier = new CyclicBarrier(2);
	static {
		 mutex = new Semaphore(BOUND);
		 queue = new ArrayBlockingQueue<>(BOUND);
		 service = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,THREAD_WAITING_TIME,TimeUnit.MILLISECONDS,queue,KubeThreadFactory.getInstance(),new GrowPolicy());
	}
	
	public static void main(String[] args){
		String portStr = args.length == 1?args[0]: System.getProperty("port");
		log.info("starting server at port {}",portStr);
		int port = Integer.parseInt(portStr.trim());
		log.info("starting server at port {}",port);
		try(ServerSocket serverSocket = new ServerSocket(port,BOUND,InetAddress.getLoopbackAddress())){
			KubeServer server = new KubeServer();
			if(serverSocket.isBound()) {
				server.start(serverSocket);
			}
		}
		catch(IOException|InterruptedException ex) {
			log.error("Unable to bound server at port {}", port);
			log.error("Exception while binding {}",ex);
		}
	}
	public void start(ServerSocket serverSocket) throws IOException,InterruptedException {
		while (true)
		{
			mutex.acquire();
			log.info("started server ...");
			try(Socket socket = serverSocket.accept()){
				InputStream jsonCmd = socket.getInputStream();
				String clientCmd = IOUtils.toString(jsonCmd, StandardCharsets.UTF_8).trim();
				log.info("Accepted client commmand {}",clientCmd);
				final JSONObject jsonClientCommand = new JSONObject(clientCmd);
				CommandType cmdType = CommandType.valueOf(jsonClientCommand.getString(KubeConstants.COMMAND_TYPE));
				CommandFactory factory = CommandFactory.getInstance();
				NodeCommand nodeCmd = (NodeCommand)factory.getCommand(cmdType);
				String prefix = "cmdOutput";
	            String suffix = "json";
	            File directoryPath = new File(Config.getProcessRunDirectoty());
	            File outputFile = File.createTempFile(prefix, suffix, directoryPath);
				Runnable handler = new ClientHandler(nodeCmd.constructAndGetCommand(jsonClientCommand),outputFile);
				service.submit(handler);
				barrier.await();
				DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
				log.info("Output file size {}",outputFile.length());
                FileReader reader = new FileReader(outputFile);
                char[] chars = new char[10*1024];
                int success = reader.read(chars);
                log.info("Successfully read commnad out file {}",success);
                String outputJson = new String(chars);
                log.info("Successfully read from file {}",outputJson);
                outputStream.writeBytes(outputJson);
                outputStream.flush();
                reader.close();
                log.info("About to delete process output file");
			    outputFile.delete();
			    log.info("Successfully deleted process output file");
			}
			catch (Exception ex){
				log.error("exception occured while running server {}",ex);
			}
			mutex.release();
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
		private final String cmdString;
		private final File outputFile;
		public ClientHandler(String inputCommand,File i_File){
			this.cmdString = inputCommand;
			this.outputFile = i_File;
		}

		@Override
		public void run(){
           log.info("running command {} on date {}",this.cmdString,RUN_DATE.get());
           try {
               Process process = Runtime.getRuntime().exec(String.format("cmd.exe /c %s",this.cmdString));
               StreamGobbler streamGobbler =  new StreamGobbler(process.getInputStream(),this.outputFile,process.getErrorStream());
               Executors.newSingleThreadExecutor().submit(streamGobbler);
               int exitCode = process.waitFor();
               log.info("process exited {}",exitCode);
               barrier.await();
           } catch (InterruptedException | IOException | BrokenBarrierException ex) {
               log.error("Exception occured while running process {}", ex);
           }
		}
	}


}
