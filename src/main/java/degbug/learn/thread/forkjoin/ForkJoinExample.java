package degbug.learn.thread.forkjoin;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

public class ForkJoinExample extends RecursiveTask<Integer> {

	private int first;
	private int last;
	private static final int threshold = 5;

	public ForkJoinExample(int first, int last) {
		this.first = first;
		this.last = last;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected Integer compute() {
		int sub = last - first;
		int result = 0;
		if (sub <= threshold) {
			for (int i = first; i <= last; i++) {
				result += i;
			}
		} else {
			int mid = (last + first) / 2;
			ForkJoinExample left = new ForkJoinExample(first, mid);
			ForkJoinExample right = new ForkJoinExample(mid + 1, last);
			left.fork();
			right.fork();
			result = left.join() + right.join();
		}
		return result;
	}

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		ForkJoinExample e = new ForkJoinExample(1, 10000);
		ForkJoinPool pool = new ForkJoinPool();
		Future<Integer> fu = pool.submit(e);

		System.out.println(fu.get());
	}
}
