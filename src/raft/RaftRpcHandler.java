package raft;


/**
 * provide uniform access to rpc
 * @author volt
 *
 */
public class RaftRpcHandler {

	public RaftCall getHandler(String serverName){
		RaftCall ret =  null;
		return ret;
	}
	
	private static RaftRpcHandler ins = null;
	
	private RaftRpcHandler(){
		
	}
	
	public static RaftRpcHandler getInstance(){
		
		return ins;
	}
	
}
