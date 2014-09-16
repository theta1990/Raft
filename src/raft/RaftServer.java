package raft;

import java.util.Timer;

public class RaftServer {

	private RaftRpcServer m_rpcServer;
	
	private int m_term;
	private String voteFor;
	
	private String m_name;
	private State state;
	
	public RaftServer() throws Exception{
		
		m_term = 0;
		
		m_rpcServer = new RaftRpcServer(m_name) {
			
			@Override
			public boolean vote(String serName, int curTerm) {

				boolean ret = false;
				switch(state){
				case follower:
					
					if(m_term < curTerm){
						//acquire the lock of this acceptor
						m_term = curTerm;
						voteFor = serName;			
						ret = true;
					}else {
						//An proposer with a bigger term has locked the acceptor.
						ret = false;
					}
					
					break;
				case candidate:
					
					ret = false;
					break;
				case leader:
					
					
					break;
				}
				
				
				
				return ret;
			}
			
			@Override
			public String append() {
				
				
				return null;
			}
		};
		
	}
	
	public void election(){
		m_term++;
	}
	
	public static void main(){
		
	}
	
}

enum State{
	follower,
	candidate,
	leader
};

class ServerState{
	
	State m_state;
	Timer timer;
	ServerState(){
		m_state = State.follower;

		switch(m_state){
		case follower:
			break;
			
		case candidate:
			break;
			
		case leader:
			break;
		}
	}
	
	
	
}
