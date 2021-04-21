package modelChecker.model;

import java.util.ArrayList;
import java.util.List;

public class State {
	public String StateName;
	public List<String> Atoms;
	
	public State() {
//		System.out.println("Called");
		// TODO Auto-generated constructor stub
		Atoms = new ArrayList<String>();
		
	}
	public State(String stateName) {
		this();
		this.StateName = stateName;
	}
	
	public boolean Equals(State other) {
		return this.StateName.equals(other.StateName);
	}

}
