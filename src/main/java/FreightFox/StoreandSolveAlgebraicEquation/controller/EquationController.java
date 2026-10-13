package FreightFox.StoreandSolveAlgebraicEquation.controller;

import java.util.List;
import java.util.Map;

import FreightFox.StoreandSolveAlgebraicEquation.service.EquationService;
import FreightFox.StoreandSolveAlgebraicEquation.service.EquationService.StoredEquation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/equations")
@Tag(name = "Equations", description = "Store, list, and evaluate algebraic expressions.")
public class EquationController {

	private final EquationService equationService;

	public EquationController(EquationService equationService) {
		this.equationService = equationService;
	}

	@PostMapping("/store")
	@Operation(summary = "Store an equation")
	public ResponseEntity<StoreEquationResponse> store(@RequestBody StoreEquationRequest request) {
		if (request == null || request.equation() == null) {
			throw new IllegalArgumentException("Equation must not be null.");
		}
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new StoreEquationResponse("Equation stored successfully", equationService.store(request.equation())));
	}

	@GetMapping
	@Operation(summary = "List stored equations")
	public ResponseEntity<EquationListResponse> list() {
		return ResponseEntity.ok(new EquationListResponse(equationService.findAll()));
	}

	@PostMapping("/{equationId}/evaluate")
	@Operation(summary = "Evaluate a stored equation")
	public ResponseEntity<EvaluationResponse> evaluate(@PathVariable String equationId,
			@RequestBody(required = false) EvaluationRequest request) {
		StoredEquation equation = equationService.findById(equationId);
		Map<String, Double> variables = request == null ? Map.of() : request.variables();
		double result = equationService.evaluate(equationId, variables);
		if (!Double.isFinite(result)) {
			throw new IllegalArgumentException("Equation result is not a finite number.");
		}
		return ResponseEntity.ok(new EvaluationResponse(equation.equationId(), equation.equation(),
				variables == null ? Map.of() : variables, result));
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
