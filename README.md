# BTHL-HealthCare Management Platform

![Build Status](https://github.com/davestj/bthl-healthcare/workflows/CI/CD%20Pipeline/badge.svg)
![Java Version](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.0-brightgreen.svg)
![License](https://img.shields.io/badge/License-MIT-blue.svg)

## 📋 Project Overview

BTHL-HealthCare is a comprehensive enterprise healthcare management platform designed to streamline the complex relationships between companies, insurance providers, brokers, and healthcare plans. I built this platform to address the growing need for sophisticated healthcare benefit administration in today's complex insurance landscape.

### 🎯 Mission Statement

I created this platform to simplify healthcare benefit management by providing a unified interface for companies to discover, compare, and manage their employee healthcare benefits while ensuring compliance with healthcare regulations and optimizing cost-effectiveness.

### ✨ Key Features

**For Companies:**
- Comprehensive healthcare portfolio management
- Real-time plan comparison and analysis
- Employee enrollment tracking and reporting
- Cost optimization recommendations
- Compliance monitoring and reporting

**For Insurance Brokers:**
- Client portfolio management dashboard
- Commission tracking and analytics
- License renewal monitoring
- Territory and specialization management
- Performance metrics and reporting

**For Insurance Providers:**
- Plan catalog management
- Network adequacy reporting
- Broker relationship management
- Market analysis and competitive intelligence
- Regulatory compliance tracking

**For System Administrators:**
- Role-based access control (RBAC)
- Comprehensive audit logging
- User lifecycle management
- System configuration and monitoring
- Security compliance oversight

## 🏗️ Technology Stack

### Backend Technologies
- **Java 21**: Latest LTS version with modern language features
- **Spring Boot 3.2.0**: Enterprise-grade application framework
- **Spring Security 6.2**: Comprehensive security framework with JWT authentication
- **Spring Data JPA**: Object-relational mapping and data access abstraction
- **PostgreSQL 15+**: Robust relational database with advanced features
- **Flyway**: Database migration and version control
- **Maven**: Dependency management and build automation

### Frontend Technologies
- **Thymeleaf**: Server-side template engine for dynamic web pages
- **Bootstrap 5**: Modern CSS framework for responsive design
- **Vanilla JavaScript**: ES6+ features for dynamic user interactions
- **CSS Custom Properties**: Modern styling with design system approach

### Development and Operations
- **Docker**: Containerization for consistent deployment environments
- **GitHub Actions**: Continuous integration and deployment automation
- **JaCoCo**: Code coverage analysis and reporting
- **SonarQube**: Code quality analysis and security scanning
- **Nginx**: Reverse proxy and load balancing for production

## 📋 Prerequisites

### Development Environment Requirements

**Java Development Kit:**
```bash
# Verify Java 21 installation
java -version
# Should output: openjdk version "21.0.x"
```

**Apache Maven:**
```bash
# Verify Maven installation
mvn -version
# Should output: Apache Maven 3.9.x or higher
```

**PostgreSQL Database:**
```bash
# Verify PostgreSQL installation
psql --version
# Should output: psql (PostgreSQL) 15.x or higher
```

**Node.js (Optional - for development tools):**
```bash
# Verify Node.js installation
node --version
# Should output: v18.x.x or higher
```

### System Requirements
- **Memory**: Minimum 4GB RAM (8GB recommended for development)
- **Storage**: 10GB free disk space
- **Operating System**:
    - Development: macOS 10.15+, Ubuntu 22.04+, Windows 10+
    - Production: Ubuntu 22.04 LTS, Debian 12, RHEL 8+

## 🚀 Quick Start Guide

### Automated Setup (Recommended)

I've created an automated bootstrap script that handles the complete environment setup:

```bash
# Clone the repository
git clone https://github.com/davestj/bthl-healthcare.git
cd bthl-healthcare

# Run automated setup (Linux/macOS)
chmod +x bootstrap.sh
sudo ./bootstrap.sh

# The script automatically:
# - Installs Java 21, Maven, PostgreSQL, Node.js
# - Creates project directories with proper permissions
# - Configures database with initial schema
# - Sets up development environment variables
```

### Manual Setup

If you prefer manual setup or need to understand each step:

**1. Database Setup:**
```bash
# Create database and user
sudo -u postgres createuser -s davestj
sudo -u postgres createdb bthl_healthcare -O davestj

# Load initial schema
psql -U davestj -d bthl_healthcare -f src/main/resources/db/migration/V1__Initial_Schema.sql
```

**2. Application Configuration:**
```bash
# Copy environment configuration
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml

# Edit configuration for your environment
nano src/main/resources/application-dev.yml
```

**3. Build and Run:**
```bash
# Clean and build project
mvn clean install

# Run application in development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Alternative: Run packaged JAR
java -jar target/bthl-healthcare.jar --spring.profiles.active=dev
```

**4. Verify Installation:**
```bash
# Check application health
curl http://localhost:8330/actuator/health

# Access login page
open http://localhost:8330/login
```

## 📁 Project Structure

```
bthl-healthcare/
├── .github/                          # GitHub workflows and templates
│   ├── workflows/                     # CI/CD pipeline definitions
│   └── ISSUE_TEMPLATE/               # Issue templates for bug reports and features
├── src/main/
│   ├── java/com/bthl/healthcare/
│   │   ├── config/                   # Application configuration classes
│   │   ├── controller/               # REST API and web controllers
│   │   │   ├── AuthController.java   # Authentication endpoints
│   │   │   ├── WebController.java    # Web interface routing
│   │   │   └── api/                  # REST API controllers (planned)
│   │   ├── dto/                      # Data Transfer Objects
│   │   ├── exception/                # Custom exception classes
│   │   ├── model/                    # JPA entity classes
│   │   │   ├── User.java            # User entity with MFA support
│   │   │   ├── Role.java            # RBAC role definition
│   │   │   ├── Company.java         # Client company management
│   │   │   ├── InsuranceProvider.java # Insurance company data
│   │   │   ├── InsuranceBroker.java  # Licensed broker information
│   │   │   ├── InsurancePlan.java    # Healthcare plan details
│   │   │   └── CompanyHealthcarePortfolio.java # Portfolio management
│   │   ├── repository/               # Data access layer interfaces
│   │   ├── security/                 # Security configuration and JWT handling
│   │   └── service/                  # Business logic layer
│   └── resources/
│       ├── db/migration/             # Flyway database migrations
│       ├── static/                   # CSS, JavaScript, images
│       ├── templates/                # Thymeleaf HTML templates
│       └── application.yml           # Application configuration
├── src/test/                         # Test classes and resources
├── docs/                            # Project documentation
├── scripts/                         # Utility scripts for deployment and maintenance
├── docker/                          # Docker configuration files
├── pom.xml                          # Maven project configuration
├── bootstrap.sh                     # Automated environment setup script
└── README.md                        # This file
```

## 🔧 Configuration Guide

### Environment Profiles

I've configured the application to support multiple environments with different settings:

**Development Profile (`dev`):**
- Debug logging enabled
- Hot reload with Spring Boot DevTools
- H2 console available for database inspection
- Relaxed security for easier testing

**Production Profile (`prod`):**
- Optimized logging configuration
- Security hardening enabled
- Performance monitoring active
- Database connection pooling optimized

**Test Profile (`test`):**
- In-memory H2 database for fast testing
- Mock external services
- Comprehensive test data fixtures

### Security Configuration

**JWT Authentication:**
```yaml
bthl:
  healthcare:
    jwt:
      secret: ${JWT_SECRET:your-secret-key-change-in-production}
      expiration: 86400000  # 24 hours
```

**Database Security:**
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bthl_healthcare
    username: ${DB_USERNAME:davestj}
    password: ${DB_PASSWORD:your-secure-password}
```

### Environment Variables

I recommend setting these environment variables for production deployment:

```bash
# Database Configuration
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=bthl_healthcare
export DB_USERNAME=davestj
export DB_PASSWORD=your-secure-password

# Security Configuration
export JWT_SECRET=your-256-bit-secret-key
export SESSION_TIMEOUT_MINUTES=480

# Email Configuration (for notifications)
export SMTP_HOST=your-smtp-server
export SMTP_PORT=587
export SMTP_USERNAME=your-email-username
export SMTP_PASSWORD=your-email-password

# Application Configuration
export SERVER_PORT=8330
export SPRING_PROFILES_ACTIVE=prod
```

## 🔐 Security Features

### Authentication and Authorization

**Multi-Factor Authentication (MFA):**
- TOTP (Time-based One-Time Password) support
- Email-based OTP verification
- Backup code generation and validation

**Role-Based Access Control (RBAC):**
- Hierarchical role system with inheritance
- Dynamic permission assignment
- Fine-grained resource access control

**Session Management:**
- JWT token-based authentication
- Automatic session timeout
- Concurrent session limiting

### Data Protection

**Audit Logging:**
- Comprehensive activity tracking
- Immutable audit trail
- GDPR compliance support

**Input Validation:**
- SQL injection prevention
- XSS protection
- CSRF token validation

**Password Security:**
- Bcrypt hashing with salt
- Password complexity requirements
- Account lockout protection

## 📡 API Documentation

### Authentication Endpoints

**User Registration:**
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john.doe",
  "email": "john.doe@company.com",
  "password": "SecurePassword123!",
  "firstName": "John",
  "lastName": "Doe",
  "userType": "COMPANY_USER"
}
```

**User Login:**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john.doe",
  "password": "SecurePassword123!"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
  "expiresIn": 86400,
  "userType": "COMPANY_USER"
}
```

### Resource Management Endpoints

**Company Management:**
- `GET /api/companies` - List companies with pagination
- `POST /api/companies` - Create new company
- `GET /api/companies/{id}` - Get company details
- `PUT /api/companies/{id}` - Update company information
- `DELETE /api/companies/{id}` - Deactivate company

**Portfolio Management:**
- `GET /api/portfolios` - List portfolios with filtering
- `POST /api/portfolios` - Create new portfolio
- `GET /api/portfolios/{id}/plans` - Get portfolio plans
- `PUT /api/portfolios/{id}` - Update portfolio configuration

## 🧪 Testing Strategy

### Running Tests

**Unit Tests:**
```bash
# Run all unit tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run tests with coverage report
mvn clean test jacoco:report
```

**Integration Tests:**
```bash
# Run integration tests
mvn verify

# Run with test profile
mvn test -Dspring.profiles.active=test
```

### Test Coverage Requirements

I maintain high test coverage standards for healthcare software:
- **Unit Tests**: Minimum 80% code coverage
- **Integration Tests**: All API endpoints tested
- **Security Tests**: Authentication and authorization flows
- **Performance Tests**: Load testing for critical paths

### Test Data Management

**Database Testing:**
- TestContainers for real PostgreSQL testing
- Flyway migrations tested in CI/CD
- Test data fixtures for consistent scenarios

**Security Testing:**
- Authentication flow validation
- Authorization boundary testing
- Input validation and sanitization

## 🚀 Deployment Guide

### Production Deployment

**Docker Deployment:**
```bash
# Build Docker image
docker build -t bthl-healthcare:latest .

# Run with environment variables
docker run -d \
  --name bthl-healthcare \
  -p 8330:8330 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_HOST=your-db-host \
  -e DB_PASSWORD=your-secure-password \
  bthl-healthcare:latest
```

**Traditional Deployment:**
```bash
# Build production JAR
mvn clean package -P prod

# Deploy to server
scp target/bthl-healthcare.jar user@server:/opt/bthl-healthcare/

# Start with systemd service
sudo systemctl start bthl-healthcare
sudo systemctl enable bthl-healthcare
```

### Health Monitoring

**Application Health Checks:**
```bash
# Basic health check
curl http://localhost:8330/actuator/health

# Detailed health information
curl http://localhost:8330/actuator/health/db

# Application metrics
curl http://localhost:8330/actuator/metrics
```

## 🤝 Contributing Guidelines

### Development Workflow

I welcome contributions to the BTHL-HealthCare platform! Please follow these guidelines:

**1. Fork and Clone:**
```bash
# Fork the repository on GitHub
# Clone your fork locally
git clone https://github.com/your-username/bthl-healthcare.git
cd bthl-healthcare
```

**2. Create Feature Branch:**
```bash
# Create and switch to feature branch
git checkout -b feature/your-feature-name
```

**3. Development Standards:**
- Follow Google Java Style Guide
- Write comprehensive unit tests
- Include integration tests for API changes
- Update documentation for new features
- Use conventional commit messages

**4. Commit Guidelines:**
```bash
# Use conventional commit format
git commit -m "feat: add portfolio optimization algorithm"
git commit -m "fix: resolve authentication token expiration issue"
git commit -m "docs: update API documentation for new endpoints"
```

**5. Pull Request Process:**
- Ensure all tests pass locally
- Update README.md if needed
- Request review from maintainers
- Address feedback promptly

### Code Quality Standards

**Java Code Style:**
- Use descriptive variable and method names
- Write self-documenting code with minimal comments
- Follow SOLID principles
- Implement proper error handling

**Database Changes:**
- Create Flyway migrations for schema changes
- Include rollback scripts where applicable
- Test migrations against production-like data

**Security Considerations:**
- Never commit sensitive information
- Validate all user inputs
- Follow OWASP security guidelines
- Document security assumptions

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support and Contact

### Getting Help

**Documentation:**
- [Developer Setup Guide](docs/DEVELOPER.md)
- [Deployment Guide](docs/SETUP.md)
- [API Documentation](docs/API.md)

**Community Support:**
- GitHub Issues for bug reports and feature requests
- GitHub Discussions for questions and community support

**Professional Support:**
- Email: support@bthl-healthcare.com
- Documentation: [Healthcare Platform Wiki](docs/)

### Reporting Security Issues

If you discover a security vulnerability, please send an email to security@bthl-healthcare.com instead of using the public issue tracker. I take security issues seriously and will respond promptly to address any concerns.

## 🏥 Healthcare Compliance

### HIPAA Compliance

This platform is designed with HIPAA compliance considerations:
- Comprehensive audit logging
- Data encryption at rest and in transit
- Access control and user authentication
- Secure data backup and recovery

### SOC 2 Compliance

Security controls implemented for SOC 2 compliance:
- Security monitoring and incident response
- Access management and user provisioning
- Change management procedures
- Vendor management and due diligence

### Data Privacy

Privacy features and considerations:
- GDPR-compliant data handling
- Right to erasure implementation
- Data portability support
- Privacy impact assessment documentation

---

**Last Updated:** 2025-07-17  
**Version:** 1.1.1-alpha  
**Author:** davestj (David St John)  
**Platform Status:** Active Development

*Built with ❤️ for better healthcare benefit management*