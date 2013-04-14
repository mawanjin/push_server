package cn.lala.push.core;

import org.apache.mina.core.session.IoSession;

public class TimerMessage extends Thread{
	IoSession session;
	public TimerMessage(IoSession session){
		this.session = session;
	}

	@Override
	public void run() {
		super.run();
		try {
			Thread.currentThread().sleep(3000);
			session.write("good...");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}

}
