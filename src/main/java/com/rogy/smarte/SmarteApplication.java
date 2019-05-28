package com.rogy.smarte;

import com.rogy.smarte.fsu.FsuListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
@ServletComponentScan
public class SmarteApplication extends SpringBootServletInitializer implements CommandLineRunner {
    private final FsuListener fsuListener;

    @Autowired
    public SmarteApplication(FsuListener fsuListener) {
        this.fsuListener = fsuListener;
    }

    public static void main(String[] args) {
        SpringApplication.run(SmarteApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(SmarteApplication.class);
    }

    @Override
    public void run(String... args) {
        fsuListener.contextInitialized();
        Runtime.getRuntime().addShutdownHook(new Thread(fsuListener::contextDestroyed));
    }
}
