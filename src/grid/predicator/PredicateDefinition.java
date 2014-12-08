package grid.predicator;

/**
 * Represents a whole class of possible predicates.
 * 
 * @author Chris
 */
public class PredicateDefinition {
	String name;
	int numArgs;
	
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
