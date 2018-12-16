package degbug.learn.thread;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PipedReader;
import java.io.PipedWriter;

public class PipedDemo {
	public static void main(String[] args) throws IOException {
		PipedInputStream pis = new PipedInputStream();
		PipedOutputStream pos = new PipedOutputStream();
//		pos.connect(pis);
		pis.connect(pos);
		
		Thread t1 = new Thread(() -> {
			try {
				Thread.sleep(3000);
				pos.write("abc".getBytes());
				System.out.println("write -> abc");
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		});
		
		t1.start();
		
		
		Thread t2 = new Thread(() -> {
			try {
				System.out.println("read:");
				byte[] bbb = new byte[100];
				int lenth = pis.read(bbb);
				System.out.println(new String(bbb, 0, lenth));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		t2.start();
		
		
		
		PipedWriter pw = new PipedWriter();
		PipedReader pr = new PipedReader();
		pr.connect(pw);
		
		Thread t3 = new Thread(() -> {
			System.out.println("begin read");
			char[] cc = new char[100];
			
			int length;
			try {
				length = pr.read(cc);
				pr.close();
				System.out.println("readed : " + new String(cc, 0, length));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		t3.start();
		
		
		
		Thread t4 = new Thread(()->{
			try {
				Thread.sleep(3000);
				System.out.println("begin write");
				pw.write("piledwrite");
				pw.close();
				System.out.println("write piledwrite");
			} catch (InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		
		t4.start();
	}
}
