package modelChecker.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modelChecker.model.State;
import modelChecker.model.Transition;

public class ctlFormula {
	
	 public KripkeStructure _kripke;
     public State _state;
     public String _expression;
     public HashMap<String, String> _convertionString;
     private String leftExpression;
	 private String rightExpression;
     
	public enum TypeSAT
    {
        Unknown,
        AllTrue,
        AllFalse,
        Atomic,
        Not,
        And,
        Or,
        Implies,
        AX,
        EX,
        AU,
        EU,
        EF,
        EG,
        AF,
        AG
    }
	
	
	public ctlFormula() {
		this.leftExpression = "";
		this.rightExpression = "";
	}
	
	public ctlFormula(String expression, State state, KripkeStructure kripke)
    {
		this();
        this._convertionString = new HashMap<String, String>();
        this._convertionString.put("and", "&");
        this._convertionString.put("or", "|");
        this._convertionString.put("->", ">");
        this._convertionString.put("not", "!");

        this._kripke = kripke;
        this._state = state;
        this._expression = ConvertToSystemFormula(expression);
    }

	private String ConvertToSystemFormula(String expression) {
		// TODO Auto-generated method stub
		 for (Map.Entry<String, String> entry : this._convertionString.entrySet())
         {
             expression = expression.replace(entry.getKey().toString(), entry.getValue().toString());
         }
		 System.out.println("Converted expression "+expression);
         return expression;
	}
	
	
	public boolean IsSatisfy() throws Exception
    {
        List<State> states = SAT(_expression);
        System.out.println("ISSATISFY STATES");
        for(State st: states) {
        	System.out.print(st.StateName+" ");
        }
        System.out.println();
        for (State st: states) {
        	System.out.print(st.StateName+" ");
        	if(st.StateName.equals(_state.StateName))
        		return true;
        }
        return false;
    }

	private List<State> SAT(String expression) throws Exception
    {
        System.out.println("Original Expression: "+ expression);
        List<State> states = new ArrayList<State>();

        //from Logic in Computer Science, page 227
//        this.this.leftExpression = ""; 
//        		this.this.rightExpression = "";

        //TypeSAT typeSAT = DetermineTypeSAT(expression,  this.leftExpression,  this.rightExpression);
        TypeSAT typeSAT = GetTypeSAT(expression);

        System.out.println("Type SAT: "+ typeSAT.toString());
        System.out.println("Left Expression: "+this.leftExpression);
        System.out.println("Right Expression: "+this.rightExpression);
        System.out.println("------------------------------------");

        switch (typeSAT)
        {
            case AllTrue:
                //all states
            	states.addAll(_kripke.States);
                break;
            case AllFalse:
                //empty 
                break;
            case Atomic:
                for (State state : _kripke.States)
                {
                    if (state.Atoms.contains(this.leftExpression))
                        states.add(state);
                }
                break;
            case Not:
                //S − SAT (φ1)
            	System.out.println("States "+_kripke.States.toString());
                states.addAll(_kripke.States);
                System.out.println("Left Exprssion "+this.leftExpression);
                List<State> f1States = SAT(this.leftExpression);

                for (State state : f1States)
                {
                    if (states.contains(state))
                        states.remove(state);
                }
                break;
            case And:
                //SAT (φ1) ∩ SAT (φ2)
                List<State> andF1States = SAT(this.leftExpression);
                List<State> andF2States = SAT(this.rightExpression);

                for (State state : andF1States)
                {
                    if (andF2States.contains(state))
                        states.add(state);
                }
                break;
            case Or:
                //SAT (φ1) ∪ SAT (φ2)
                List<State> orF1States = SAT(this.leftExpression);
                List<State> orF2States = SAT(this.rightExpression);

                states = orF1States;
                for (State state : orF2States)
                {
                    if (!states.contains(state))
                        states.add(state);
                }
                break;
            case Implies:
                //SAT (¬φ1 ∨ φ2)
                //TODO: reevaluate impliesFormula
                String impliesFormula = "!" + this.leftExpression + "|" +this.rightExpression;
                states = SAT(impliesFormula);
                break;
            case AX:
                //SAT (¬EX¬φ1)
                //TODO: reevaluate axFormula
                String axFormula = "!" + "EX" + "!"+ this.leftExpression;
                states = SAT(axFormula);

                //check if states actually has link to next state
                List<State> tempStates = new ArrayList<State>();
                for (State sourceState : states)
                {
                    for (Transition transition : _kripke.Transitions)
                    {
                        if (sourceState.equals(transition.FromState))
                        {
                            tempStates.add(sourceState);
                            break;
                        }
                    }
                }
                states = tempStates;
                break;
            case EX:
                //SATEX(φ1)
                //TODO: reevaluate exFormula
                String exFormula = this.leftExpression;
                states = SAT_EX(exFormula);
                break;
            case AU:
                //A[φ1 U φ2]
                //SAT(¬(E[¬φ2 U (¬φ1 ∧¬φ2)] ∨ EG¬φ2))
                //TODO: reevaluate auFormulaBuilder
                StringBuilder auFormulaBuilder = new StringBuilder();
                auFormulaBuilder.append("!(E(!");
                auFormulaBuilder.append(this.rightExpression);
                auFormulaBuilder.append("U(!");
                auFormulaBuilder.append(this.leftExpression);
                auFormulaBuilder.append("&!");
                auFormulaBuilder.append(this.rightExpression);
                auFormulaBuilder.append("))|(EG!");
                auFormulaBuilder.append(this.rightExpression);
                auFormulaBuilder.append("))");
                states = SAT(auFormulaBuilder.toString());
                break;
            case EU:
                //SATEU(φ1, φ2)
                //TODO: reevaluate euFormula
                states = SAT_EU(this.leftExpression, this.rightExpression);
                break;
            case EF:
                //SAT (E( U φ1))
                //TODO: reevaluate efFormula
                String efFormula = "E(TU" + this.leftExpression + ")";
                states = SAT(efFormula);
                break;
            case EG:
                //SAT(¬AF¬φ1)
                //TODO: reevaulate egFormula
                String egFormula = "!AF!" + this.leftExpression;
                states = SAT(egFormula);
                break;
            case AF:
                //SATAF (φ1)
                //TODO: reevaluate afFormula
                String afFormula = this.leftExpression;
                states = SAT_AF(afFormula);
                break;
            case AG:
                //SAT (¬EF¬φ1)
                //TODO: reevaluate agFormula
                String agFormula = "!EF!" + this.leftExpression;
                states = SAT(agFormula);
                break;
            case Unknown:
                throw new Exception("Invalid CTL expression");
        }

        return states;
    }
	
	
	
