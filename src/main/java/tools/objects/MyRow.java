package tools.objects;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MyRow {
	private List<String> action;
	private Queue<Queue<String>> input;
	public MyRow() { 
		this.action = new ArrayList<String>();
	}
	public MyRow(ArrayList<String> action,LinkedList<Queue<String>> input) {
		this.action = action;
		this.input = input;
    }
	
	public String toString() {
	    return String.format("%s - %s", action, "numero de inputs:"+input.size());
	}
	 
	public void addColToInput(Queue<String> col) {
		this.input.add(col);
	}
	public Queue<String> getFirstAndRemove(){
		return this.input.poll();
	}
	public String getAction(int pos) {
		return this.action.get(pos);
	}
	public void setAction(String action) {
		this.action.add(action);
	}
	public Queue<Queue<String>> getInput() {
		return input;
	}
	public void setInput(LinkedList<Queue<String>> linkedList) {
		this.input = linkedList;
	}

}
