package com.cc.test.listener;

/**
 * Created by Administrator on 2019/12/24.
 */
import com.cc.test.TestBase;
import io.qameta.allure.Attachment;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class TestFailListener extends TestListenerAdapter{
    @Override
    public void onTestFailure(ITestResult result) {
        screenshot();
    }

    @Attachment(value = "screen shot",type = "image/png")
    public byte[]  screenshot(){
        byte[] screenshotAs = ((TakesScreenshot) TestBase.driver).getScreenshotAs(OutputType.BYTES);

        return screenshotAs;
    }
}
