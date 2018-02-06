package tools;

import java.util.concurrent.Semaphore;

public class Producer extends Thread {
	private int counter;
	private boolean end;
	private Semaphore semaphore;
	private Semaphore mutex;
	public Producer(Semaphore semaphore,Semaphore mutex) {
		this.semaphore=semaphore;
		this.mutex=mutex;
		this.end = false;
	}
    public void run() {

        this.counter = 0;
        try {
            while (!isEnd()) {
                mutex.acquire();
                System.out.println("El generador reporta nuevo hilo.\tcontador="+this.counter);
                this.counter++;
                mutex.release();
                
                // release lock
                semaphore.release();
                Thread.sleep(500);
            }
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
	public boolean isEnd() {
		return end;
	}
	public void setEnd(boolean end) {
		this.end = end;
	}
	public int getCounter() {
		return counter;
	}
	public void setCounter(int counter) {
		this.counter = counter;
	}
}
