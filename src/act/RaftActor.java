package act;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import mes.ElectionMessage;
import mes.HeartBeatCheckMessage;
import mes.HeartBeatMessage;
import mes.RaftMessage;
import mes.RaftVoteReplyMessage;
import mes.RaftVoteRequestMessage;
import raft.Configure;
import raft.State;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.japi.Creator;

public class RaftActor extends UntypedActor {

	private final int m_id;

	private int m_curTerm;
	private int m_voteFor;
	private State m_state;

	private int m_votesNum;

	private long followerTimeOut = 5000;
	private long candidateTimeOut = 1000;
	private long heartbeatCycle = 1000;

	private Timer m_Timer = null;

	public RaftActor(final int id) {
		super();
		this.m_id = id;
		this.m_curTerm = 0;
		this.m_voteFor = -1;
		this.m_Timer = new Timer();

		changeToFollower();
	}

	public final int getid() {

		return m_id;
	}

	public int getCurTime() {

		return m_curTerm;
	}

	@Override
	public void onReceive(Object message) throws Exception {

		
		if (!(message instanceof RaftMessage)){
			unhandled(message);
			return;
		}
		else {
			if (((RaftMessage) message).getCurTime() < getCurTime()) {
				// we are more advanced, we do not listen to what the message.
				// ignore it
				System.out.println("ignore message from " + getSender()
						+ ", time = " + ((RaftMessage) message).getCurTime());
				return;
			}
		}

		if (message instanceof ElectionMessage) {
			// begin election
			if (m_state != State.leader)
				handleElection();
		} else if (message instanceof RaftVoteRequestMessage) {
			// receive vote request
			handleVoteRequest(((RaftVoteRequestMessage) message).getCurTime(),
					((RaftVoteRequestMessage) message).getVoteFor());
		} else if (message instanceof RaftVoteReplyMessage) {
			// receive reply for vote request
			handleVoteReply(((RaftVoteReplyMessage) message).getCurTime(),
					((RaftVoteReplyMessage) message).getVote());
		} else if (message instanceof HeartBeatCheckMessage) {
			// try to send heart beat message
			handleHeartBeatCheck();
		} else if (message instanceof HeartBeatMessage) {
			handleHeartBeat();
		}
		
		System.out.println("[ " + getSender().path().name() + "  to  " + getSelf().path().name() + " ]" + 
		"[State : " + m_state.name() + "] " +  
		"[Term : " + m_curTerm + "] "  +
		"[fo: " + m_voteFor + "]" + 
		"[votes: " + m_votesNum + " ]"
				);

	}

