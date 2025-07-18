/**
 * File: /var/www/davestj.com/bthl-hc/src/main/resources/static/js/main.js
 * Author: davestj (David St John)
 * Date: 2025-07-16
 * Purpose: Enhanced JavaScript functionality for BTHL-HealthCare platform
 * Description: I created this comprehensive JavaScript module to provide modern interactivity,
 *              API integration, form handling, and enhanced user experience throughout
 *              my healthcare management platform.
 * 
 * Changelog:
 * 2025-07-16: Initial creation of main JavaScript module with comprehensive platform functionality
 * 
 * Git Commit: git commit -m "feat: add comprehensive JavaScript module with API integration and UX enhancements"
 * 
 * Next Dev Feature: Add real-time notifications and progressive web app capabilities
 * TODO: Implement service worker for offline functionality and push notifications
 */

class BthlHealthcare {
    constructor() {
        // I initialize the application when DOM is ready
        this.init();
        
        // I store references to commonly used elements
        this.elements = {
            loadingSpinner: document.getElementById('loading-spinner'),
            notificationCount: document.getElementById('notificationCount'),
            csrfToken: document.querySelector('meta[name="_csrf"]')?.getAttribute('content'),
            csrfHeader: document.querySelector('meta[name="_csrf_header"]')?.getAttribute('content')
        };
        
        // I configure API defaults
        this.apiConfig = {
            baseUrl: '/api',
            timeout: 30000,
            retries: 3
        };
        
        // I track application state
        this.state = {
            isAuthenticated: this.checkAuthenticationStatus(),
            currentUser: null,
            notifications: [],
            activeModals: new Set()
        };
    }

    /**
     * I initialize the application with all necessary event listeners and components
     */
    init() {
        console.log('Initializing BTHL-HealthCare platform...');
        
        // I wait for DOM to be fully loaded
        if (document.readyState === 'loading') {
            document.addEventListener('DOMContentLoaded', () => this.onDOMReady());
        } else {
            this.onDOMReady();
        }
    }

    /**
     * I handle DOM ready event and initialize all components
     */
    onDOMReady() {
        console.log('DOM ready - initializing components');
        
        // I hide the loading spinner after page load
        this.hideLoadingSpinner();
        
        // I initialize all components
        this.initializeNavigation();
        this.initializeForms();
        this.initializeModals();
        this.initializeTooltips();
        this.initializeAlerts();
        this.initializeDataTables();
        this.initializeCharts();
        this.loadNotifications();
        
        // I add global event listeners
        this.addGlobalEventListeners();
        
        // I start periodic tasks
        this.startPeriodicTasks();
        
        console.log('BTHL-HealthCare platform initialized successfully');
    }

    /**
     * I manage the loading spinner for better user experience
     */
    showLoadingSpinner() {
        if (this.elements.loadingSpinner) {
            this.elements.loadingSpinner.classList.remove('hidden');
        }
    }

    hideLoadingSpinner() {
        if (this.elements.loadingSpinner) {
            setTimeout(() => {
                this.elements.loadingSpinner.classList.add('hidden');
            }, 500);
        }
    }

    /**
     * I initialize navigation functionality
     */
    initializeNavigation() {
        // I handle active navigation highlighting
        const currentPath = window.location.pathname;
        const navLinks = document.querySelectorAll('.navbar-nav .nav-link');
        
        navLinks.forEach(link => {
            const href = link.getAttribute('href');
            if (href && currentPath.startsWith(href) && href !== '/') {
                link.classList.add('active');
                
                // I also highlight parent dropdown if applicable
                const dropdown = link.closest('.dropdown');
                if (dropdown) {
                    const dropdownToggle = dropdown.querySelector('.dropdown-toggle');
                    if (dropdownToggle) {
                        dropdownToggle.classList.add('active');
                    }
                }
            }
        });

        // I add smooth scrolling for anchor links
        document.querySelectorAll('a[href^="#"]').forEach(anchor => {
            anchor.addEventListener('click', (e) => {
                e.preventDefault();
                const target = document.querySelector(anchor.getAttribute('href'));
                if (target) {
                    target.scrollIntoView({
                        behavior: 'smooth',
                        block: 'start'
                    });
                }
            });
        });
    }

