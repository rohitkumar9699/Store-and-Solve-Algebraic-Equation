package FreightFox.StoreandSolveAlgebraicEquation.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import FreightFox.StoreandSolveAlgebraicEquation.exception.EquationNotFoundException;
import FreightFox.StoreandSolveAlgebraicEquation.repository.EquationRepository;
import FreightFox.StoreandSolveAlgebraicEquation.repository.EquationRepository.StoredExpression;
import FreightFox.StoreandSolveAlgebraicEquation.utility.EquationParser;
import FreightFox.StoreandSolveAlgebraicEquation.utility.ExpressionTree;

@Service
public class EquationService {

	private final EquationRepository equationRepository;

	public EquationService(EquationRepository equationRepository) {
		this.equationRepository = equationRepository;
	}

	public String store(String equation) {
		ExpressionTree tree = EquationParser.parse(equation);
		return equationRepository.save(tree);
	}

	public List<StoredEquation> findAll() {
		return equationRepository.findAll().stream()
				.map(expression -> toStoredEquation(expression.equationId(), expression.expressionTree()))
				.toList();
	}

	public StoredEquation findById(String equationId) {
		StoredExpression expression = getExpression(equationId);
		return toStoredEquation(expression.equationId(), expression.expressionTree());
	}

	public double evaluate(String equationId, Map<String, Double> variables) {
		return getExpression(equationId).expressionTree().evaluate(variables == null ? Map.of() : variables);
	}

	private StoredExpression getExpression(String equationId) {
		return equationRepository.findById(equationId)
				.orElseThrow(() -> new EquationNotFoundException(equationId));
	}

	private StoredEquation toStoredEquation(String equationId, ExpressionTree tree) {
		return new StoredEquation(equationId, tree.toInfix());
	}

	public record StoredEquation(String equationId, String equation) {
	}
}
