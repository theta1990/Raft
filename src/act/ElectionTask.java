package act;

import java.util.TimerTask;

public abstract class ElectionTask extends TimerTask {

	protected int m_curTerm;
	protected long m_timeOut;
	
	public ElectionTask(int curTerm, long timeOut){
		
		m_curTerm = curTerm;
		m_timeOut = timeOut;
	}
	
}
