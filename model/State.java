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
	
	
	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (o == null || getClass() != o.getClass()) return false;
	    State that = (State) o;
	    return StateName.equals(that.StateName);
	}


}
