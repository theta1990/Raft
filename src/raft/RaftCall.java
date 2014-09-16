package raft;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RaftCall extends Remote{

	public String append() throws RemoteException;
	
	public boolean vote(String serName, int curTerm) throws RemoteException;
}
