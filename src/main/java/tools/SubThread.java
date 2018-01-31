package tools;

import org.sikuli.script.FindFailed;
import org.sikuli.script.Screen;


public class SubThread extends Thread{
	private boolean error;
	private boolean end;
	private Screen s;
	public SubThread() {
		this.s = new Screen();
		setError(false);
	}
    public void run(){
    	this.error= false;
    	this.end = false;
    	while(!isEnd()) {
    		if(this.s.exists("src/main/resources/images/PopUps/InternalError.png") != null) {
    			if(!isError()) { setError(true); }
    			try {
					this.s.click("src/main/resources/images/PopUps/InternalError__OK.PNG");
				} catch (FindFailed e) {
					this.end = true;
					e.printStackTrace();
				}
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
