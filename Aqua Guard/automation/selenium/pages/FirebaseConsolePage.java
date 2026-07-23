package selenium.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class FirebaseConsolePage {
    private WebDriver driver;

    @FindBy(xpath = "//*[contains(text(), 'Authentication')]")
    private WebElement authenticationTab;

    @FindBy(xpath = "//*[contains(text(), 'Firestore Database')]")
    private WebElement firestoreTab;

    @FindBy(id = "search-users-input")
    private WebElement searchUsersInput;

    @FindBy(id = "add-user-btn")
    private WebElement addUserBtn;

    @FindBy(xpath = "//*[contains(text(), 'users')]")
    private WebElement usersCollection;

    @FindBy(xpath = "//*[contains(text(), 'devices')]")
    private WebElement devicesCollection;

    public FirebaseConsolePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void clickAuthenticationTab() {
        authenticationTab.click();
    }

    public void clickFirestoreTab() {
        firestoreTab.click();
    }

    public void searchUser(String email) {
        searchUsersInput.sendKeys(email);
    }

    public void clickAddUser() {
        addUserBtn.click();
    }

    public boolean isUsersCollectionVisible() {
        return usersCollection.isDisplayed();
    }

    public boolean isDevicesCollectionVisible() {
        return devicesCollection.isDisplayed();
    }
}
