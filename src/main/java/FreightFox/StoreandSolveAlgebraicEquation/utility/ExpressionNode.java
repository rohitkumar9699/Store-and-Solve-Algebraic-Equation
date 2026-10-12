package FreightFox.StoreandSolveAlgebraicEquation.utility;

record ExpressionNode(String token, ExpressionNode left, ExpressionNode right) {

	boolean isLeaf() {
		return left == null && right == null;
	}
}
