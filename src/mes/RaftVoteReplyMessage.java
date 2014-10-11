package mes;

/**
 * send by actors to one candidate, in reply to the vote request
 * @author volt
 *
 */
public class RaftVoteReplyMessage extends RaftMessage {

	private final boolean m_vote;	//grant volt or not
	
	public RaftVoteReplyMessage(int sequenceNumber, int curTime, boolean vote) {
		super(sequenceNumber, curTime);
		this.m_vote = vote;
	}
	
	public boolean getVote(){
		
		return m_vote;
	}
}