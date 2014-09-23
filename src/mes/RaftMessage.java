package mes;

/**
 * 如何解决序列化的问题的？
 * @author volt
 *
 */
public class RaftMessage{
	
	private final int sequenceNumber;
	private final int curTime;
	
	
	public RaftMessage(int sequenceNumber, int curTime){
		
		this.sequenceNumber = sequenceNumber;
		this.curTime = curTime;
	}
	
	public int getSequenceNumber(){
		
		return this.sequenceNumber;
	}
	
	public int getCurTime(){
		
		return this.curTime;
	}
	
	@Override
	public String toString() {

		StringBuffer sb = new StringBuffer();
		sb.append("SequenceNumber: ");
		sb.append(sequenceNumber);
		sb.append("\t");
		sb.append("curTime: ");
		sb.append(curTime);
		return sb.toString();
	}
}