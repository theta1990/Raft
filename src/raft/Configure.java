package raft;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Configure {

	private ArrayList<String> serverNames;
	
	private int serverNum;

	
	private static Configure ins = null;
	
	public static String configPath = null;
	
	private Configure() throws Exception{
		
		BufferedReader br = new BufferedReader(new FileReader(configPath));
		String line = null;
		while( null != (line = br.readLine()) ){
			serverNames.add(line);
			serverNum ++;
		}
		br.close();
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
	
	public final int getServerNum(){
		return serverNum;
	}
}
