package FreightFox.StoreandSolveAlgebraicEquation.controller;

import java.util.List;
import java.util.Map;

import FreightFox.StoreandSolveAlgebraicEquation.service.EquationService;
import FreightFox.StoreandSolveAlgebraicEquation.service.EquationService.StoredEquation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/equations")
@Tag(name = "Equations", description = "Store, list, and evaluate algebraic expressions.")
public class EquationController {

	private final EquationService equationService;

	public EquationController(EquationService equationService) {
		this.equationService = equationService;
	}

	@PostMapping("/store")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Store an equation")
	public StoreEquationResponse store(@RequestBody StoreEquationRequest request) {
		if (request == null || request.equation() == null) {
			throw new IllegalArgumentException("Equation must not be null.");
		}
		return new StoreEquationResponse("Equation stored successfully", equationService.store(request.equation()));
	}

	@GetMapping
	@Operation(summary = "List stored equations")
	public EquationListResponse list() {
		return new EquationListResponse(equationService.findAll());
	}

	@PostMapping("/{equationId}/evaluate")
	@Operation(summary = "Evaluate a stored equation")
	public EvaluationResponse evaluate(@PathVariable String equationId,
			@RequestBody(required = false) EvaluationRequest request) {
		StoredEquation equation = equationService.findById(equationId);
		Map<String, Double> variables = request == null ? Map.of() : request.variables();
		double result = equationService.evaluate(equationId, variables);
		if (!Double.isFinite(result)) {
			throw new IllegalArgumentException("Equation result is not a finite number.");
		}
		return new EvaluationResponse(equation.equationId(), equation.equation(),
				variables == null ? Map.of() : variables, result);
	}

	public record StoreEquationRequest(String equation) {
	}

	public record StoreEquationResponse(String message, String equationId) {
	}

	public record EquationListResponse(List<StoredEquation> equations) {
	}

	public record EvaluationRequest(Map<String, Double> variables) {
	}

	public record EvaluationResponse(String equationId, String equation,
			Map<String, Double> variables, double result) {
	}
}
