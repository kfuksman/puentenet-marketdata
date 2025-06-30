package com.puentenet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import com.puentenet.config.ServiceConfig;
import com.puentenet.service.MarketDataScheduler;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(ServiceConfig.class)
public class MarketMonitorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MarketMonitorApplication.class, args);
    }

    @Component
    public static class InstrumentInitializer implements CommandLineRunner {
        
        @Autowired
        private MarketDataScheduler marketDataScheduler;
        
        @Override
        public void run(String... args) throws Exception {
            marketDataScheduler.initializeInstruments();
        }
    }
} 