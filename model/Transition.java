package modelChecker.model;

public class Transition {
	
	
    public String TransitionName;
    public State FromState;
    public State ToState;
    
    
	public Transition() {
		// TODO Auto-generated constructor stub
	}
	
	 public Transition(State fromState, State toState)
	 {
		 this();
	     TransitionName = "";
	     FromState = fromState;
	     ToState = toState;
	 }
	 
	 public Transition(String transitionName, State fromState, State toState)
     {
		 this();
         TransitionName = transitionName;
         FromState = fromState;
         ToState = toState;
     }
	 
	 public boolean Equals(Transition other)
     {
         if (this.FromState.Equals(other.FromState) && this.ToState.Equals(other.ToState))
             return true;

         return false;
     }


}
