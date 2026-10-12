package FreightFox.StoreandSolveAlgebraicEquation.repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import FreightFox.StoreandSolveAlgebraicEquation.repository.EquationRepository.StoredExpression;
import FreightFox.StoreandSolveAlgebraicEquation.utility.ExpressionTree;
import org.springframework.stereotype.Repository;

@Repository
public class InMemoryEquationRepository implements EquationRepository {

	private final Map<String, StoredExpression> equations = new LinkedHashMap<>();
	private long nextId = 1;

	@Override
	public synchronized String save(ExpressionTree expressionTree) {
		String equationId = Long.toString(nextId++);
		equations.put(equationId, new StoredExpression(equationId, expressionTree));
		return equationId;
	}

	@Override
	public synchronized List<StoredExpression> findAll() {
		return new ArrayList<>(equations.values());
	}

	@Override
	public synchronized Optional<StoredExpression> findById(String equationId) {
		return Optional.ofNullable(equations.get(equationId));
	}
}
