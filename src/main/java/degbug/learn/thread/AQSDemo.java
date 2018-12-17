package degbug.learn.thread;

import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static degbug.learn.util.PrintUtil.print;;

public class AQSDemo {
	public static void main(String[] args) throws InterruptedException {
		// countDownLatchDemo();
//		countDownLatchDemo2();
		cyclicBarrierDemo();
		// semaphoreDemo();
	}

	/**
	 * countdownLatch demo:<br>
	 * 
	 * 场景：<br>
	 * 多个线程执行完成后，再进行下一步操作
	 */
	public static void countDownLatchDemo1() {

		int count = 10;
		CountDownLatch latch = new CountDownLatch(count);
		ExecutorService service = Executors.newFixedThreadPool(2);
		for (int i = 0; i < count; i++) {
			final int id = i;
			service.execute(() -> {
				print("线程" + id + "开始执行");
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				print("线程" + id + "开始结束");
				latch.countDown();
			});
		}

		print("主线程开始等待");
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		service.shutdown();
		print("all executed, main thread execute");
	}

	/**
	 * countDownLatch Demo:<br>
	 * 场景<br>
	 * 多个线程等待某个动作完成后，一起执行。类似等待发令枪响
	 * @throws InterruptedException 
	 */
	public static void countDownLatchDemo2() throws InterruptedException {
		int count = 5;
		CountDownLatch latch = new CountDownLatch(1);

		Random r = new Random();
		ExecutorService service = Executors.newCachedThreadPool();
		for (int i = 0; i < count; i++) {
			final int id = i;
			service.execute(() -> {
				int sleep = r.nextInt(10);
				print("线程"+id+"随机睡眠" + sleep);
				try {
					TimeUnit.SECONDS.sleep(sleep);
					print("线程" + id + "复苏，准备开始执行动作");
					latch.await();
					print("线程" + id + "开始执行");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
		
		
		print("主线程睡眠10秒");
		TimeUnit.SECONDS.sleep(10);
		print("主线程清醒了，开始发令");
		latch.countDown();
		
		service.shutdown();

	}

	/**
	 * cyclicBarrier适用于多个线程执行完某些操作后，再进行继续进行而外的操作。同时可以设置这些操作完成后执行的某个动作
	 */
	public static void cyclicBarrierDemo() {
		int count = 6;
		CountDownLatch latch = new CountDownLatch(1);
		CyclicBarrier barrier = new CyclicBarrier(count / 2, () -> {
			latch.countDown();
			System.out.println(count + "线程执行完了，才执行这里");
		});

		ExecutorService service = Executors.newCachedThreadPool();
		for (int i = 0; i < count; i++) {
			final int id = i;
			service.execute(() -> {
				print("线程" + id + "执行");
				try {
					TimeUnit.SECONDS.sleep(id);
					barrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
				print("所有线程都执行了await线程" + id + "执行完成");
			});
		}

		try {
			latch.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		barrier.reset();
		print("结束");

		for (int i = 0; i < count; i++) {
			final int id = i;
			service.execute(() -> {
				print("线程" + id + "执行");
				try {
					TimeUnit.SECONDS.sleep(id);
					barrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
				print("所有线程都执行了await线程" + id + "执行完成");
			});
		}
	}

	/**
	 * semaphore主要用于资源的共享，用于有限资源的共享。 <br>
	 * 例子中保证同时只有两个线程再跑
	 */
	public static void semaphoreDemo() {
		Semaphore semaphore = new Semaphore(2);
		ExecutorService service = Executors.newFixedThreadPool(2);
		for (int i = 0; i < 10; i++) {
			final int id = i;
			service.execute(() -> {
				try {
					semaphore.acquire();
					print("线程" + id + "获取到资源，执行睡眠2秒");
					TimeUnit.SECONDS.sleep(2);
					print("线程" + id + "执行结束");
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					semaphore.release();
					// TODO: handle finally clause
				}
			});
		}
	}
}
