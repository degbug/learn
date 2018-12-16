package degbug.learn.util;

import java.time.LocalTime;

public class PrintUtil {
  public static void print(String s) {
	  System.out.println(LocalTime.now() + ":" + s);
  }
}	
