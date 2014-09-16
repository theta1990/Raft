package raft;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public abstract class RaftRpcServer extends UnicastRemoteObject implements
		RaftCall {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8632177763984590325L;
	static final String APPEND = "append";
	static final String VOTE = "vote";

	private String m_name;
	
	public RaftRpcServer(String serverName) throws RemoteException {
		super(0);
		m_name = serverName;
	}

	@Override
	public abstract String append();

	@Override
	public abstract boolean vote(String serName, int curTerm);

	public void bind() throws Exception {

		System.out.println("Raft server started");

		try {
			LocateRegistry.createRegistry(1099);
			System.out.println("java RMI registry created.");
		} catch (RemoteException e) {
			System.out.println("java RMI registry already exists");
		}

		Naming.rebind("//localhost/" + m_name, this);
		System.out.println("Raft rpc server established.");
	}

}
