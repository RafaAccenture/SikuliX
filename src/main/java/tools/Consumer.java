package tools;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;

public class Consumer extends Thread{
	private int countPopups;
	private Semaphore semaphore;
	private Semaphore mutex;
	private boolean on;
	private Screen s;
	
	synchronized private void checkInternalError() throws InterruptedException {
		if(this.s.exists("src/main/resources/images/PopUps/InternalError.png") != null) {
			increaseCountPopus();
			try {
				this.s.click("src/main/resources/images/PopUps/InternalError__OK.PNG");
			} catch (FindFailed e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		TimeUnit.MILLISECONDS.sleep(500);
	}
	public Consumer(Semaphore semaphore, Semaphore mutex) {
		super();
		this.semaphore = semaphore;
		this.mutex = mutex;
		this.s = new Screen();
		setCountPopups(0);
	}
	@Override
    public void run(){
		try {
			semaphore.acquire();
			mutex.acquire();
	    	while(isOn()) {
	    		checkInternalError();
	    	}
	    	mutex.release();
	        Thread.sleep(500);
		}catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
	public Screen getS() {
		return s;
	}
	public void setS(Screen s) {
		this.s = s;
	}
	public void increaseCountPopus() {
		this.countPopups++;
	}
	public int getCountPopups() {
		return countPopups;
	}
	public void setCountPopups(int countPopups) {
		this.countPopups = countPopups;
	}
	synchronized public boolean isOn() {
		return on;
	}
	public void setOnOff(boolean on) {
		this.on = on;
	}	
}
