package com.slpixe.springshorturl;

import org.springframework.boot.SpringApplication;

public class TestSpringShortUrlApplication {

    public static void main(String[] args) {
        SpringApplication.from(SpringShortUrlApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
