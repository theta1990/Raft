package act;

import java.util.TimerTask;

public abstract class HeartBeatTask extends TimerTask{

	private int m_term;
	
	public HeartBeatTask(int term){
		
		m_term = term;
	}
	
	public int getTime(){
		
		return m_term;
	}
}
