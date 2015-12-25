import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestCommentsPage {
    private WebDriver webDriver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        webDriver = new FirefoxDriver();
        wait = new WebDriverWait(webDriver, 30);


        webDriver.get("http://comments.azurewebsites.net/");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("main")));
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("command-navigation")));
    }

    @After
    public void tearDown() {
        if (webDriver != null)
            webDriver.quit();
    }

    @Test
    public void testAddingNewComment() {
        webDriver.findElement(By.cssSelector("input[value='New...']")).click();
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.id("commentfields")));

        webDriver.findElement(By.id("Text")).sendKeys("My Test Comment");
        webDriver.findElement(By.id("Number")).sendKeys(getRandomNumber("" + System.currentTimeMillis()));

        List<WebElement> categoriesList = webDriver.findElements(By.className("categoryitem"));
        for(WebElement category: categoriesList) {
            if (category.getText().contains("Cat1")) {
                category.findElement(By.id("Categories")).click();
            }
        }

        webDriver.findElement(By.name("CurSelect")).click();
        webDriver.findElement(By.className("buttonAsLink")).click();

        webDriver.findElement(By.linkText("Return")).click();

        webDriver.get("http://comments.azurewebsites.net/?sort=NumberValue&Text=DESC");

        List<WebElement> commentsRows = webDriver.findElement(By.cssSelector("table.webgrid tbody")).findElements(By.tagName("tr"));

        WebElement newCommentRow = null;
        for(WebElement row: commentsRows) {
            if (row.findElement(By.className("textcolumn")).getText().contains("My Test Comment")) {
                newCommentRow = row;
            }
        }

        if (newCommentRow == null) {
            throw new RuntimeException("Searched comment row was not found in the list");
        }

        assertTrue(newCommentRow.getText().contains("My Test Comment"));
        assertTrue(newCommentRow.findElement(By.className("categorycolumn")).getText().contains("Cat1"));
    }

    @Test
    public void testDuplicateComment() {
        wait.until(ExpectedConditions.elementToBeClickable(By.name("SelectedId")));
        webDriver.findElement(By.name("SelectedId")).click();

        String commentName = webDriver.findElement(By.className("textcolumn")).getText();

        webDriver.findElement(By.cssSelector("input[value='Duplicate...']")).click();

        String commentNameAfterDuplicate = webDriver.findElement(By.cssSelector(".commenteditor input")).getAttribute("value");
        assertTrue("Comment name is not as expected", commentNameAfterDuplicate.equalsIgnoreCase("Copy of" + commentName));

        webDriver.findElement(By.id("Number")).clear();
        webDriver.findElement(By.id("Number")).sendKeys(getRandomNumber("" + System.currentTimeMillis()));

        webDriver.findElement(By.className("buttonAsLink")).click();

        webDriver.get("http://comments.azurewebsites.net/?sort=NumberValue&Text=DESC");

        List<WebElement> commentsRows = webDriver.findElement(By.cssSelector("table.webgrid tbody")).findElements(By.tagName("tr"));

        WebElement newCommentRow = null;
        for(WebElement row: commentsRows) {
            if (row.findElement(By.className("textcolumn")).getText().contains(commentNameAfterDuplicate)) {
                newCommentRow = row;
            }
        }

        if (newCommentRow == null) {
            throw new RuntimeException("Searched comment row was not found in the list");
        }

        assertTrue(newCommentRow.getText().contains(commentNameAfterDuplicate));
    }

    @Test
    public void testEditComment() {
        wait.until(ExpectedConditions.elementToBeClickable(By.name("SelectedId")));
        webDriver.findElement(By.name("SelectedId")).click();

        webDriver.findElement(By.cssSelector("input[value='Edit...']")).click();

        String commentNameAfterEdit = "Alena comment";
        webDriver.findElement(By.id("Text")).clear();
        webDriver.findElement(By.id("Text")).sendKeys(commentNameAfterEdit);

        webDriver.findElement(By.id("Number")).clear();
        webDriver.findElement(By.id("Number")).sendKeys(getRandomNumber("" + System.currentTimeMillis()));

        webDriver.findElement(By.className("buttonAsLink")).click();

        webDriver.get("http://comments.azurewebsites.net/");

        String commentNameInTable = webDriver.findElement(By.className("textcolumn")).getText();

        assertTrue(commentNameInTable.equalsIgnoreCase(commentNameAfterEdit));
    }

    @Test
    public void testDeleteComment() {
        String firstComment = webDriver.findElement(By.className("textcolumn")).getText();

        wait.until(ExpectedConditions.elementToBeClickable(By.name("SelectedId")));
        webDriver.findElement(By.name("SelectedId")).click();

        webDriver.findElement(By.cssSelector("input[value='Delete']")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".ui-dialog.ui-widget.ui-widget-content.ui-corner-all.ui-draggable")));

        webDriver.findElement(By.className("ui-dialog-buttonset")).findElement(By.className("ui-button-text")).click();

        String newFirstComment = webDriver.findElement(By.className("textcolumn")).getText();

        assertFalse(newFirstComment.contains(firstComment));
    }

    @Test
    public void testPageRefreshByLink() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("logindisplay")));

        String firstComment = webDriver.findElement(By.className("textcolumn")).getText();

        webDriver.findElement(By.cssSelector("#logindisplay a")).click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("logindisplay")));
        String firstCommentAfterRefresh = webDriver.findElement(By.className("textcolumn")).getText();

        assertTrue(firstCommentAfterRefresh.contains(firstComment));

    }

    @Test
    public void testPageRefreshByWebDriver() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("logindisplay")));

        String firstComment = webDriver.findElement(By.className("textcolumn")).getText();

        webDriver.navigate().refresh();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("logindisplay")));
        String firstCommentAfterRefresh = webDriver.findElement(By.className("textcolumn")).getText();

        assertTrue(firstCommentAfterRefresh.contains(firstComment));
    }

    @Test
    public void testHeaderColor() {
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("logindisplay")));
        String headerColor = webDriver.findElement(By.tagName("header")).getCssValue("color");
        assertTrue(headerColor.contains("rgba(0, 0, 0, 1)"));
    }

    private String getRandomNumber(String number) {
        String randomNum = number.substring(number.length() - 3);
        if (randomNum.substring(0).equals("0"))
            randomNum.substring(0).replace("0", "6");

        return randomNum;
    }

}
