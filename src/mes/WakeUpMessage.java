package mes;


public class WakeUpMessage extends RaftMessage{

	public WakeUpMessage(int sequenceNumber, int curTime) {
		super(sequenceNumber, curTime);
	}

}