	/**
	 * A heartbeat message with leader time out. The actor choose to enter into
	 * a new era, And try to elect itself as the leader Follower to candidates
	 */
	private void handleElection() {

		m_curTerm++;
		m_voteFor = m_id;
		m_votesNum = 1;
		m_state = State.candidate;

		try {

			// final ArrayList<Integer> serverIds = Configure.getConfig()
			// .getServerList();

			// for (Integer server : serverIds) {
			// if (server == m_id)
			// continue;
			// getContext().actorSelection("/user/" + server.toString()).tell(
			// new RaftVoteRequestMessage(1, m_curTerm, server),
			// getSelf());
			//
			// }

			final ArrayList<ActorRef> serverRef = Configure.getConfig()
					.getServerRef();

			for (ActorRef ref : serverRef) {
				if (getSelf().equals(ref))
					continue;
				ref.tell(new RaftVoteRequestMessage(1, m_curTerm, m_id),
						getSelf());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		changeToCandidate();
	}

	/**
	 * 处理投票请求
	 * 
	 * @param term
	 * @param voteName
	 * @return
	 */
	private boolean handleVoteRequest(int term, int voteName) {

		boolean ret = false;

		if (m_curTerm < term) {
			m_voteFor = voteName;
			m_curTerm = term;
			ret = true;
			changeToFollower();
		} else if (m_curTerm == term
				&& (m_voteFor == -1 || m_voteFor == voteName)) {
			m_voteFor = voteName;
			ret = true;
			changeToFollower();
		}
		
		if( ret ) getSender().tell(new RaftVoteReplyMessage(1, m_curTerm, true) , getSelf());
		
		return ret;
	}

	/**
	 * 接受投票，增加自己获得的选票
	 * 
	 * @param term
	 * @param vote
	 */
	private void handleVoteReply(int term, boolean vote) {

		if (vote) {
			++m_votesNum;

			try {
				if (m_votesNum > Configure.getConfig().getServerNum() / 2) {

					changeToLeader();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void handleHeartBeatCheck() {

		try {

//			for (Integer server : serverList) {
//				if (server == m_id)
//					continue;
//				getContext().actorSelection("/user/" + server.toString()).tell(
//						new HeartBeatMessage(1, m_curTerm), getSelf());
//			}
			
			final ArrayList<ActorRef> serverRef = Configure.getConfig()
					.getServerRef();

			for (ActorRef ref : serverRef) {
				if (getSelf().equals(ref))
					continue;
				ref.tell(new HeartBeatMessage(1, m_curTerm), getSelf());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleHeartBeat() {

		ElectionTask task = new ElectionTask(m_curTerm, followerTimeOut) {
			@Override
			public void run() {

				getSelf().tell(new ElectionMessage(1, this.m_curTerm),
						getSelf());
			}
		};
		m_Timer.cancel();
		m_Timer = null;
		m_Timer = new Timer();
		m_Timer.schedule(task, task.m_timeOut);
	}

	/**
	 * Now work as a follower DONE
	 */
	private void changeToFollower() {

		m_state = State.follower;
//		System.out.println("[ id: " + m_id + " , time = " + m_curTerm + "] "
//				+ "I am follower");
		// use the term when teh task built, instead of which when the task run.
		ElectionTask task = new ElectionTask(m_curTerm, followerTimeOut) {
			@Override
			public void run() {

				getSelf().tell(new ElectionMessage(1, this.m_curTerm),
						getSelf());
			}
		};
		m_Timer.cancel();
		m_Timer = null;
		m_Timer = new Timer();
		m_Timer.schedule(task, task.m_timeOut);
	}

	/**
	 * Now work as a candidate DONE
	 */
	private void changeToCandidate() {

		m_state = State.candidate;
//		System.out.println("[ id: " + m_id + " , time = " + m_curTerm + "] "
//				+ "I am candidate");

		int timeOut = (int) ((1 + Math.random()) / 2 * candidateTimeOut);
		ElectionTask task = new ElectionTask(m_curTerm, timeOut) {

			@Override
			public void run() {

				getSelf().tell(new ElectionMessage(1, this.m_curTerm),
						getSelf());
			}
		};
		m_Timer.cancel();
		m_Timer = null;
		m_Timer = new Timer();
		m_Timer.schedule(task, task.m_timeOut);
	}

	private void changeToLeader() {

		m_state = State.leader;
//		System.out.println("[ id: " + m_id + " , time = " + m_curTerm + "] "
//				+ "I am leader");

		HeartBeatTask task = new HeartBeatTask(m_curTerm) {

			@Override
			public void run() {

				// send heart beat message to all nodes;
				getSelf().tell(new HeartBeatCheckMessage(1, getTime()),
						getSelf());
			}
		};
		/**
		 * heartbeat 可以有两种方式来做，一种是另外一个线程，周期性的发送heartbeat消息，
		 * 另外一种方法是另外一个线程将heartbeat的请求周期性的加入到acotr的事件队列中 ，由Actor来负责发送heartbeat请求。
		 * 现在我们先采用第二种方案
		 */
		m_Timer.cancel();
		m_Timer = null;
		m_Timer = new Timer();
		m_Timer.schedule(task, 0, heartbeatCycle);
	}

	static class RaftCreator implements Creator<RaftActor> {

		private static final long serialVersionUID = 1L;
		private final int m_id;

		public RaftCreator(final int id) {
			this.m_id = id;
		}

		@Override
		public RaftActor create() throws Exception {
			return new RaftActor(m_id);
		}
	}

	public static void main(String[] args) throws Exception {

		ActorSystem system = ActorSystem.create("System");

		for (int i = 0; i < 5; ++i) {
			ActorRef ref = system.actorOf(Props.create(new RaftCreator(i)),
					(new Integer(i).toString()));
			Configure.getConfig().add(ref);
		}
	}

}
