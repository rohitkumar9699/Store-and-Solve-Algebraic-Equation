package FreightFox.StoreandSolveAlgebraicEquation.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.Test;

class EquationParserTests {

	@Test
	void parsesAndEvaluatesImplicitMultiplication() {
		ExpressionTree tree = EquationParser.parse("3x + 2y - z");

		assertEquals(11.0, tree.evaluate(Map.of("x", 2.0, "y", 3.0, "z", 1.0)));
		assertEquals("3 * x + 2 * y - z", tree.toInfix());
	}

	@Test
	void respectsParenthesesAndExponentPrecedence() {
		ExpressionTree tree = EquationParser.parse("-(x + 1)^2 + 2^3^2");

		assertEquals(503.0, tree.evaluate(Map.of("x", 2.0)));
	}

	@Test
	void supportsAdjacentParentheses() {
		ExpressionTree tree = EquationParser.parse("2(x + 2)(x - 2)");

		assertEquals(10.0, tree.evaluate(Map.of("x", 3.0)));
	}

	@Test
	void rejectsMalformedExpressions() {
		assertThrows(IllegalArgumentException.class, () -> EquationParser.parse("x + * 2"));
		assertThrows(IllegalArgumentException.class, () -> EquationParser.parse("x + (2"));
		assertThrows(IllegalArgumentException.class, () -> EquationParser.parse("2 3"));
		assertThrows(IllegalArgumentException.class, () -> EquationParser.parse("x & y"));
	}

	@Test
	void reportsMissingVariablesAndUnsupportedOperators() {
		assertThrows(IllegalArgumentException.class,
				() -> EquationParser.parse("x + 1").evaluate(Map.of()));
		assertThrows(IllegalArgumentException.class,
				() -> EquationParser.parse("x / 2"));
		assertThrows(IllegalArgumentException.class,
				() -> EquationParser.parse("x * 2"));
	}
}
