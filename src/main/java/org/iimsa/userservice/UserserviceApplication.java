package org.iimsa.userservice;

import org.iimsa.common.exception.GlobalExceptionAdvice;
import org.iimsa.config.security.LoginFilter;
import org.iimsa.config.security.SecurityConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@Import({SecurityConfig.class, LoginFilter.class, GlobalExceptionAdvice.class})
@SpringBootApplication
public class UserserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserserviceApplication.class, args);
    }

}
