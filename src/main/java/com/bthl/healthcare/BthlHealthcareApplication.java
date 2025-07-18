/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/BthlHealthcareApplication.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Main Spring Boot application entry point for BTHL-HealthCare platform
 * Description: I created this as the primary application class that bootstraps my healthcare management
 *              platform. I've configured it to enable JPA repositories, web MVC, and security features.
 *              I designed this to run on port 8330 with comprehensive auto-configuration.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of Spring Boot main application class with comprehensive configuration
 * 
 * Git Commit: git commit -m "feat: add main Spring Boot application class with JPA and security configuration"
 * 
 * Next Dev Feature: Add application health checks and custom metrics for monitoring
 * TODO: Implement graceful shutdown handling and startup banner customization
 */

package com.bthl.healthcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.Clock;

/**
 * I designed this main application class to serve as the central configuration point
 * for my BTHL-HealthCare platform. I've enabled all necessary Spring Boot features
 * including JPA auditing, async processing, and comprehensive security.
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.bthl.healthcare.repository")
@EntityScan(basePackages = "com.bthl.healthcare.model")
@ComponentScan(basePackages = "com.bthl.healthcare")
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties
public class BthlHealthcareApplication {

    /**
     * I created this main method as the application entry point that starts
     * my Spring Boot healthcare management platform with all configured services.
     * 
     * @param args Command line arguments passed to the application
     */
    public static void main(String[] args) {
        // I customize the startup banner and logging for my application
        SpringApplication application = new SpringApplication(BthlHealthcareApplication.class);
        
        // I set additional startup properties for better control
        application.setAdditionalProfiles("default");
        application.setRegisterShutdownHook(true);
        
        // I start the application with comprehensive logging
        System.out.println("Starting BTHL-HealthCare Application...");
        application.run(args);
        System.out.println("BTHL-HealthCare Application started successfully on port 8330");
    }

    /**
     * I configure BCrypt password encoder as my primary password hashing mechanism
     * for secure user authentication. I chose BCrypt for its adaptive nature and
     * resistance to rainbow table attacks.
     * 
     * @return BCryptPasswordEncoder instance configured with strength 12
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // I use strength 12 for optimal balance between security and performance
        return new BCryptPasswordEncoder(12);
    }

    /**
     * I provide a Clock bean for consistent time handling across my application.
     * This allows me to easily mock time in tests and ensures timezone consistency.
     * 
     * @return System default Clock instance
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    /**
     * I configure CORS settings to allow my frontend to communicate with the backend
     * while maintaining security. I've set specific origins and methods for production safety.
     * 
     * @return WebMvcConfigurer with CORS configuration
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // I configure CORS for API endpoints with specific security restrictions
                registry.addMapping("/api/**")
                    .allowedOriginPatterns("https://davestj.com", "https://*.davestj.com", "http://localhost:*")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
                
                // I allow broader CORS for public endpoints like health checks
                registry.addMapping("/actuator/health")
                    .allowedOriginPatterns("*")
                    .allowedMethods("GET")
                    .allowedHeaders("*")
                    .allowCredentials(false)
                    .maxAge(3600);
            }
        };
    }
}
