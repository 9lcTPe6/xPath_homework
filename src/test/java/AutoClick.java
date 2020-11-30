import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoClick {

    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void initializeDriver() {

        System.setProperty("webdriver.chrome.driver", "webdriver/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, 30, 1000);

        String baseUrl = "https://www.rgs.ru/";

        driver.get(baseUrl);

    }

    @Test
    public void runTest() {
        String mainMenuButton = "//div[@id='main-navbar-collapse']/ol/li/a";
        List<WebElement> menuButtonList = driver.findElements(By.xpath(mainMenuButton));
        if (!menuButtonList.isEmpty()) {
            menuButtonList.get(0).click();
        }


        String dmsButton = "//a[contains(text(),'ДМС')]";
        WebElement dmsButtonClick = driver.findElement(By.xpath(dmsButton));
        dmsButtonClick.click();

        Assert.assertEquals("Заголовок отсутствует/не соответствует требуемому",
                "ДМС 2020 | Рассчитать стоимость добровольного медицинского страхования и оформить ДМС в Росгосстрах", driver.getTitle());

        String sendOffer = "//a[contains(text(),'Отправить заявку')]";
        WebElement sendOfferClick = driver.findElement(By.xpath(sendOffer));
        sendOfferClick.click();

        String expectedTitle = "Заявка на добровольное медицинское страхование";
        String actualTitle = "//b[contains(.,'Заявка на добровольное медицинское страхование')]";
        String fieldXPath = "//input[@name='%s']";

        //Глупая проверка получается, но другого выхода я пока не нашёл
        if (driver.findElement(By.xpath(actualTitle)).toString().contains(expectedTitle)) {
            //Заполнение полей
            //Фамилия и проверка поля
            fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "LastName"))), "Тестов");
//            checkErrorMessageAtField(driver.findElement(By.xpath(String.format(fieldXPath, "LastName"))), "Введите Фамилию");
            //Имя
            fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "FirstName"))), "Тест");
//            checkErrorMessageAtField(driver.findElement(By.xpath(String.format(fieldXPath, "FirstName"))), "Введите Имя");
            //Отчество
            fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "MiddleName"))), "Тестович");
//            checkErrorMessageAtField(driver.findElement(By.xpath(String.format(fieldXPath, "MiddleName"))), "Введите Отчество");
            //Выбираем поле "Регион"
            String selectRegion = "//select[@name='Region']";
            WebElement selReg = driver.findElement(By.xpath(selectRegion));
            selReg.click();
            //Выбираем регион "Москва"
            String selectMoscow = selectRegion + "/option[@value=\"77\"]";
            WebElement selMos = driver.findElement(By.xpath(selectMoscow));
            selMos.click();
            //Проверка поля "Регион"
//            checkErrorMessageAtField(driver.findElement(By.xpath("//select[@name='Region']")), "Введите Регион");
            //Вводим номер телефона
            fillInputField(driver.findElement(By.xpath("(//input[@type='text'])[5]")), "1111111111");
//            checkErrorMessageAtField(driver.findElement(By.xpath("(//input[@type='text'])[5]")), "Введите номер телефона");
            //Email
            fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "Email"))), "qwertyqwerty");
//            checkErrorMessageAtField(driver.findElement(By.xpath(String.format(fieldXPath, "Email"))), "Введите адрес электронной почты");
            //Дата контакта
            fillInputField(driver.findElement(By.xpath(String.format(fieldXPath, "ContactDate"))), "23122020");
//            checkErrorMessageAtField(driver.findElement(By.xpath(String.format(fieldXPath, "ContactDate"))), "Введите дату");
            //Комментарий
            fillInputField(driver.findElement(By.xpath(String.format("//textarea[@name='Comment']"))), "Тест");
            //Выбираем чекбокс "Я согласен с условиями"
            String checkBox = "//input[@type='checkbox']";
            WebElement clickCheckBox = driver.findElement(By.xpath(checkBox));
            clickCheckBox.click();
            //Отправляем данные
            String sendButton = "//button[@id='button-m']";
            WebElement clickSendButton = driver.findElement(By.xpath(sendButton));
            clickSendButton.click();
        } else {
            throw new NoSuchElementException("Данный заголовок не соответствует действительному");
        }

        String errorAlertXPath = "//span[contains(.,'Введите адрес электронной почты')]";
        WebElement errorAlert = driver.findElement(By.xpath(errorAlertXPath));
        scrollToElementJs(errorAlert);
        waitUtilElementToBeVisible(errorAlert);
        Assert.assertEquals("Проверка ошибки у alert на странице не была пройдено",
                "Введите адрес электронной почты", errorAlert.getText());

    }

    private void scrollToElementJs(WebElement element) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) driver;
        javascriptExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void waitUtilElementToBeClickable(WebElement element) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    private void waitUtilElementToBeVisible(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void waitUtilElementToBeVisible(WebElement element) {
        wait.until(ExpectedConditions.visibilityOf(element));
    }

    private void fillInputField(WebElement element, String value) {
        scrollToElementJs(element);
        waitUtilElementToBeClickable(element);
        element.click();
        element.sendKeys(value);
//        Assert.assertEquals("Поле было заполнено некорректно",
//               value, element.getAttribute("value"));
    }

    private void checkErrorMessageAtField(WebElement element, String errorMessage) {
        element = element.findElement(By.xpath("//span[@class=\"validation-error-text\"]"));
        Assert.assertEquals("Проверка ошибки у поля не была пройдена",
                errorMessage, element.getText());
    }

    @After
    public void after() {
        driver.quit();
    }

}