    /**
     * I enhance forms with validation and AJAX submission
     */
    initializeForms() {
        const forms = document.querySelectorAll('form[data-ajax="true"]');
        
        forms.forEach(form => {
            form.addEventListener('submit', (e) => this.handleFormSubmission(e));
        });

        // I add real-time validation
        const inputs = document.querySelectorAll('input, textarea, select');
        inputs.forEach(input => {
            input.addEventListener('blur', () => this.validateField(input));
            input.addEventListener('input', () => this.clearFieldError(input));
        });

        // I add password strength indicator
        const passwordInputs = document.querySelectorAll('input[type="password"]');
        passwordInputs.forEach(input => {
            input.addEventListener('input', () => this.updatePasswordStrength(input));
        });
    }

    /**
     * I handle AJAX form submissions with comprehensive error handling
     */
    async handleFormSubmission(e) {
        e.preventDefault();
        const form = e.target;
        const submitButton = form.querySelector('button[type="submit"]');
        
        try {
            // I disable the submit button to prevent double submission
            if (submitButton) {
                submitButton.disabled = true;
                submitButton.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Processing...';
            }

            // I collect form data
            const formData = new FormData(form);
            const url = form.getAttribute('action') || window.location.href;
            const method = form.getAttribute('method') || 'POST';

            // I make the API request
            const response = await this.makeApiRequest(url, {
                method: method,
                body: formData
            });

            if (response.success) {
                this.showSuccessMessage(response.message || 'Operation completed successfully');
                
                // I handle redirects
                if (response.redirect) {
                    window.location.href = response.redirect;
                    return;
                }
                
                // I reset form if specified
                if (form.dataset.resetOnSuccess === 'true') {
                    form.reset();
                }
            } else {
                this.showErrorMessage(response.error || 'An error occurred');
                this.displayFormErrors(form, response.errors);
            }

        } catch (error) {
            console.error('Form submission error:', error);
            this.showErrorMessage('A network error occurred. Please try again.');
        } finally {
            // I re-enable the submit button
            if (submitButton) {
                submitButton.disabled = false;
                submitButton.innerHTML = submitButton.dataset.originalText || 'Submit';
            }
        }
    }

    /**
     * I validate individual form fields
     */
    validateField(field) {
        const value = field.value.trim();
        const rules = field.dataset.validate ? JSON.parse(field.dataset.validate) : {};
        let isValid = true;
        let errorMessage = '';

        // I check required fields
        if (field.hasAttribute('required') && !value) {
            isValid = false;
            errorMessage = 'This field is required';
        }

        // I validate email fields
        if (field.type === 'email' && value) {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(value)) {
                isValid = false;
                errorMessage = 'Please enter a valid email address';
            }
        }

        // I validate password fields
        if (field.type === 'password' && value) {
            const passwordStrength = this.calculatePasswordStrength(value);
            if (passwordStrength.score < 3) {
                isValid = false;
                errorMessage = 'Password is too weak';
            }
        }

        // I display validation results
        if (isValid) {
            this.markFieldValid(field);
        } else {
            this.markFieldInvalid(field, errorMessage);
        }

