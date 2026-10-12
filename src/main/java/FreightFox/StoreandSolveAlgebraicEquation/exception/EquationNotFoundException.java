package FreightFox.StoreandSolveAlgebraicEquation.exception;

public class EquationNotFoundException extends RuntimeException {

	public EquationNotFoundException(String equationId) {
		super("Equation '" + equationId + "' was not found.");
	}
}
