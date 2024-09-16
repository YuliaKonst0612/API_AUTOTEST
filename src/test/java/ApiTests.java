import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testng.annotations.BeforeClass;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static io.restassured.RestAssured.given;

public class ApiTests {
	private String baseUrl = "https://superhero.qa-test.csssr.com/superheroes";
	@BeforeClass
	public void setUp() {
		RestAssured.baseURI = baseUrl;
	}

	@Test
	@DisplayName("Add superhero with all required fields")
	void postAllRequiredFieldsTest() {
		String body = """
				    {
				        "birthDate": "2019-02-21",
				        "city": "GOTHAM",
				        "fullName": "BATMAN",
				        "gender": "M",
				        "mainSkill": "fight"
				    }
				""";
		ValidatableResponse response =
				given().
						header("accept", "application/json").
						header("Content-Type", "application/json").
						body(body).
						when().
						post(baseUrl).then();
		Response responseBodyCheck = response.extract().response();
		int status = responseBodyCheck.statusCode();
		String responseBody = responseBodyCheck.body().prettyPrint();
		String actualResponseBody = responseBodyCheck.body().asString();
		String id = responseBodyCheck.jsonPath().getString("id");
		Assertions.assertNotNull(id, "id не должен быть null");
		String actualBirthDate = responseBodyCheck.jsonPath().getString("birthDate");
		String actualCity = responseBodyCheck.jsonPath().getString("city");
		String actualFullName = responseBodyCheck.jsonPath().getString("fullName");
		String actualGender = responseBodyCheck.jsonPath().getString("gender");
		String actualMainSkill = responseBodyCheck.jsonPath().getString("mainSkill");

		Assertions.assertEquals(200, status, "Статус код не соответствует ожидаемому");
		assertEquals("2019-02-21", actualBirthDate, "birthDate не соответствует ожидаемому значению");
		assertEquals("GOTHAM", actualCity, "Город не соответствует ожидаемому");
		assertEquals("BATMAN", actualFullName, "Полное имя не соответствует ожидаемому");
		assertEquals("M", actualGender, "Пол не соответствует ожидаемому");
		assertEquals("fight", actualMainSkill, "Основной навык не соответствует ожидаемому");
	}

	@Test
	@DisplayName("Validate required fields")
	void postWithoutRequiredFieldsTest() {
		String body = """
				    {
				        "birthDate": "",
				        "city": "",
				        "fullName": "",
				        "gender": "",
				        "mainSkill": ""
				    }
				""";
		ValidatableResponse response =
				given().
						header("accept", "application/json").
						header("Content-Type", "application/json").
						body(body).
						when().
						post(baseUrl).then();
		Response responseBodyCheck = response.extract().response();
		int status = responseBodyCheck.statusCode();
		String actualMessage = responseBodyCheck.body().asString();
		//баг: неверный статус код: 403 вместо 400
		String expectedMessage = "{\n" +
				"    \"message\": \"Incorrect request data\",\n" +
				"    \"code\": \"BAD_REQUEST\"\n" +
				"}";

		Assertions.assertEquals(400, status, "Статус код не соответствует ожидаемому");
		Assertions.assertEquals(expectedMessage, actualMessage, "Сообщение в теле ответа не соответствует ожиданиям");
	}


	@Test
	@DisplayName("GET all superheroes")
	void getAllSuperheroesTest() {
		given().
				when().
				get(baseUrl).
				then().
				log().
				all().
				statusCode(200).
				assertThat()
				.body(notNullValue()) // Проверка, что тело ответа не null
				.body(not(isEmptyOrNullString())); // Проверка, что тело ответа не пустое
	}

	@Test
	@DisplayName("GET superhero by Id")
	void getSuperheroByIdTest() {
		int id = 3;
		given().
				when().
				get(baseUrl + "/" + id)
				.then().
				log().
				all().
				statusCode(200).
				body("id", equalTo(id));
	}

	@Test
	@DisplayName("GET superhero by not existed id")
	void getSuperheroByNotExistedIdTest() {
		int id = 154;
		given().
				when().
				get(baseUrl + "/" + id)
				.then().
				log().
				all().
				//баг: неверный статус код: 400 вместо 404
						statusCode(404).
				body("id", not(equalTo(id)));
	}