	private String mySubString(String myString, int start, int length) {
	    return myString.substring(start, Math.min(start + length, myString.length()));
	}
	
	
	
	private TypeSAT GetTypeSAT(String expression)
    {
		System.out.println("GET TYPE SAT "+expression);
        //remove extra brackets
        expression = RemoveExtraBrackets(expression);

        //look for binary implies
        if (expression.contains(">"))
        {
            if (IsBinaryOp(expression, ">"))
                return TypeSAT.Implies;
        }
        //look for binary and
        if (expression.contains("&"))
        {
            if (IsBinaryOp(expression, "&"))
                return TypeSAT.And;
        }
        //look for binary or
        if (expression.contains("|"))
        {
            if (IsBinaryOp(expression, "|"))
                return TypeSAT.Or;
        }
        //look for binary AU
        if (expression.startsWith("A("))
        {
            String strippedExpression = mySubString(expression, 2, expression.length() - 3);
            if (IsBinaryOp(strippedExpression, "U"))
                return TypeSAT.AU;
        }
        //look for binary EU
        if (expression.startsWith("E("))
        {
            String strippedExpression = mySubString(expression, 2, expression.length() - 3);
            if (IsBinaryOp(strippedExpression, "U"))
                return TypeSAT.EU;
        }

        //look for unary T, F, !, AX, EX, AG, EG, AF, EF, atomic
        if (expression.equals("T"))
        {
            this.leftExpression = expression;
            return TypeSAT.AllTrue;
        }
        if (expression.equals("F"))
        {
            this.leftExpression = expression;
            return TypeSAT.AllFalse;
        }
        if (IsAtomic(expression))
        {
            this.leftExpression = expression;
            return TypeSAT.Atomic;
        }
        if (expression.startsWith("!"))
        {
        	System.out.println("Not exp");
            this.leftExpression = mySubString(expression, 1, expression.length() - 1);
            return TypeSAT.Not;
        }
        if (expression.startsWith("AX"))
        {
            this.leftExpression = mySubString(expression,2, expression.length() - 2);
            return TypeSAT.AX;
        }
        if (expression.startsWith("EX"))
        {
            this.leftExpression = mySubString(expression,2, expression.length() - 2);
            return TypeSAT.EX;
        }
        if (expression.startsWith("EF"))
        {
            this.leftExpression = mySubString(expression,2, expression.length() - 2);
            return TypeSAT.EF;
        }
        if (expression.startsWith("EG"))
        {
            this.leftExpression = mySubString(expression,2, expression.length() - 2);
            return TypeSAT.EG;
        }
        if (expression.startsWith("AF"))
        {
            this.leftExpression = mySubString(expression,2, expression.length() - 2);
            return TypeSAT.AF;
        }
        if (expression.startsWith("AG"))
        {
        	this.leftExpression = mySubString(expression, 2, expression.length()-2);
        	  System.out.println("Converted AG exp "+ this.leftExpression);
            return TypeSAT.AG;
        }

        return TypeSAT.Unknown;
    }

	

