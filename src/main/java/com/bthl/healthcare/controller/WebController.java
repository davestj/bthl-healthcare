/**
 * File: /var/www/davestj.com/bthl-hc/src/main/java/com/bthl/healthcare/controller/WebController.java
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Main web interface controller for serving Thymeleaf templates and handling web navigation
 * Description: I designed this controller to handle all web-based user interface requests, serving
 *              Thymeleaf templates for the healthcare management platform. I've implemented role-based
 *              navigation and secure access to different dashboard areas.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of WebController with role-based navigation and dashboard access
 * 
 * Git Commit: git commit -m "feat: add WebController for web interface and role-based navigation"
 * 
 * Next Dev Feature: Add breadcrumb navigation and contextual help system
 * TODO: Implement dynamic menu generation based on user permissions
 */

package com.bthl.healthcare.controller;

import com.bthl.healthcare.model.User;
import com.bthl.healthcare.model.enums.UserType;
import com.bthl.healthcare.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * I created this WebController to handle all web-based user interface requests
 * for my healthcare platform. I've implemented role-based navigation and
 * secure access controls for different user types and dashboard areas.
 */
@Controller
public class WebController {

    public static final Logger logger = LoggerFactory.getLogger(WebController.class);

    public final UserService userService;

    /**
     * I create this constructor to inject the user service for user-related operations.
     */
    @Autowired
    public WebController(UserService userService) {
        this.userService = userService;
    }

    /**
     * I handle the home page requests and redirect authenticated users to their dashboard.
     * 
     * @param authentication the current authentication context
     * @param model the Thymeleaf model for template rendering
     * @return the template name or redirect URL
     */
    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        logger.debug("Home page request");

        if (authentication != null && authentication.isAuthenticated()) {
            User user = (User) authentication.getPrincipal();
            logger.debug("Authenticated user accessing home: {}", user.getUsername());
            
            // I redirect authenticated users to their appropriate dashboard
            return "redirect:" + getUserDashboardUrl(user.getUserType());
        }

        // I add basic model attributes for the home page
        model.addAttribute("pageTitle", "BTHL-HealthCare Platform");
        model.addAttribute("pageDescription", "Comprehensive Healthcare Management Platform");
        
