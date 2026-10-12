package FreightFox.StoreandSolveAlgebraicEquation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@SpringBootApplication
@OpenAPIDefinition(servers = @Server(url = "/", description = "Current application host"))
public class StoreandSolveAlgebraicEquationApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreandSolveAlgebraicEquationApplication.class, args);
	}

}