	//SAT EU
	private List<State> SAT_EU(String leftExpString, String rightExpString) throws Exception {
		// TODO Auto-generated method stub
		List<State> w = new ArrayList<State>();
        List<State> x = new ArrayList<State>();
        List<State> y = new ArrayList<State>();

        w = SAT(leftExpString);
        x.addAll(_kripke.States);
        y = SAT(rightExpString);

        while (!AreListStatesEqual(x, y))
        {
            x = y;
            List<State> newY = new ArrayList<State>();
            List<State> preEStates = PreE(y);

            newY.addAll(y);
            List<State> wAndPreE = new ArrayList<State>();
            for (State state : w)
            {
                if (preEStates.contains(state))
                    wAndPreE.add(state);
            }

            for (State state : wAndPreE)
            {
                if (!newY.contains(state))
                    newY.add(state);
            }
            y = newY;
        }

        return y;
	}

	
	
	private List<State> PreE(List<State> y) {
        //{s ∈ S | exists s, (s → s and s ∈ Y )}
        List<State> states = new ArrayList<State>();

//        List<Transition> transitions = new ArrayList<Transition>();
        for (State sourceState : _kripke.States)
        {
            for (State destState : y)
            {
                Transition myTransition = new Transition(sourceState, destState);
                System.out.println("PRE E :"+" ["+myTransition.getFromState().StateName+","+myTransition.getToState().StateName+"] "+_kripke.Transitions.stream().anyMatch(o -> 
                		o.equals(myTransition)));
                if(_kripke.Transitions.stream().anyMatch(o -> 
                		o.equals(myTransition)
                		))
//                if (_kripke.Transitions.contains(myTransition))
                {
                    if (!states.contains(sourceState))
                        states.add(sourceState);
                }
            }
        }

        return states;
	}

	
	private boolean AreListStatesEqual(List<State> list1, List<State> list2) {
		// TODO Auto-generated method stub
		if (list1.size() != list2.size())
            return false;

        for (State state : list1)
        {
            if (!list2.contains(state))
                return false;
        }

        return true;
	}

	
	//SAT_AF
	private List<State> SAT_AF(String afFormula) throws Exception {
		// TODO Auto-generated method stub
		List<State> x = new ArrayList<State>();
        x.addAll(_kripke.States);
        List<State> y = new ArrayList<State>();
        y = SAT(afFormula);

        while (!AreListStatesEqual(x, y))
        {
            x = y;
            List<State> newY = new ArrayList<State>();
            List<State> preAStates = PreA(y);
            newY.addAll(y);

            for (State state : preAStates)
            {
                if (!newY.contains(state))
                    newY.add(state);
            }

            y = newY;
        }

        return y;
	}


	private List<State> PreA(List<State> y) {
		// TODO Auto-generated method stub
		 List<State> PreEY = PreE(y);

         List<State> S_Minus_Y = new ArrayList<State>();
         S_Minus_Y.addAll(_kripke.States);

         for (State state : y)
         {
             if (S_Minus_Y.contains(state))
                 S_Minus_Y.remove(state);
         }

         List<State> PreE_S_Minus_Y = PreE(S_Minus_Y);

         //PreEY - PreE(S-Y)
         for (State state : PreE_S_Minus_Y)
         {
             if (PreEY.contains(state))
                 PreEY.remove(state);
         }

         return PreEY;
	}

	
	//SAT EX
	private List<State> SAT_EX(String exFormula) throws Exception {
		// TODO Auto-generated method stub
		 //X := SAT (φ);
        //Y := pre∃(X);
        //return Y
        List<State> x = new ArrayList<State>();
        List<State> y = new ArrayList<State>();
        x = SAT(exFormula);
        y = PreE(x);
        return y;
	}
	
	
	//isBINARYOP
	private boolean IsBinaryOp(String expression, String symbol) {
		// TODO Auto-generated method stub
		boolean isBinaryOp = false;
        if (expression.contains(symbol))
        {
            int openParanthesisCount = 0;
            int closeParanthesisCount = 0;

            for (int i = 0; i < expression.length(); i++)
            {
                String currentChar = mySubString(expression, i, 1);
                if (currentChar.equals(symbol) && openParanthesisCount == closeParanthesisCount)
                {
                    this.leftExpression = mySubString(expression, 0, i);
                    this.rightExpression = mySubString(expression, i + 1, expression.length() - i - 1);
                    isBinaryOp = true;
                    break;
                }
                else if (currentChar.equals("("))
                {
                    openParanthesisCount++;
                }
                else if (currentChar.equals(")"))
                {
                    closeParanthesisCount++;
                }
            }
        }
        return isBinaryOp;
	}

	
	
	private boolean IsAtomic(String expression) {
		// TODO Auto-generated method stub
		if (_kripke.Atoms.contains(expression))
            return true;
        return false;
	}

	private String RemoveExtraBrackets(String expression) {
		// TODO Auto-generated method stub
		String newExpression = expression;
        int openParanthesis = 0;
        int closeParanthesis = 0;

        if (expression.startsWith("(") && expression.endsWith(")"))
        {
            for (int i = 0; i < expression.length() - 1; i++)
            {
                String charExpression = mySubString(expression, i, 1);

                if (charExpression.equals("("))
                    openParanthesis++;
                else if (charExpression.equals(")"))
                    closeParanthesis++;
            }

            if (openParanthesis - 1 == closeParanthesis)
                newExpression = mySubString(expression, 1, expression.length() - 2);
        }
        return newExpression;
	}
}

