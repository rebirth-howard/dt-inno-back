package com.deutschmotors.moduleapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {
        "com.deutschmotors.moduleapi.**",       // API 모듈 패키지
        "com.deutschmotors.modulecommon.**",    // Common 모듈 패키지
        "com.deutschmotors.moduledata.**"       // Data 모듈 패키지
})
public class ModuleApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ModuleApiApplication.class, args);
    }

}
