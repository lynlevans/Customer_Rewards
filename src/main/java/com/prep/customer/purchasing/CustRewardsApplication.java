package com.prep.customer.purchasing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(
        basePackages = {
            "com.prep.customer.purchasing.domain",
            "com.prep.customer.purchasing.repository"
        })
@SpringBootApplication
public class CustRewardsApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustRewardsApplication.class, args);
    }
}
