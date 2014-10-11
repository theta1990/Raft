package mes;

/**
 * a message sent by candidate to the others, asking for votes
 * @author volt
 *
 */
public class RaftVoteRequestMessage extends RaftMessage{

	private final int voteFor;	//please, give me a vote
	
	public RaftVoteRequestMessage(int sequenceNumber, int curTime, int voteFor) {
		super(sequenceNumber, curTime);	//my current time phase
		this.voteFor = voteFor;
	}
	
	public int getVoteFor(){
		
		return voteFor;
	}

}
