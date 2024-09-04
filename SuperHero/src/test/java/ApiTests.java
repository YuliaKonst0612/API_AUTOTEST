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
	void postTest() {
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
		Response response1 = response.extract().response();
		int status = response1.statusCode();
		String responseBody = response1.body().prettyPrint();
		Assertions.assertEquals(200, status, "Статус код не соответствует ожидаемому");
		String actualResponseBody = response1.body().asString();
		String id = response1.jsonPath().getString("id");
		Assertions.assertNotNull(id, "id не должен быть null");
		String actualBirthDate = response1.jsonPath().getString("birthDate");
		assertEquals("2019-02-21", actualBirthDate, "birthDate не соответствует ожидаемому значению");

		String actualCity = response1.jsonPath().getString("city");
		assertEquals("GOTHAM", actualCity, "Город не соответствует ожидаемому");

		String actualFullName = response1.jsonPath().getString("fullName");
		assertEquals("BATMAN", actualFullName, "Полное имя не соответствует ожидаемому");

		String actualGender = response1.jsonPath().getString("gender");
		assertEquals("M", actualGender, "Пол не соответствует ожидаемому");

		String actualMainSkill = response1.jsonPath().getString("mainSkill");
		assertEquals("fight", actualMainSkill, "Основной навык не соответствует ожидаемому");
	}

	@Test
	@DisplayName("Validate required fields")
	void postTest2() {
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
		Response response1 = response.extract().response();
		int status = response1.statusCode();
		//баг: неверный статус код: 403 вместо 400
		Assertions.assertEquals(400, status, "Статус код не соответствует ожидаемому");
		String expectedMessage = "{\n" +
				"    \"message\": \"Incorrect request data\",\n" +
				"    \"code\": \"BAD_REQUEST\"\n" +
				"}";
		String actualMessage = response1.body().asString();
		Assertions.assertEquals(expectedMessage, actualMessage, "Сообщение в теле ответа не соответствует ожиданиям");
	}


	@Test
	@DisplayName("GET all superheroes")
	void getTest1() {
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
	void getTest2() {
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
	void getTest3() {
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
	void putTest1() {
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
	void patchTest1() {
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
	void putTest2() {
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
		//баг:  статус код не соответствует ожидаемому: 400 вместо 404

		String expectedMessage = "{\"message\":\"Superhero with id '132' was not found\",\"code\":\"NOT_FOUND\"}";
		Response response1 = response.extract().response();
		int status = response1.statusCode();
		//баг: статус код не соответсвует ожидаемому. 400 вместо 404
		Assertions.assertEquals(404, status, "Статус код не соответствует ожидаемому");
		String actualMessage = response1.body().asString();
		Assertions.assertEquals(expectedMessage, actualMessage, "Сообщение в теле ответа не соответствует ожиданиям");
	}

	@Test
	@DisplayName("DELETE superhero")
	void deleteTest1() {
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
	void deleteTest2() {
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

