package degbug.learn.thread;

import java.time.LocalTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class ThreadDemo {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		// joinDemo();

		thread3Demo();
	}

	/**
	 * jion的使用，主线程等待子线程执行完成后继续执行
	 */
	private static void joinDemo() {
		Thread t = new Thread(() -> {
			try {
				System.out.println(LocalTime.now() + "子线程执行开始");
				Thread.sleep(2000);
				System.out.println(LocalTime.now() + "子线程执行完成");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});

		t.start();
		System.out.println(LocalTime.now() + "等待子线程执行完成");
		try {
			t.join();

			System.out.println(LocalTime.now() + "子线程执行完成");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 线程的三种方式
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private static void thread3Demo() throws InterruptedException, ExecutionException {

		MyThread t = new MyThread();
		t.start();

		Thread t1 = new Thread(() -> System.out.println("实现runable函数接方式"));
		t1.start();

		FutureTask<String> ft = new FutureTask<>(() -> {
			System.out.println("实现callable函数接方式");
			return "callable";
		});

		Thread t3 = new Thread(ft);
		t3.start();

		System.out.println(ft.get());
	}

	static class MyThread extends Thread {
		@Override
		public void run() {
			System.out.println("继承Thread的方式实现");
		}
	}

}