        return "index";
    }

    /**
     * I handle login page requests with proper error and logout message handling.
     * 
     * @param error optional error parameter for login failures
     * @param logout optional logout parameter for logout confirmation
     * @param model the Thymeleaf model for template rendering
     * @return the login template name
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       @RequestParam(value = "expired", required = false) String expired,
                       Model model) {
        logger.debug("Login page request - error: {}, logout: {}, expired: {}", error, logout, expired);

        model.addAttribute("pageTitle", "Login - BTHL-HealthCare");

        if (error != null) {
            if ("unauthorized".equals(error)) {
                model.addAttribute("errorMessage", "You must be logged in to access this page.");
            } else {
                model.addAttribute("errorMessage", "Invalid username or password. Please try again.");
            }
        }

        if (logout != null) {
            model.addAttribute("successMessage", "You have been logged out successfully.");
        }

        if (expired != null) {
            model.addAttribute("warningMessage", "Your session has expired. Please log in again.");
        }

        return "auth/login";
    }

    /**
     * I handle registration page requests for new user signup.
     * 
     * @param model the Thymeleaf model for template rendering
     * @return the registration template name
     */
    @GetMapping("/register")
    public String register(Model model) {
        logger.debug("Registration page request");

        model.addAttribute("pageTitle", "Register - BTHL-HealthCare");
        model.addAttribute("userTypes", UserType.values());

        return "auth/register";
    }

    /**
     * I handle the main dashboard redirect based on user type and role.
     * 
     * @param user the authenticated user
     * @param model the Thymeleaf model for template rendering
     * @return redirect to appropriate dashboard
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        logger.debug("Dashboard request for user: {}", user.getUsername());

        // I redirect to the appropriate dashboard based on user type
        String dashboardUrl = getUserDashboardUrl(user.getUserType());
        logger.debug("Redirecting user {} to dashboard: {}", user.getUsername(), dashboardUrl);
        
        return "redirect:" + dashboardUrl;
    }

    /**
     * I handle admin dashboard requests with admin-specific functionality.
     * 
     * @param user the authenticated admin user
     * @param model the Thymeleaf model for template rendering
     * @return the admin dashboard template name
     */
    @GetMapping("/admin/dashboard")
    public String adminDashboard(@AuthenticationPrincipal User user, Model model) {
        logger.debug("Admin dashboard request for user: {}", user.getUsername());

        // I add admin-specific model attributes
        model.addAttribute("pageTitle", "Admin Dashboard - BTHL-HealthCare");
        model.addAttribute("currentUser", user);
        model.addAttribute("userType", "Administrator");
        model.addAttribute("dashboardType", "admin");

        // I can add admin-specific data here
        // model.addAttribute("userCount", userService.getTotalUserCount());
        // model.addAttribute("activeUsers", userService.getActiveUserCount());

        return "dashboard/admin";
    }

    /**
     * I handle broker dashboard requests with broker-specific functionality.
     * 
     * @param user the authenticated broker user
     * @param model the Thymeleaf model for template rendering
     * @return the broker dashboard template name
     */
    @GetMapping("/broker/dashboard")
    public String brokerDashboard(@AuthenticationPrincipal User user, Model model) {
        logger.debug("Broker dashboard request for user: {}", user.getUsername());

        model.addAttribute("pageTitle", "Broker Dashboard - BTHL-HealthCare");
        model.addAttribute("currentUser", user);
        model.addAttribute("userType", "Insurance Broker");
        model.addAttribute("dashboardType", "broker");

        return "dashboard/broker";
    }

    /**
     * I handle provider dashboard requests with provider-specific functionality.
     * 
     * @param user the authenticated provider user
     * @param model the Thymeleaf model for template rendering
     * @return the provider dashboard template name
     */
    @GetMapping("/provider/dashboard")
    public String providerDashboard(@AuthenticationPrincipal User user, Model model) {
        logger.debug("Provider dashboard request for user: {}", user.getUsername());

        model.addAttribute("pageTitle", "Provider Dashboard - BTHL-HealthCare");
        model.addAttribute("currentUser", user);
        model.addAttribute("userType", "Insurance Provider");
        model.addAttribute("dashboardType", "provider");

        return "dashboard/provider";
    }

    /**
     * I handle company user dashboard requests with company-specific functionality.
     * 
     * @param user the authenticated company user
     * @param model the Thymeleaf model for template rendering
     * @return the company dashboard template name
     */
    @GetMapping("/company/dashboard")
    public String companyDashboard(@AuthenticationPrincipal User user, Model model) {
        logger.debug("Company dashboard request for user: {}", user.getUsername());

        model.addAttribute("pageTitle", "Company Dashboard - BTHL-HealthCare");
        model.addAttribute("currentUser", user);
        model.addAttribute("userType", "Company Representative");
        model.addAttribute("dashboardType", "company");

        return "dashboard/company";
    }

    /**
     * I handle access denied page requests for unauthorized access attempts.
     * 
     * @param model the Thymeleaf model for template rendering
     * @return the access denied template name
     */
    @GetMapping("/access-denied")
    public String accessDenied(Model model) {
        logger.warn("Access denied page request");

        model.addAttribute("pageTitle", "Access Denied - BTHL-HealthCare");
        model.addAttribute("errorMessage", "You don't have permission to access this resource.");

        return "error/access-denied";
    }

    /**
     * I handle forgot password page requests.
     * 
     * @param model the Thymeleaf model for template rendering
     * @return the forgot password template name
     */
    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        logger.debug("Forgot password page request");

        model.addAttribute("pageTitle", "Forgot Password - BTHL-HealthCare");

        return "auth/forgot-password";
    }

    /**
     * I handle password reset page requests with token validation.
     * 
     * @param token the password reset token
     * @param model the Thymeleaf model for template rendering
     * @return the password reset template name
     */
    @GetMapping("/reset-password")
    public String resetPassword(@RequestParam(value = "token", required = false) String token, Model model) {
        logger.debug("Password reset page request with token");

        model.addAttribute("pageTitle", "Reset Password - BTHL-HealthCare");

        if (token == null || token.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Invalid or missing reset token.");
            return "auth/forgot-password";
        }

        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    /**
     * I handle user profile page requests for authenticated users.
     * 
     * @param user the authenticated user
     * @param model the Thymeleaf model for template rendering
     * @return the profile template name
     */
    @GetMapping("/profile")
    public String profile(@AuthenticationPrincipal User user, Model model) {
        logger.debug("Profile page request for user: {}", user.getUsername());

        model.addAttribute("pageTitle", "My Profile - BTHL-HealthCare");
        model.addAttribute("currentUser", user);

        return "profile/index";
    }

    // I implement helper methods for navigation and URL generation

    /**
     * I determine the appropriate dashboard URL based on user type.
     * 
     * @param userType the type of user
     * @return the dashboard URL for the user type
     */
    public String getUserDashboardUrl(UserType userType) {
        return switch (userType) {
            case ADMIN -> "/admin/dashboard";
            case BROKER -> "/broker/dashboard";
            case PROVIDER -> "/provider/dashboard";
            case COMPANY_USER -> "/company/dashboard";
        };
    }

    /**
     * I add common model attributes for all authenticated pages.
     * 
     * @param model the Thymeleaf model
     * @param user the authenticated user
     * @param pageTitle the page title
     */
    public void addCommonAttributes(Model model, User user, String pageTitle) {
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("currentUser", user);
        model.addAttribute("userType", user.getUserType().displayName);
        model.addAttribute("applicationName", "BTHL-HealthCare");
        model.addAttribute("currentYear", java.time.Year.now().getValue());
    }
}