	@Test
	@DisplayName("update superhero all fields")
	void putSuperheroAllFieldsTest() {
		int id = 1;
		String body = """
				    {
				        "birthDate": "2020-02-21",
				        "city": "PARIS",
				        "fullName": "LADY BUG",
				        "gender": "F",
				        "mainSkill": "BUGS"
				    }
				""";

		ValidatableResponse response =
				given()
						.header("accept", "application/json")
						.header("Content-Type", "application/json")
						.body(body)
						.when()
						.put(baseUrl + "/" + id)
						.then()
						.body("id", equalTo(id))
						.statusCode(200);


		response.log().body();
		given()
				.header("accept", "application/json")
				.when()
				.get(baseUrl + "/" + id)
				.then()
				.log().all()
				.statusCode(200)
				.body("city", equalTo("PARIS"))
				.body("fullName", equalTo("LADY BUG"))
				.body("birthDate", equalTo("2020-02-21"))
				.body("gender", equalTo("F"))
				.body("mainSkill", equalTo("BUGS"));
	}

	@Test
	@DisplayName("update superhero one field")
	void patchOneFieldTest() {
		int id = 1;
		String body = """
				    {
				        "birthDate": "2020-02-21",
				        "city": "LONDON",
				        "fullName": "LADY BUG",
				        "gender": "F",
				        "mainSkill": "BUGS"
				    }
				""";
		ValidatableResponse response =
				given()
						.header("accept", "application/json")
						.header("Content-Type", "application/json")
						.body(body)
						.when()
						.patch(baseUrl + "/" + id)
						.then()
						.body("id", equalTo(id))
						.statusCode(200);
		given()
				.header("accept", "application/json")
				.when()
				.get(baseUrl + "/" + id)
				.then()
				.log().all()
				.statusCode(200)
				.body("city", equalTo("LONDON"))
				.body("fullName", equalTo("LADY BUG"))
				.body("birthDate", equalTo("2020-02-21"))
				.body("gender", equalTo("F"))
				.body("mainSkill", equalTo("BUGS"));
	}

	@Test
	@DisplayName("update superhero all fields by not existed id ")
	void putAllFieldsByNotExostedIdTest() {
		int id = 132;
		String body = """
				    {
				        "birthDate": "2020-02-21",
				        "city": "PARIS",
				        "fullName": "LADY BUG",
				        "gender": "F",
				        "mainSkill": "BUGS"
				    }
				""";

		ValidatableResponse response =
				given()
						.header("accept", "application/json")
						.header("Content-Type", "application/json")
						.body(body)
						.when()
						.put(baseUrl + "/" + id)
						.then();

		String expectedMessage = "{\"message\":\"Superhero with id '132' was not found\",\"code\":\"NOT_FOUND\"}";
		Response response1 = response.extract().response();
		String actualMessage = response1.body().asString();
		int status = response1.statusCode();

		//баг: статус код не соответсвует ожидаемому. 400 вместо 404
		Assertions.assertEquals(404, status, "Статус код не соответствует ожидаемому");
		Assertions.assertEquals(expectedMessage, actualMessage, "Сообщение в теле ответа не соответствует ожиданиям");
	}

	@Test
	@DisplayName("DELETE superhero")
	void deleteSuperheroTest() {
		int id = 1;
		ValidatableResponse response =
				given().
						when().
						delete(baseUrl + "/" + id).
						then();
		response.log().body();
		response.statusCode(200);
		given()
				.header("accept", "application/json")
				.when()
				.get(baseUrl + "/" + id)
				.then()
				.log().all()
				.statusCode(400);
	}

	@Test
	@DisplayName("DELETE superhero by not existed id")
	void deleteSuperheroByNotExistedIdTest() {
		int id = 154;
		ValidatableResponse response =
				given().
						when().
						delete(baseUrl + "/" + id).
						then();
		response.log().body();
		//баг: ответ статус код 200 вместо 404
		response.statusCode(404);
	}
}

