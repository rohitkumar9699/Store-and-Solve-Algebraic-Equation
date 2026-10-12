package FreightFox.StoreandSolveAlgebraicEquation.repository;

import java.util.List;
import java.util.Optional;

import FreightFox.StoreandSolveAlgebraicEquation.utility.ExpressionTree;

public interface EquationRepository {

	String save(ExpressionTree expressionTree);

	List<StoredExpression> findAll();

	Optional<StoredExpression> findById(String equationId);

	record StoredExpression(String equationId, ExpressionTree expressionTree) {
	}
}
