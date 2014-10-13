package raft;

import java.util.ArrayList;

import akka.actor.ActorRef;

public class Configure {

	private ArrayList<String> serverNames;
	private ArrayList<Integer> serverList;
	private ArrayList<ActorRef> m_serverRef;
	private int serverNum;

	
	private static Configure ins = null;
	
	public static String configPath = "config";
	
	private Configure() throws Exception{
		
		serverList = new ArrayList<Integer>();
		m_serverRef = new ArrayList<ActorRef>();
		serverNum = 0;
//		BufferedReader br = new BufferedReader(new FileReader(configPath));
//		String line = null;
//		while( null != (line = br.readLine()) ){
//			serverNames.add(line);
//			serverList.add(Integer.parseInt(line));
//			serverNum ++;
//		}
//		br.close();
	}
	
	public void initConfig(String path){
		
		configPath = path;
	}
	
	public static Configure getConfig() throws Exception{
		if( null == ins ){
			ins = new Configure();
		}
		return ins;                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              
	}
	
	public final ArrayList<String> getServerNames(){
		
		return serverNames;
	}
	
	public final ArrayList<Integer> getServerList(){
		
		return serverList;
	}
	
	public final int getServerNum(){
		
		return serverNum;
	}
	
	public void addServer(int id){
		serverList.add(id);
		serverNum++;
	}

	public void add(ActorRef ref) {
		m_serverRef.add(ref);
	}

	public ArrayList<ActorRef> getServerRef() {
		
		++serverNum;
		return m_serverRef;
	}
}
