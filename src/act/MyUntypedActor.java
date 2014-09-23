package act;

import java.util.Timer;
import java.util.TimerTask;

import mes.RaftMessage;
import mes.WakeUpMessage;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.Creator;

public class MyUntypedActor extends UntypedActor {

	LoggingAdapter log = Logging.getLogger(getContext().system(), this);

	private final int magicNumber;

	public MyUntypedActor(int magic) {

		super();
		this.magicNumber = magic;
		for (int i = 0; i < magicNumber; ++i) {
			getContext().actorOf(Props.create(new InnerActor.InnerCreater(i, magicNumber)),
					new Integer(i).toString());
		}
	}

	@Override
	public void onReceive(Object message) throws Exception {

		if (message instanceof RaftMessage) {

			log.info("Received String message: {}", message.toString());

		} else {
			unhandled(message);
		}
	}

	static class InnerActor extends UntypedActor {

		private final int id;
		private final int magicNumber;
		private int seqNumber;
		private int curTime;

		InnerActor(final int id, final int magicNumber) {
			this.id = id;
			this.magicNumber = magicNumber;

			(new Timer()).schedule(new TimerTask(){

				@Override
				public void run() {
					
					getSelf().tell(new WakeUpMessage(seqNumber, curTime), getSelf());
				}
			},  id * 10 + 2);

		}

		@Override
		public void onReceive(Object message) throws Exception {
			// TODO Auto-generated method stub
			if( message instanceof WakeUpMessage){
				
				for (int i = 0; i < magicNumber; ++i) {

					if (id != i) {

						getContext().actorSelection("/user/demo/" + i).tell(
								new RaftMessage(++seqNumber, curTime),
								getSelf());
					}
				}
			}else if (message instanceof RaftMessage) {

				Logging.getLogger(getContext().system(), this).info(
						"Received String message: {}", message.toString() + getSender().path().toString());
				
				this.curTime = ((RaftMessage) message).getCurTime() + 1;
				
			} else {
				unhandled(message);
			}
		}

		static class InnerCreater implements Creator<InnerActor> {

			private static final long serialVersionUID = 1L;
			final int id;
			final int magicNumber;

			InnerCreater(int id, int magicNumber) {
				this.id = id;
				this.magicNumber = magicNumber;
			}

			@Override
			public InnerActor create() throws Exception {

				return new InnerActor(id, magicNumber);
			}
		}

	}

	static class MyUntypedCreator implements Creator<MyUntypedActor>{

		private static final long serialVersionUID = 1L;

		private final int magicNumber;
		
		public MyUntypedCreator(int magicNumber) {

			this.magicNumber = magicNumber;
		}
		
		@Override
		public MyUntypedActor create() throws Exception {

			return new MyUntypedActor(magicNumber);
		}
		
	}
	

	public static void main(String[] args) {

		final ActorSystem system = ActorSystem.create("MySystem");

		system.actorOf(Props.create(new MyUntypedCreator(3)), "demo");
		system.actorOf(Props.create(new MyUntypedCreator(2)), "demo1");
	}

}
