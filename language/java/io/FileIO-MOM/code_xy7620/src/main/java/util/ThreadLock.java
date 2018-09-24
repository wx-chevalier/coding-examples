package util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author XF
 * 此类实现一个自定义同步器。
 * <p>设置几个线程可以获得锁
 * <p>获得锁的线程可以执行，其他线程阻塞，避免cpu在线程间过多的切换耗费时间
 */
public class ThreadLock implements Lock {
	//自定义同步器
	private final Sync sync ;
	
	public ThreadLock(int count){
		sync = new Sync(count);
	}
	//默认可以4个线程获取资源
	public ThreadLock(){
		this(4);
	}
	/**
	 * 注意，這裡調用的是acquireShared方法，而不是下面實現的tryAcquireShared方法。
	 * <p>这里调用tryAcquireShared并不能锁住。
	 */
	@Override
	public void lock() {
		sync.acquireShared(1);
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean tryLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void unlock() {
		// TODO Auto-generated method stub
		sync.releaseShared(1);
	}

	@Override
	public Condition newCondition() {
		// TODO Auto-generated method stub
		return null;
	}
	//自定义同步器
	private static final class Sync extends AbstractQueuedSynchronizer{
		//count代表同步资源数，即当前还允许获得的线程数
		Sync(int count){
			if(count < 0){
				throw new IllegalArgumentException("count must large than zero");
			}
			setState(count);
		}
		@Override
		public int tryAcquireShared(int one) {
			for(;;){
				int current = getState();
				int newC = current -one;
				if(newC < 0 || compareAndSetState(current, newC)){
					return newC;
				}
			}
		}
		@Override
		public boolean tryReleaseShared(int one) {
			for(;;){
				int current = getState();
				if(compareAndSetState(current, current+one)){
					return true;
				}
			}
		}
	}
}
