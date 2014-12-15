package grid.predicator;

import java.util.ArrayList;

/**
 * Predicate class.
 * Represents a single, boolean-valued statement with some arguments.
 * A large number of these will be produced by the Needle Game and used to classify the goals and preconditions of different actions.
 * 
 * @author Chris
 */
public class Predicate {
	
	PredicateDefinition myDef;
	
	ArrayList<String> params;

	public boolean ofPredicateType(String name) {
		return myDef.getName() == name;
	}
	
}
