package FreightFox.StoreandSolveAlgebraicEquation.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class EquationControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void storesListsAndEvaluatesAnEquation() throws Exception {
		mockMvc.perform(post("/api/equations/store")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"equation\":\"3x + 2y - z\"}"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.message").value("Equation stored successfully"))
			.andExpect(jsonPath("$.equationId").value("1"));

		mockMvc.perform(get("/api/equations"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.equations[0].equation").value("3 * x + 2 * y - z"));

		mockMvc.perform(post("/api/equations/1/evaluate")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"variables\":{\"x\":2,\"y\":3,\"z\":1}}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.equationId").value("1"))
			.andExpect(jsonPath("$.result").value(11.0));
	}

	@Test
	void returnsClientErrorsForInvalidInputAndUnknownIds() throws Exception {
		mockMvc.perform(post("/api/equations/store")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"equation\":\"x + * 2\"}"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.message").exists());

		mockMvc.perform(post("/api/equations/999/evaluate")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"variables\":{}}"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.message").exists());
	}

	@Test
	void publishesOpenApiDocumentation() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.servers[0].url").value("/"))
			.andExpect(jsonPath("$.paths['/api/equations/store']").exists())
			.andExpect(jsonPath("$.paths['/api/equations/{equationId}/evaluate']").exists());
	}

	@Test
	void servesSwaggerUi() throws Exception {
		mockMvc.perform(get("/swagger-ui.html"))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("/swagger-ui/index.html"));

		mockMvc.perform(get("/swagger-ui/index.html"))
			.andExpect(status().isOk());

		mockMvc.perform(get("/v3/api-docs/swagger-config"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.url").value("/v3/api-docs"));
	}
}
