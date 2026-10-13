import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Домашняя работа по JUnit")
public class HomeWorkJUnitTests {

    @BeforeEach
    void setUp() {
        Configuration.browser = "chrome";
        Configuration.browserSize = "1920x1080";
        Configuration.timeout = 10000;
    }

    @AfterEach
    void tearDown() {
        closeWebDriver();
    }

    @ParameterizedTest(name = "Поиск: {0}")
    @ValueSource(strings = {"мыло", "гель"})
    @DisplayName("Проверка успешного поиска по названию товара")

    void successfulSearchTest(String value) {
        open("https://oilsoap.ru/");
        $("[name='query']").shouldBe(Condition.visible)
                .setValue(value).pressEnter();
        $$(".product-item, .catalog-item, [class*='product']")
                .findBy(Condition.text(value))
                .shouldBe(Condition.visible);
    }

    @ParameterizedTest(name = "Категория: {0} → заголовок: {1}")
    @CsvSource({
            "'/category/mylo/',              'Мыло'",
            "'/category/ukhod-za-telom/',    'Уход за телом'",
            "'/category/ukhod-za-volosami/', 'Уход за волосами'"
    })
    @DisplayName("Проверка заголовков страниц категорий")
    void testCategoryTitle(String path, String expectedTitle) {
        open("https://oilsoap.ru" + path);
        $("h1").shouldBe(Condition.visible)
                .shouldHave(Condition.text(expectedTitle));
    }

    @ParameterizedTest(name = "Логин: {0} → успех: {2}")
    @CsvFileSource(resources = "/login_data.csv")
    @DisplayName("Проверка авторизации")
    void testLogin(String email, String password, boolean shouldSucceed) {
        open("https://oilsoap.ru/login/");

        $(".js-wa-form-item").shouldBe(Condition.visible);

        $("input[name='login']").setValue(email);
        $("input[name='password']").setValue(password);
        $("input.wa-login-submit").click();

        if (shouldSucceed) {
            $(".js-wa-form-item").shouldNotBe(Condition.visible);
            assertTrue(WebDriverRunner.url().contains("/my/orders/"),
                    "Ожидался редирект в личный кабинет, но URL: " + WebDriverRunner.url());
        } else {
            $(".wa-uncaught-errors").shouldBe(Condition.visible);
        }
    }
}