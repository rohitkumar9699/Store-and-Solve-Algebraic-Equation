package FreightFox.StoreandSolveAlgebraicEquation.utility;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

public final class ExpressionTree {

	private final ExpressionNode root;

	private ExpressionTree(ExpressionNode root) {
		this.root = root;
	}

	static ExpressionTree fromPostfix(Iterable<String> postfix) {
		Deque<ExpressionNode> nodes = new ArrayDeque<>();
		for (String token : postfix) {
			if (token.equals("u-")) {
				if (nodes.isEmpty()) {
					throw new IllegalArgumentException("Invalid equation syntax.");
				}
				nodes.push(new ExpressionNode(token, nodes.pop(), null));
			} else if (isBinaryOperator(token)) {
				if (nodes.size() < 2) {
					throw new IllegalArgumentException("Invalid equation syntax.");
				}
				ExpressionNode right = nodes.pop();
				ExpressionNode left = nodes.pop();
				nodes.push(new ExpressionNode(token, left, right));
			} else {
				nodes.push(new ExpressionNode(token, null, null));
			}
		}
		if (nodes.size() != 1) {
			throw new IllegalArgumentException("Invalid equation syntax.");
		}
		return new ExpressionTree(nodes.pop());
	}

	public String toInfix() {
		return format(root, null, false);
	}

	public double evaluate(Map<String, Double> variables) {
		return evaluate(root, variables);
	}

	private static String format(ExpressionNode node, String parentOperator, boolean rightChild) {
		if (node.isLeaf()) {
			return node.token();
		}

		int precedence = precedence(node.token());
		String expression;
		if (isUnaryOperator(node.token())) {
			expression = "-" + format(node.left(), node.token(), false);
		} else {
			String left = format(node.left(), node.token(), false);
			String right = format(node.right(), node.token(), true);
			expression = left + " " + node.token() + " " + right;
		}

		int parentPrecedence = parentOperator == null ? 0 : precedence(parentOperator);
		boolean needsParentheses = precedence < parentPrecedence
				|| precedence == parentPrecedence && needsEqualPrecedenceParentheses(parentOperator, node.token(), rightChild);
		return needsParentheses ? "(" + expression + ")" : expression;
	}

	private static boolean needsEqualPrecedenceParentheses(String parent, String child, boolean rightChild) {
		if (parent == null) {
			return false;
		}
		if (parent.equals("^")) {
			return !rightChild;
		}
		if (!rightChild) {
			return false;
		}
		return parent.equals("-") || parent.equals("/")
				|| parent.equals("+") && child.equals("-")
				|| parent.equals("*") && child.equals("/");
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
			case "/" -> left / right;
			case "^" -> Math.pow(left, right);
			default -> throw new IllegalArgumentException("Unsupported operator '" + node.token() + "'.");
		};
	}

	private static boolean isUnaryOperator(String token) {
		return token.equals("u-");
	}

	private static boolean isBinaryOperator(String token) {
		return "+-*/^".contains(token);
	}

	private static int precedence(String operator) {
		return switch (operator) {
			case "+", "-" -> 1;
			case "*", "/" -> 2;
			case "u-" -> 3;
			case "^" -> 4;
			default -> 5;
		};
	}
}
