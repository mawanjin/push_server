package cn.lala.push.core;

import java.util.Collection;
import java.util.Date;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PserverHandler extends IoHandlerAdapter {
	
	 private final static Logger log = LoggerFactory.getLogger(PserverHandler.class);

	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		cause.printStackTrace();
	}

	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		String str = message.toString();
		if (str.trim().equalsIgnoreCase("quit")) {
			session.close();
			return;
		}

		Date date = new Date();
		session.write(date.toString() + ";" + message.toString());
		System.out.println("Message written..." + message);
		// 拿到所有的客户端Session
		Collection<IoSession> sessions = session.getService()
				.getManagedSessions().values();
		// 向所有客户端发送数据
		for (IoSession sess : sessions) {
			sess.write(new Date() + "\t" + message.toString());
		}
		// Thread t = new TimerMessage(session);
		// t.start();

	}


	@Override
	public void sessionClosed(IoSession session) throws Exception {
		log.info("关闭当前session：{}#{}", session.getId(),
				session.getRemoteAddress());

		CloseFuture closeFuture = session.close(true);
		closeFuture.addListener(new IoFutureListener<IoFuture>() {
			public void operationComplete(IoFuture future) {
				if (future instanceof CloseFuture) {
					((CloseFuture) future).setClosed();
					log.info("sessionClosed CloseFuture setClosed-->{},",
							future.getSession().getId());
				}
			}
		});
	}

	@Override
	public void sessionCreated(IoSession session) throws Exception {
		log.info("创建一个新连接：{}", session.getRemoteAddress());
		session.write("welcome to the chat room !");
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		log.info("当前连接{}处于空闲状态：{}", session.getRemoteAddress(), status);
	}

	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.info("打开一个session：{}#{}", session.getId(),
				session.getBothIdleCount());
	}

}
