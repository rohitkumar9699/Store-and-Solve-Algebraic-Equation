package FreightFox.StoreandSolveAlgebraicEquation.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class EquationParser {

	private static final Pattern NUMBER = Pattern.compile("(?:\\d+(?:\\.\\d*)?|\\.\\d+)");
	private final List<String> tokens;
	private int position;

	private EquationParser(String equation) {
		this.tokens = tokenize(equation);
	}

	public static ExpressionTree parse(String equation) {
		if (equation == null || equation.isBlank()) {
			throw new IllegalArgumentException("Equation must not be blank.");
		}
		EquationParser parser = new EquationParser(equation);
		ExpressionNode root = parser.parseExpression();
		if (parser.position != parser.tokens.size()) {
			throw parser.syntaxError();
		}
		return ExpressionTree.fromRoot(root);
	}

	private ExpressionNode parseExpression() {
		ExpressionNode expression = parseTerm();
		while (match("+") || match("-")) {
			String operator = previous();
			expression = new ExpressionNode(operator, expression, parseTerm());
		}
		return expression;
	}

	private ExpressionNode parseTerm() {
		ExpressionNode expression = parseUnary();
		while (match("*")) {
			String operator = previous();
			expression = new ExpressionNode(operator, expression, parseUnary());
		}
		return expression;
	}

	private ExpressionNode parseUnary() {
		if (match("-")) {
			return new ExpressionNode("u-", parseUnary(), null);
		}
		return parsePower();
	}

	private ExpressionNode parsePower() {
		ExpressionNode base = parsePrimary();
		if (match("^")) {
			return new ExpressionNode("^", base, parseUnary());
		}
		return base;
	}

	private ExpressionNode parsePrimary() {
		if (match("(")) {
			ExpressionNode expression = parseExpression();
			if (!match(")")) {
				throw syntaxError();
			}
			return expression;
		}
		if (position >= tokens.size()) {
			throw syntaxError();
		}
		String token = tokens.get(position++);
		if (isOperator(token) || token.equals(")")) {
			throw syntaxError();
		}
		return new ExpressionNode(token, null, null);
	}

	private boolean match(String expected) {
		if (position < tokens.size() && tokens.get(position).equals(expected)) {
			position++;
			return true;
		}
		return false;
	}

	private String previous() {
		return tokens.get(position - 1);
	}

	private IllegalArgumentException syntaxError() {
		return new IllegalArgumentException("Invalid equation syntax near token "
				+ (position < tokens.size() ? "'" + tokens.get(position) + "'" : "at end") + ".");
	}

	private static List<String> tokenize(String equation) {
		List<String> rawTokens = new ArrayList<>();
		for (int i = 0; i < equation.length();) {
			char current = equation.charAt(i);
			if (Character.isWhitespace(current)) {
				i++;
			} else if (Character.isDigit(current) || current == '.') {
				int start = i++;
				while (i < equation.length() && Character.isDigit(equation.charAt(i))) {
					i++;
				}
				if (i < equation.length() && equation.charAt(i) == '.') {
					i++;
					while (i < equation.length() && Character.isDigit(equation.charAt(i))) {
						i++;
					}
				}
				String number = equation.substring(start, i);
				if (!isNumber(number)) {
					throw new IllegalArgumentException("Invalid number in equation.");
				}
				rawTokens.add(number);
			} else if (Character.isLetter(current) || current == '_') {
				int start = i++;
				while (i < equation.length()
						&& (Character.isLetterOrDigit(equation.charAt(i)) || equation.charAt(i) == '_')) {
					i++;
				}
				rawTokens.add(equation.substring(start, i));
			} else if ("+-^()".indexOf(current) >= 0) {
				rawTokens.add(String.valueOf(current));
				i++;
			} else if (current == '*' || current == '/') {
				throw new IllegalArgumentException("Unsupported operator '" + current + "' in equation.");
			} else {
				throw new IllegalArgumentException("Unsupported character '" + current + "' in equation.");
			}
		}
		return addImplicitMultiplication(rawTokens);
	}

	private static List<String> addImplicitMultiplication(List<String> tokens) {
		List<String> expanded = new ArrayList<>();
		for (int i = 0; i < tokens.size(); i++) {
			String current = tokens.get(i);
			expanded.add(current);
			if (i + 1 < tokens.size() && endsOperand(current) && startsOperand(tokens.get(i + 1))) {
				if (isNumber(current) && isNumber(tokens.get(i + 1))) {
					throw new IllegalArgumentException("Missing operator between numbers.");
				}
				expanded.add("*");
			}
		}
		return expanded;
	}

	private static boolean endsOperand(String token) {
		return isNumber(token) || isIdentifier(token) || token.equals(")");
	}

	private static boolean startsOperand(String token) {
		return isNumber(token) || isIdentifier(token) || token.equals("(");
	}

	static boolean isNumber(String token) {
		return NUMBER.matcher(token).matches();
	}

	private static boolean isIdentifier(String token) {
		return token.matches("[A-Za-z_][A-Za-z_0-9]*");
	}

	private static boolean isOperator(String token) {
		return "+-^".contains(token);
	}
}
