package druid.elf.tool;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class ToolApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(ToolApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // 在Spring Boot项目启动成功后打开浏览器并跳转到指定的URL
        String url = "http://localhost:5566/ipinfo/index"; // 替换为你需要的URL
        openBrowser(url);
    }

    private void openBrowser(String url) throws IOException {
        String os = System.getProperty("os.name").toLowerCase();

        // Windows
        if (os.contains("win")) {
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
        }
        // MacOS
        else if (os.contains("mac")) {
            Runtime.getRuntime().exec("open " + url);
        }
        // Linux
        else if (os.contains("nix") || os.contains("nux")) {
            Runtime.getRuntime().exec("xdg-open " + url);
        }
        else {
            throw new UnsupportedOperationException("Unsupported operating system");
        }
    }
}
