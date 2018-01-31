package launcher;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.sikuli.basics.Settings;
import org.sikuli.script.FindFailed;

import controller.ManagerScheduler;



public class Main {
	public static void main(String[] args) {
		System.out.println("AutoAmdocs main");
		System.out.println("**********************************\n\n");
    	Settings.OcrTextSearch=true;

		ManagerScheduler ms;
		
		try {
			System.out.println("Empieza el programa en breves instantes, no toque el ratón ni el teclado hasta su finalización");
			TimeUnit.MILLISECONDS.sleep(3000);
			System.out.println("Start!");
			ms = new ManagerScheduler();
			ms.ShowExcelContent();
			ms.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FindFailed e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HeadlessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
