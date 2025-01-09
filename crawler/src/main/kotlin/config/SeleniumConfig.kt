package org.team_alilm.config

import io.github.bonigarcia.wdm.WebDriverManager
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.chrome.ChromeOptions
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Duration

@Configuration
class SeleniumConfig {

    @Bean
    fun webDriver(): WebDriver {
        WebDriverManager.chromedriver().setup()  // WebDriverManager로 ChromeDriver 설정

        //ec2에서도 돌아가야함
        val options = ChromeOptions()
        options.addArguments("--headless")
        options.addArguments("--disable-gpu")
        options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")

        val driver = ChromeDriver(options)

        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1))

        return driver
    }
}