        return isValid;
    }

    /**
     * I calculate password strength and provide feedback
     */
    calculatePasswordStrength(password) {
        let score = 0;
        let feedback = [];

        if (password.length >= 12) score++;
        else feedback.push('Use at least 12 characters');

        if (/[a-z]/.test(password)) score++;
        else feedback.push('Add lowercase letters');

        if (/[A-Z]/.test(password)) score++;
        else feedback.push('Add uppercase letters');

        if (/[0-9]/.test(password)) score++;
        else feedback.push('Add numbers');

        if (/[^A-Za-z0-9]/.test(password)) score++;
        else feedback.push('Add special characters');

        const strength = ['Very Weak', 'Weak', 'Fair', 'Good', 'Strong'][score];
        const color = ['#ef4444', '#f59e0b', '#eab308', '#10b981', '#059669'][score];

        return { score, strength, color, feedback };
    }

    /**
     * I update password strength indicator
     */
    updatePasswordStrength(passwordInput) {
        const password = passwordInput.value;
        const strengthIndicator = document.getElementById(passwordInput.id + '-strength');
        
        if (!strengthIndicator) return;

        const strength = this.calculatePasswordStrength(password);
        
        strengthIndicator.innerHTML = `
            <div class="password-strength-bar">
                <div class="strength-fill" style="width: ${(strength.score / 5) * 100}%; background-color: ${strength.color}"></div>
            </div>
            <div class="strength-text" style="color: ${strength.color}">${strength.strength}</div>
            ${strength.feedback.length > 0 ? `<ul class="strength-feedback">${strength.feedback.map(f => `<li>${f}</li>`).join('')}</ul>` : ''}
        `;
    }

    /**
     * I mark form fields as valid or invalid
     */
    markFieldValid(field) {
        field.classList.remove('is-invalid');
        field.classList.add('is-valid');
        this.clearFieldError(field);
    }

    markFieldInvalid(field, message) {
        field.classList.remove('is-valid');
        field.classList.add('is-invalid');
        this.showFieldError(field, message);
    }

    clearFieldError(field) {
        const errorElement = document.getElementById(field.id + '-error');
        if (errorElement) {
            errorElement.remove();
        }
    }

    showFieldError(field, message) {
        this.clearFieldError(field);
        
        const errorElement = document.createElement('div');
        errorElement.id = field.id + '-error';
        errorElement.className = 'invalid-feedback';
        errorElement.textContent = message;
        
        field.parentNode.appendChild(errorElement);
    }

    /**
     * I initialize modal functionality
     */
    initializeModals() {
        const modalTriggers = document.querySelectorAll('[data-bs-toggle="modal"]');
        
        modalTriggers.forEach(trigger => {
            trigger.addEventListener('click', (e) => {
                const modalId = trigger.getAttribute('data-bs-target');
                const modal = document.querySelector(modalId);
                
                if (modal) {
                    this.state.activeModals.add(modalId);
                }
            });
        });

        // I handle modal close events
        document.addEventListener('hidden.bs.modal', (e) => {
            this.state.activeModals.delete('#' + e.target.id);
        });
    }

    /**
     * I initialize tooltips for better UX
     */
    initializeTooltips() {
        const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
        tooltipTriggerList.map(function (tooltipTriggerEl) {
            return new bootstrap.Tooltip(tooltipTriggerEl);
        });
    }

    /**
     * I manage alert messages with auto-dismiss functionality
     */
    initializeAlerts() {
        const alerts = document.querySelectorAll('.alert:not(.alert-persistent)');
        
        alerts.forEach(alert => {
            setTimeout(() => {
                if (alert.parentNode) {
                    alert.style.opacity = '0';
                    alert.style.transform = 'translateY(-10px)';
                    setTimeout(() => alert.remove(), 300);
                }
            }, 5000);
        });
    }

    /**
     * I initialize data tables with enhanced functionality
     */
    initializeDataTables() {
        const tables = document.querySelectorAll('.data-table');
        
        tables.forEach(table => {
            // I add search functionality
            this.addTableSearch(table);
            
            // I add sorting functionality
            this.addTableSorting(table);
            
            // I add pagination if needed
            if (table.dataset.paginate === 'true') {
                this.addTablePagination(table);
            }
        });
    }

    /**
     * I initialize charts and data visualizations
     */
    initializeCharts() {
        const chartElements = document.querySelectorAll('[data-chart]');
        
        chartElements.forEach(element => {
            const chartType = element.dataset.chart;
            const chartData = JSON.parse(element.dataset.chartData || '{}');
            
            this.createChart(element, chartType, chartData);
        });
    }

    /**
     * I make API requests with comprehensive error handling
     */
    async makeApiRequest(url, options = {}) {
        const defaultOptions = {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'X-Requested-With': 'XMLHttpRequest'
            },
            timeout: this.apiConfig.timeout
        };

        // I add CSRF token for non-GET requests
        if (options.method !== 'GET' && this.elements.csrfToken) {
            defaultOptions.headers[this.elements.csrfHeader] = this.elements.csrfToken;
        }

        const finalOptions = { ...defaultOptions, ...options };

        try {
            const response = await fetch(url, finalOptions);
            
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            
            const contentType = response.headers.get('content-type');
            if (contentType && contentType.includes('application/json')) {
                return await response.json();
            } else {
                return { success: true, data: await response.text() };
            }
            
        } catch (error) {
            console.error('API request failed:', error);
            throw error;
        }
    }

    /**
     * I load and display notifications
     */
    async loadNotifications() {
        if (!this.state.isAuthenticated) return;

        try {
            const response = await this.makeApiRequest('/api/notifications');
            
            if (response.success) {
                this.state.notifications = response.data;
                this.updateNotificationCount();
                this.renderNotifications();
            }
        } catch (error) {
            console.error('Failed to load notifications:', error);
        }
    }

    /**
     * I update the notification count badge
     */
    updateNotificationCount() {
        if (this.elements.notificationCount) {
            const unreadCount = this.state.notifications.filter(n => !n.read).length;
            this.elements.notificationCount.textContent = unreadCount;
            this.elements.notificationCount.style.display = unreadCount > 0 ? 'block' : 'none';
        }
    }

    /**
     * I show success messages to users
     */
    showSuccessMessage(message) {
        this.showMessage(message, 'success');
    }

    /**
     * I show error messages to users
     */
    showErrorMessage(message) {
        this.showMessage(message, 'danger');
    }

    /**
     * I show warning messages to users
     */
    showWarningMessage(message) {
        this.showMessage(message, 'warning');
    }

    /**
     * I show info messages to users
     */
    showInfoMessage(message) {
        this.showMessage(message, 'info');
    }

    /**
     * I display messages with consistent styling
     */
    showMessage(message, type = 'info') {
        const alertHtml = `
            <div class="alert alert-${type} alert-dismissible fade show" role="alert">
                <i class="fas fa-${this.getAlertIcon(type)} me-2"></i>
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;

        const alertContainer = document.createElement('div');
        alertContainer.innerHTML = alertHtml;
        
        const mainContent = document.querySelector('.main-content');
        if (mainContent) {
            mainContent.insertBefore(alertContainer.firstElementChild, mainContent.firstElementChild);
        }
    }

    /**
     * I get appropriate icons for different alert types
     */
    getAlertIcon(type) {
        const icons = {
            success: 'check-circle',
            danger: 'exclamation-circle',
            warning: 'exclamation-triangle',
            info: 'info-circle'
        };
        return icons[type] || 'info-circle';
    }

    /**
     * I check if user is authenticated
     */
    checkAuthenticationStatus() {
        return document.body.classList.contains('authenticated') || 
               document.querySelector('.user-profile') !== null;
    }

    /**
     * I add global event listeners
     */
    addGlobalEventListeners() {
        // I handle logout confirmations
        document.addEventListener('click', (e) => {
            if (e.target.matches('[data-confirm]')) {
                const message = e.target.dataset.confirm;
                if (!confirm(message)) {
                    e.preventDefault();
                }
            }
        });

        // I handle keyboard shortcuts
        document.addEventListener('keydown', (e) => {
            // I implement Escape key to close modals
            if (e.key === 'Escape' && this.state.activeModals.size > 0) {
                const lastModal = Array.from(this.state.activeModals).pop();
                const modalElement = document.querySelector(lastModal);
                if (modalElement) {
                    bootstrap.Modal.getInstance(modalElement).hide();
                }
            }
        });

        // I handle online/offline status
        window.addEventListener('online', () => {
            this.showSuccessMessage('Connection restored');
        });

        window.addEventListener('offline', () => {
            this.showWarningMessage('Connection lost. Some features may not work.');
        });
    }

    /**
     * I start periodic tasks for real-time updates
     */
    startPeriodicTasks() {
        // I refresh notifications every 5 minutes
        setInterval(() => {
            this.loadNotifications();
        }, 300000);

        // I check for session expiry every minute
        setInterval(() => {
            this.checkSessionStatus();
        }, 60000);
    }

    /**
     * I check session status to prevent unexpected logouts
     */
    async checkSessionStatus() {
        if (!this.state.isAuthenticated) return;

        try {
            const response = await this.makeApiRequest('/api/auth/status');
            
            if (!response.success || !response.authenticated) {
                this.showWarningMessage('Your session has expired. Please log in again.');
                setTimeout(() => {
                    window.location.href = '/login';
                }, 3000);
            }
        } catch (error) {
            // I silently handle session check errors
            console.warn('Session check failed:', error);
        }
    }
}

// I initialize the application when the script loads
const bthlHealthcare = new BthlHealthcare();

// I expose the instance globally for debugging and external access
window.BthlHealthcare = bthlHealthcare;
