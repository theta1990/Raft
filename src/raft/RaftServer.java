package raft;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class RaftServer {

	private RaftRpcServer m_rpcServer;

	private int m_curTerm;
	private String m_voteFor;

	private String m_name;
	private State m_state;

	private long elapseTimeOut = 300;
	private long electionTimeOut = 500;
	private long heartbeatCycle = 100;

	private Timer m_followerTimer = null;
	private Timer m_candidateTimer = null;
	private Timer m_leaderTimer = null;

	Object syncflag;

	public RaftServer() throws Exception {

		m_curTerm = 0;
		changeToFollower();

		m_rpcServer = new RaftRpcServer(m_name) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1028592393300692110L;

			@Override
			public boolean vote(String serName, int term) {

				boolean ret = false;
				// synchronized (syncflag) {

				if (m_curTerm < term) {
					m_curTerm = term;
					m_voteFor = serName;
					changeToFollower();
					ret = true;
				} else if (m_curTerm == term) {

					if (null == m_voteFor || serName.equals(m_voteFor)) {
						m_voteFor = serName;
						ret = true;
					}
				}
				// }
				return ret;
			}

			@Override
			public String append() {

				heartbeat();
				return null;
			}
		};

	}

	public void election() {

		boolean enough = true;
		
		m_curTerm ++;
		m_voteFor = m_name;

		try {
			final ArrayList<String> serverNames = Configure.getConfig()
					.getServerNames();
			
			Callable<Boolean> callable  = new Callable<Boolean>() {
				
				@Override
				public Boolean call() throws Exception {
					return null;
				}
			};
			
			
			
			
			
			for(String server : serverNames){
				if( server.equals(m_name) )
					continue;
				
				new Runnable() {
					
					@Override
					public void run() {
						
						
					}
				}.run();
			}
			

			if (enough) {
				changeToLeader();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void heartbeat() {

		// restart the follower timer;

	}

	private void changeToFollower() {

		m_state = State.follower;
		m_followerTimer.schedule(new TimerTask() {

			@Override
			public void run() {

				// synchronized (syncflag) {
				changeToCandidate();
				// }
			}
		}, elapseTimeOut);
	}

	private void changeToCandidate() {

		m_state = State.candidate;

		if (m_followerTimer != null) {
			m_followerTimer.cancel();
			m_followerTimer = null;
		}
		election();

		m_candidateTimer = new Timer();
		m_candidateTimer.schedule(new TimerTask() {

			@Override
			public void run() {

				// synchronized (syncflag) {
				election();
				// }
			}
		}, electionTimeOut);

	}

	private void changeToLeader() {

		m_state = State.leader;

		m_leaderTimer = new Timer();
		m_leaderTimer.schedule(new TimerTask() {

			@Override
			public void run() {

				// synchronized (syncflag) {
				heartbeat();
				// }
			}
		}, 0, heartbeatCycle);
	}
}


class ServerState {

	State m_state;
	Timer timer;

	ServerState() {
		m_state = State.follower;

		switch (m_state) {
		case follower:
			break;

		case candidate:
			break;

		case leader:
			break;
		}
	}

}
