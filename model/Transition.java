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
	 
	 
	 public State getFromState() {
		 return this.FromState;
	 }
	 
	 public State getToState() {
		 return this.ToState;
	 }
	 
	 public boolean equals(Transition other)
     {
         if (this.FromState.equals(other.FromState) && this.ToState.equals(other.ToState))
             return true;

         return false;
     }
	 
	 
	 @Override
	 public boolean equals(Object o) {
	     if (this == o) return true;
	     if (o == null || getClass() != o.getClass()) return false;
	     Transition that = (Transition) o;
	     return FromState.equals(that.FromState) &&
	       ToState.equals(that.ToState);
	 }


}
