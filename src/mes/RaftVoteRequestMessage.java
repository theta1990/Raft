package mes;


public class RaftVoteRequestMessage extends RaftMessage{

	private final String voteFor;
	
	public RaftVoteRequestMessage(int sequenceNumber, int curTime, String voteFor) {
		super(sequenceNumber, curTime);
		this.voteFor = voteFor;
	}
	
	public String getVoteFor(){
		
		return voteFor;
	}

}
