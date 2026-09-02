package cn.edu.hnu.artifactweb;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "cn.edu.hnu")
@MapperScan("cn.edu.hnu.**.mapper")
public class ArtifactWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArtifactWebApplication.class, args);
    }

}
