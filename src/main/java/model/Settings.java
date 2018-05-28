package model;

public class Settings {
	private float scaledImgRatio;
	private String environment;
	private int MAX_TIMEWAIT;
	private int MAX_NUMBER_ERRORS;
	
	
	
	public Settings() {
		super();
	}

	public Settings(float scaledImgRatio, String environment, int mAX_TIMEWAIT, int mAX_NUMBER_ERRORS) {
		super();
		this.setScaledImgRatio(scaledImgRatio);
		this.environment = environment;
		MAX_TIMEWAIT = mAX_TIMEWAIT;
		MAX_NUMBER_ERRORS = mAX_NUMBER_ERRORS;
	}
	
	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}
	public int getMAX_TIMEWAIT() {
		return MAX_TIMEWAIT;
	}
	public void setMAX_TIMEWAIT(int mAX_TIMEWAIT) {
		MAX_TIMEWAIT = mAX_TIMEWAIT;
	}
	public int getMAX_NUMBER_ERRORS() {
		return MAX_NUMBER_ERRORS;
	}
	public void setMAX_NUMBER_ERRORS(int mAX_NUMBER_ERRORS) {
		MAX_NUMBER_ERRORS = mAX_NUMBER_ERRORS;
	}

	public float getScaledImgRatio() {
		return scaledImgRatio;
	}

	public void setScaledImgRatio(float scaledImgRatio) {
		this.scaledImgRatio = scaledImgRatio;
	}
}
