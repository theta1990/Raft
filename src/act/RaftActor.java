package act;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;

import mes.ElectionMessage;
import mes.RaftMessage;
import mes.RaftVoteRequestMessage;
import raft.Configure;
import raft.State;
import akka.actor.ActorPath;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

public class RaftActor extends UntypedActor {

	private final String m_name;

	private int m_curTerm;
	private String m_voteFor;
	private State m_state;

	private long followerTimeOut = 300;
	private long candidateTimeOut = 500;
	private long heartbeatCycle = 100;

	private Timer m_followerTimer = null;
	private Timer m_candidateTimer = null;
	private Timer m_leaderTimer = null;

	public RaftActor(final String name) {
		super();

		this.m_name = name;
		this.m_curTerm = 0;
		this.m_voteFor = null;
		this.m_state = State.follower;

		m_followerTimer = new Timer();
		m_followerTimer.schedule(new TimerTask() {

			@Override
			public void run() {

				/**
				 * follower time out, run election phase now
				 */
				getSelf().tell(new ElectionMessage(1, m_curTerm), getSelf());
			}
		}, followerTimeOut);

	}

	public final String getName() {

		return m_name;
	}

	@Override
	public void onReceive(Object message) throws Exception {

		if (!(message instanceof RaftMessage))
			unhandled(message);

		if (message instanceof ElectionMessage) {	//begin election
			runElection();
		}else if( message instanceof RaftVoteRequestMessage ) {	//receive vote request 
			handleVote(((RaftVoteRequestMessage) message).getCurTime(), ((RaftVoteRequestMessage) message).getVoteFor());
		}

	}

	public void runElection() {

		m_curTerm++;
		m_voteFor = m_name;

		try {
			final ArrayList<String> serverNames = Configure.getConfig()
					.getServerNames();

			for (String server : serverNames) {
				if (server.equals(m_name))
					continue;
				getContext().actorSelection("/user/" + m_name).tell(
						new RaftVoteRequestMessage(1, m_curTerm, m_name), getSelf());
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 处理投票
	 * @param term
	 * @param voteName
	 * @return
	 */
	public boolean handleVote(int term, String voteName){
		boolean ret = false;
		
		if( m_curTerm < term ) {
			
			ret = false;
		}else if( m_curTerm == term && (m_voteFor == null || m_voteFor.equals(voteName))){
			
			m_voteFor = voteName;
			ret = true;
			changeToFollower();
		}else {
			
			m_voteFor = voteName;
			m_curTerm = term;
			ret=  true;
			changeToFollower();
		}
		
		return ret;
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

	static class RaftCreator implements Creator<RaftActor> {

		private static final long serialVersionUID = 1L;
		private final String m_actorName;

		public RaftCreator(final String actorName) {
			this.m_actorName = actorName;
		}

		@Override
		public RaftActor create() throws Exception {
			return new RaftActor(m_actorName);
		}
	}

	public static void main(String[] args) {

		ActorSystem system = ActorSystem.create("System");
		for (int i = 0; i < 5; ++i) {
			system.actorOf(Props.create(new RaftCreator(new Integer(i)
					.toString())));
		}
	}

}
