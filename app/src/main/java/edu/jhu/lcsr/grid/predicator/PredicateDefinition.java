package edu.jhu.lcsr.grid.predicator;

import java.util.ArrayList;

/**
 * Represents a whole class of possible predicates.
 * 
 * @author Chris
 */
public class PredicateDefinition {
	String name; // name of this predicate; "ABOVE", "BELOW", "NEXT-TO", etc.
	int numArgs; // max number of arguments; should match types.size() if set
	
	// stores acceptable argument types
	ArrayList<String> types;
	
	String getString(Predicate instance) {
		if (instance.ofPredicateType(name)) {
			return name + "(" + ")";
		} else {
			return null;
		}
	}

	public String getName() {
		return name;
	}
}
