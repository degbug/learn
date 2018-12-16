package degbug.learn.thread;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class CLHDemo {
	public static void main(String[] args) {
		CLHLock lock = new CLHLock();
		Thread t1 = new Thread(() -> {
			print("t1准备获取锁");
			lock.lock();
			try {
				print("t1获取到锁，开始休息3秒");
				try {
					TimeUnit.SECONDS.sleep(60);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} finally {
				lock.unlock();
				print("t1释放了锁");
			}
		});
		
		Thread t2 = new Thread(() -> {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			print("t2准备开始获取锁");
			lock.lock();
			try {
				print("t2获取到锁");
			} finally {
				lock.unlock();
				print("t2释放锁");
			}
		});
		
		t1.start();
		
		t2.start();
		
		try {
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		print("全结束了");
	}
	
	
	private static void print(String msg) {
		System.out.println(LocalTime.now() + ":" + msg);
	}
	
	static class CLHLock implements Lock{

		private ThreadLocal<Node> node;
		private ThreadLocal<Node> pre;
		
		private AtomicReference<Node> tail = new AtomicReference<>(new Node());
		
		public CLHLock() {
			node = new ThreadLocal<Node>() {
				@Override
				protected Node initialValue() {
					return new Node();
				}
			};
			
			pre = new ThreadLocal<Node>() {
				protected Node initialValue() {
					return null;
				};
			};
		}
		
		@Override
		public void lock() {
			//获取一个节点
			final Node node = this.node.get();
			node.locked = true;
			Node pre = this.tail.getAndSet(node);//放到队尾
			this.pre.set(pre);
			
			//自旋
			while(pre.locked);
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Condition newCondition() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean tryLock() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean tryLock(long arg0, TimeUnit arg1) throws InterruptedException {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void unlock() {
			final Node node = this.node.get();
			node.locked = false;
			this.node.set(this.pre.get());
		}
		
	}
	
	private static class Node{
		private volatile boolean locked;
	}
}
