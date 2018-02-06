package windows.triggers;

import java.util.concurrent.Semaphore;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;

public class ErrorDetector extends Thread{
	private boolean error;
	private boolean end;
	private Screen s;
	private Semaphore semaphore;
	private Semaphore mutex;
	public ErrorDetector(Semaphore semaphore,Semaphore mutex) {
		this.s = new Screen();
		this.semaphore=semaphore;
		this.mutex=mutex;
	}
    public void run(){
    	System.out.println("Hilo consumidor 'ERROR DETECTOR' activado");
    	setError(false);
    	setEnd(false);//se controla desde fuera el setter a true
    	try {
            // acquire lock. Acquires the given number of permits from this semaphore, blocking until all are
            // available
            // process stops here until producer releases the lock
            semaphore.acquire();
            // Acquires a permit from this semaphore, blocking until one is available
            mutex.acquire();
	    	while(!isEnd()) {
	    		if(this.s.exists("src/main/resources/images/PopUps/InternalError.png") != null) {
	    			if(!isError()) { setError(true); }
	    			try {
						this.s.click("src/main/resources/images/PopUps/InternalError__OK.PNG");
					} catch (FindFailed e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	    		}	
	    	}
    	} catch (Exception e) {
    		System.err.println(e);
		}finally {
			mutex.release();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
    }
    
	public boolean isError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
		if(this.error)
			System.err.println("\n\nSe ha detectado un popUp de error!!\n");
	}
	public Screen getS() {
		return s;
	}
	public void setS(Screen s) {
		this.s = s;
	}
	public boolean isEnd() {
		return end;
	}
	public void setEnd(boolean end) {
		this.end = end;
	}
}