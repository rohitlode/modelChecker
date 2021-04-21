package modelChecker.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import modelChecker.model.State;
import modelChecker.model.Transition;

public class KripkeStructure {
	public List<Transition> Transitions;
    public List<State> States;
    public List<String> Atoms;
	
	//Constructor
	
	public KripkeStructure() {
		System.out.println("Called kripke const");
		States = new ArrayList<>();
		Transitions = new ArrayList<>();
		Atoms = new ArrayList<>();
		
	}
	
	
	public static <T> List<T> convertArrayToList(T array[])
    {
		
  
        // Create an empty List
        List<T> list = new ArrayList<>();
  
        // Iterate through the array
        for (T t : array) {
 
            // Add each element into the list
            list.add(t);
        }
  
        // Return the converted List
        return list;
    }
	
	public KripkeStructure(String kripkeStructureDefinition) throws Exception
    {
		this();
        try
        {
            /*PARSING*/
            List<String> parsedStructure = new ArrayList<String>(Arrays.asList(kripkeStructureDefinition.replaceAll("[\\t\\n\\r]+"," ").split(";")));
            for( String i: parsedStructure) {
            	System.out.println("String :"+i);
            }
            System.out.println(parsedStructure.toString());

            if (parsedStructure == null || parsedStructure.size() != 3)
                throw new Exception("Input file does not contain appropriate segments to construct kripke structure");
            
            System.out.println("Here "+parsedStructure.get(0).trim());
            	List<String> stateNames = new ArrayList<String>(Arrays.asList(parsedStructure.get(0).split(",")));
                List<String> transitions = new ArrayList<String>(Arrays.asList(parsedStructure.get(1).split(",")));
                List<String> stateAtomStructures = new ArrayList<String>(Arrays.asList(parsedStructure.get(2).split(",")));
                
                System.out.println("State names "+stateNames.toString());
                System.out.println("Transition names "+transitions.toString());
                System.out.println("State with Atom names "+stateAtomStructures.toString());
                
                
              //load states

                for (String stateName: stateNames)
                {
//                	String x = stateName;
                    State state = new State(stateName.trim());
                    if (!States.contains(state))
                        States.add(state);
                    else
                        throw new Exception(String.format("State %s is defined more than once", stateName));
                }
                
                System.out.println("States Loaded Initially :"+stateNames.toString());

                //load transitions
                for (String transition : transitions)
                {
                    List<String> parsedTransition = convertArrayToList(transition.split(":"));  
                    System.out.println("Parsed Transition "+parsedTransition.toString());

                    if (parsedTransition == null || parsedTransition.size() != 2)
                        throw new Exception("Transition is not in the valid format");

                    String transitionName = parsedTransition.get(0);
                    List<String> parsedFromToStates = convertArrayToList(parsedTransition.get(1).split("-"));

                    if (parsedFromToStates == null || parsedFromToStates.size() != 2)
                        throw new Exception(String.format("Transition %s is not in [from state] - [to state] format", transitionName));

                    
                    String fromStateName = parsedFromToStates.get(0);
                    String toStateName = parsedFromToStates.get(1);
                    State fromState = FindStateByName(fromStateName.trim());
                    State toState = FindStateByName(toStateName.trim());
                    
                    if (fromState == null || toState == null)
                        throw new Exception(String.format("Invalid state is detected in transition %s", transitionName));
                    
                    System.out.println(transitionName+" "+fromState.StateName+" "+toState.StateName);
                    
                    Transition transitionObj = new Transition(transitionName, fromState, toState);
                    System.out.println("Tansitions "+Transitions);
                    if (!Transitions.contains(transitionObj))
                        Transitions.add(transitionObj);
                    else
                    {
                        throw new Exception(String.format("Transitions from state %s to state %s are defined more than once"
                            , fromStateName, toStateName));
                    }
                }
                
              //load atoms
                for (String stateAtomStructure : stateAtomStructures)
                {
                    List<String> parsedStateAtomStructure = convertArrayToList(stateAtomStructure.split(":"));
                    
                    System.out.println("Atom "+parsedStateAtomStructure.toString());
                    if(parsedStateAtomStructure.size() < 2) {
                    	parsedStateAtomStructure.add(" ");
                    }
                    if (parsedStateAtomStructure == null || parsedStateAtomStructure.size() != 2)
                        throw new Exception(String.format("%s is not a valid state: atoms definition", stateAtomStructure));
                    String stateName = parsedStateAtomStructure.get(0).replace("\\s", "");
                    String atomNames = parsedStateAtomStructure.get(1);
                    List<String> parsedAtoms = convertArrayToList(atomNames.split(" "));

                    List<String> stateAtoms = new ArrayList<String>();
                    
                    for (String atom : parsedAtoms)
                    {
                        if(atom.isEmpty())
                        {}
                        else if (!stateAtoms.contains(atom))
                            stateAtoms.add(atom);
                        else
                            throw new Exception(String.format("Atom %s is defined more than once for state %s"
                                , atom, stateName));
                    }
                    
                    System.out.println("State Atoms "+stateAtoms.toString());

                    State stateObj = FindStateByName(stateName);
                    if (stateObj == null)
                        throw new Exception(String.format("State %s is not defined", stateName));
                    stateObj.Atoms = stateAtoms;

                    //load to list of atoms
                    for (String atom : stateAtoms)
                    {
                        if (!Atoms.contains(atom))
                            Atoms.add(atom);
                    }
                    
                    System.out.println("Atoms :"+Atoms.toString());
                }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        }


	private State FindStateByName(String stName) {
		// TODO Auto-generated method stub
		for (State state : States)
        {
            if (state.StateName.equals(stName.trim()))
                {
//            		System.out.println("Found");
            		return state;
                }
        }
		return null;
	}
	
	@Override
	public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append("STATES");
        sb.append(System.getProperty("line.separator"));
        sb.append("-----------");
        sb.append(System.getProperty("line.separator"));
        sb.append(StatesToString());
        sb.append(System.getProperty("line.separator"));
        sb.append("");
        sb.append("");
        sb.append(System.getProperty("line.separator"));
        sb.append("TRANSITIONS");
        sb.append(System.getProperty("line.separator"));
        sb.append("-------------------");
        sb.append(System.getProperty("line.separator"));
        sb.append(TransitionsToString());
        sb.append(System.getProperty("line.separator"));

        return sb.toString();
    }
	
	private String TransitionsToString() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();

        List<String> transitionString = new ArrayList<String>();
        for (Transition transition : Transitions)
        {
            transitionString.add(String.format("%s(%s -> %s)", transition.TransitionName
                , transition.FromState.StateName, transition.ToState.StateName));
        }
        String trimmedString = transitionString.toString().replace("[", "").replace("]", "");
        sb.append(String.join(", ", trimmedString ));
        return sb.toString();
	}


	public String StatesToString()
    {
        StringBuilder sb = new StringBuilder();

        List<String> stateStrings = new ArrayList<String>();
        for (State state : States)
        {      	
        	String trimmedNames = state.Atoms.toString().replace("[", "").replace("]", "");
            String atomNames = String.join(", ", trimmedNames);
            stateStrings.add(String.format("%s(%s)", state.StateName, atomNames));
        }
        String trimmedString = stateStrings.toString().replace("[", "").replace("]", "");
        sb.append(String.join(", ", trimmedString ));
        return sb.toString();
    }
	
	public List<String> getStates() {
		List<String> ls = new ArrayList<String>();
		for(State state: States) {
			ls.add(state.StateName);
		}
		return ls;
		
	}
}
