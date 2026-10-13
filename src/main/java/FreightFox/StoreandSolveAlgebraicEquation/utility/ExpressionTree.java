package FreightFox.StoreandSolveAlgebraicEquation.utility;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public final class ExpressionTree {

	private final ExpressionNode root;

	private ExpressionTree(ExpressionNode root) {
		this.root = root;
	}

	static ExpressionTree fromRoot(ExpressionNode root) {
		return new ExpressionTree(root);
	}

	public String toInfix() {
		return format(root, 0, false);
	}

	public double evaluate(Map<String, Double> variables) {
		return evaluate(root, variables);
	}

	private static String format(ExpressionNode node, int parentPrecedence, boolean rightChild) {
		if (node.isLeaf()) {
			return node.token();
		}

		int precedence = precedence(node.token());
		String expression;
		if (isUnaryOperator(node.token())) {
			expression = node.token().substring(1) + format(node.left(), precedence, false);
		} else {
			String left = format(node.left(), precedence, false);
			String right = format(node.right(), precedence, true);
			expression = left + " " + node.token() + " " + right;
		}

		boolean needsParentheses = precedence < parentPrecedence;
		if (rightChild && precedence == parentPrecedence
				&& (node.token().equals("-") || node.token().equals("^"))) {
			needsParentheses = true;
		}
		if (!rightChild && node.token().equals("^") && precedence == parentPrecedence) {
			needsParentheses = true;
		}
		return needsParentheses ? "(" + expression + ")" : expression;
	}

	private static double evaluate(ExpressionNode node, Map<String, Double> variables) {
		if (node.isLeaf()) {
			if (EquationParser.isNumber(node.token())) {
				return Double.parseDouble(node.token());
			}
			Double value = variables.get(node.token());
			if (value == null) {
				throw new IllegalArgumentException("A value is required for variable '" + node.token() + "'.");
			}
			if (!Double.isFinite(value)) {
				throw new IllegalArgumentException("Variable '" + node.token() + "' must have a finite value.");
			}
			return value;
		}

		double left = evaluate(node.left(), variables);
		if (node.token().equals("u-")) {
			return -left;
		}
		double right = evaluate(node.right(), variables);
		return switch (node.token()) {
			case "+" -> left + right;
			case "-" -> left - right;
			case "*" -> left * right;
			case "^" -> Math.pow(left, right);
			default -> throw new IllegalArgumentException("Unsupported operator '" + node.token() + "'.");
		};
	}

	private static boolean isUnaryOperator(String token) {
		return token.equals("u-");
	}

	private static int precedence(String operator) {
		return switch (operator) {
			case "+", "-" -> 1;
			case "*" -> 2;
			case "u-" -> 3;
			case "^" -> 4;
			default -> 5;
		};
	}
}
