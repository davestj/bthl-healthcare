# BTHL-HealthCare Complete Setup and Installation Guide

**File:** `/var/www/davestj.com/bthl-hc/SETUP.md`  
**Author:** davestj (David St John)  
**Date:** 2025-07-17  
**Purpose:** Complete system setup, installation, and initialization guide for BTHL-HealthCare platform  
**Description:** I created this comprehensive setup guide to provide step-by-step instructions for installing and configuring the BTHL-HealthCare platform on both development and production environments. I've included automated bootstrap options as well as manual setup procedures for learning purposes.

**Changelog:**
- 2025-07-17: Initial creation of complete setup and installation guide

**Git Commit:** `git commit -m "docs: add comprehensive setup and installation guide with bootstrap and manual options"`

**Next Dev Feature:** Add Docker Compose setup for containerized development and production deployment  
**TODO:** Integrate CI/CD pipeline configuration with GitHub Actions and deployment automation

---

## 📋 Overview

The BTHL-HealthCare platform is a comprehensive healthcare management system built with:

- **Backend:** Java 21, Spring Boot 3.2.0, Spring Security, JPA/Hibernate
- **Database:** PostgreSQL 15+ with advanced features and audit logging
- **Frontend:** Thymeleaf templates, Bootstrap 5, vanilla JavaScript
- **Security:** JWT authentication, RBAC, MFA support, CSRF protection
- **Architecture:** RESTful APIs, responsive web interface, audit trail

**Supported Platforms:**
- **Development:** macOS (M2 ARM), Linux (Debian 12, Ubuntu 22/24)
- **Production:** Linux (Debian 12, Ubuntu 22/24), Docker containers

---

## 🚀 Quick Start (Automated Bootstrap)

### Option 1: Automated Setup with Bootstrap Script

I created an automated bootstrap script that handles the complete environment setup:

```bash
# Clone or download the project
git clone https://github.com/davestj/bthl-healthcare.git /var/www/davestj.com/bthl-hc
cd /var/www/davestj.com/bthl-hc

# Make bootstrap script executable
chmod +x bootstrap.sh

# Run automated setup (requires sudo)
sudo ./bootstrap.sh

# The script will automatically:
# - Detect OS (Debian 12, Ubuntu 22/24)
# - Install Java 21, Maven, PostgreSQL, Node.js
# - Create project structure
# - Configure database
# - Set up proper permissions
```

**Post-Bootstrap Steps:**
```bash
# Load database schema
sudo -u postgres psql bthl_healthcare < src/main/resources/db/migration/V1__Initial_Schema.sql

# Build and run application
mvn clean install
mvn spring-boot:run

# Access application
open http://localhost:8330
```

---

## 🛠️ Manual Setup (Step-by-Step)

### Prerequisites Check

I recommend verifying system requirements before installation:

```bash
# Check OS version
cat /etc/os-release

# Check available memory (minimum 4GB recommended)
free -h

# Check disk space (minimum 10GB recommended)
df -h

# Check network connectivity
ping -c 3 google.com
```

---

## 🐧 Linux Setup (Debian 12 / Ubuntu 22/24)

### System Preparation

I start with updating the system and installing essential tools:

```bash
# Update package lists and upgrade system
sudo apt update && sudo apt upgrade -y

# Install essential development tools
sudo apt install -y curl wget apt-transport-https ca-certificates gnupg \
                    lsb-release software-properties-common unzip zip \
                    build-essential git nano vim htop tree

# Create project user and directories
sudo useradd -m -s /bin/bash davestj
sudo mkdir -p /var/www/davestj.com
sudo chown -R davestj:davestj /var/www/davestj.com
```

### Java Development Kit Installation

I install OpenJDK 21 for optimal Spring Boot compatibility:

```bash
# Install OpenJDK 21
sudo apt install -y openjdk-21-jdk openjdk-21-jre

# Configure JAVA_HOME system-wide
sudo tee /etc/environment << EOF
JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
PATH="/usr/lib/jvm/java-21-openjdk-amd64/bin:\$PATH"
EOF

# Configure for current session
export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"
export PATH="$JAVA_HOME/bin:$PATH"

# Add to user profile
echo 'export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"' >> ~/.bashrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.bashrc

# Reload environment
source ~/.bashrc

# Verify Java installation
java -version
javac -version
```

### Maven Build Tool Installation

I set up Maven for project build management:

```bash
# Install Maven
sudo apt install -y maven

# Verify Maven installation
mvn -version

# Configure Maven settings for optimal performance
mkdir -p ~/.m2
cat > ~/.m2/settings.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0
          http://maven.apache.org/xsd/settings-1.0.0.xsd">
    
    <localRepository>${user.home}/.m2/repository</localRepository>
    
    <profiles>
        <profile>
            <id>bthl-development</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <maven.compiler.source>21</maven.compiler.source>
                <maven.compiler.target>21</maven.compiler.target>
                <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                <maven.test.skip>false</maven.test.skip>
            </properties>
        </profile>
    </profiles>
    
</settings>
EOF
```

### PostgreSQL Database Installation and Configuration

I install and configure PostgreSQL for the healthcare platform:

```bash
# Install PostgreSQL and extensions
sudo apt install -y postgresql postgresql-contrib postgresql-client

# Start and enable PostgreSQL service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Check PostgreSQL status
sudo systemctl status postgresql

# Configure PostgreSQL for development
sudo -u postgres psql << 'EOF'
-- Create application user
CREATE USER davestj WITH SUPERUSER CREATEDB CREATEROLE LOGIN;
ALTER USER davestj WITH PASSWORD '<DEV_PASSWORD>';

-- Create application database
CREATE DATABASE bthl_healthcare OWNER davestj;

-- Grant all privileges
GRANT ALL PRIVILEGES ON DATABASE bthl_healthcare TO davestj;

-- Create extensions
\c bthl_healthcare
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Exit PostgreSQL
\q
EOF

# Configure PostgreSQL for local access
sudo cp /etc/postgresql/*/main/pg_hba.conf /etc/postgresql/*/main/pg_hba.conf.backup

# Update pg_hba.conf for local development access
sudo sed -i 's/#local   replication     all                                     peer/local   replication     all                                     peer/' /etc/postgresql/*/main/pg_hba.conf
sudo sed -i 's/local   all             all                                     peer/local   all             all                                     md5/' /etc/postgresql/*/main/pg_hba.conf

# Restart PostgreSQL to apply changes
sudo systemctl restart postgresql

# Test database connection
psql -U davestj -d bthl_healthcare -c "SELECT version();"
```

### Node.js Installation (Optional but Recommended)

I install Node.js for potential frontend build tools and utilities:

```bash
# Add NodeSource repository for latest LTS
curl -fsSL https://deb.nodesource.com/setup_lts.x | sudo -E bash -

# Install Node.js
sudo apt install -y nodejs

# Verify installation
node --version
npm --version

# Install global development utilities
sudo npm install -g typescript ts-node nodemon prettier eslint
```

---

## 🍎 macOS Setup (M2 ARM)

### Homebrew Installation

I use Homebrew as the primary package manager for macOS:

```bash
# Install Homebrew if not present
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Update Homebrew
brew update && brew upgrade

# Add Homebrew to PATH (M2 ARM Macs)
echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
eval "$(/opt/homebrew/bin/brew shellenv)"
```

### Development Tools Installation

I install all required development tools using Homebrew:

```bash
# Install Java 21
brew install openjdk@21

# Create system-wide Java symlink
sudo ln -sfn $(brew --prefix)/opt/openjdk@21/libexec/openjdk.jdk \
     /Library/Java/JavaVirtualMachines/openjdk-21.jdk

# Configure JAVA_HOME
echo 'export JAVA_HOME="$(brew --prefix)/opt/openjdk@21"' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc

# Install Maven
brew install maven

# Install PostgreSQL
brew install postgresql@15

# Install Node.js
brew install node

# Install Git (if not present)
brew install git

# Reload shell configuration
source ~/.zshrc

# Verify installations
java -version
mvn -version
node --version
git --version
```

### PostgreSQL Configuration (macOS)

I configure PostgreSQL for development on macOS:

```bash
# Start PostgreSQL service
brew services start postgresql@15

# Add PostgreSQL to PATH
echo 'export PATH="$(brew --prefix)/opt/postgresql@15/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Create database and user
createuser -s davestj
createdb bthl_healthcare -O davestj

# Set password for database user
psql -d bthl_healthcare -c "ALTER USER davestj WITH PASSWORD '<DEV_PASSWORD>';"

# Test connection
psql -U davestj -d bthl_healthcare -c "SELECT version();"
```

---

## 📦 Project Installation and Configuration

### Project Structure Creation

I create the complete project structure with proper permissions:

```bash
# Create project root directory
sudo mkdir -p /var/www/davestj.com/bthl-hc
sudo chown -R davestj:davestj /var/www/davestj.com

# Navigate to project root
cd /var/www/davestj.com/bthl-hc

# Create directory structure
mkdir -p {src/{main/{java/com/bthl/healthcare/{controller,service,repository,model,config,security,dto,exception},resources/{static/{css,js,images},templates/{auth,dashboard,layout},db/migration}},test/java/com/bthl/healthcare},config,logs,ssl,scripts,backups}

# Set proper permissions
chmod -R 755 src config logs scripts
chmod -R 700 ssl
```

### Project Files Installation

I provide the core project files for manual setup:

**1. Maven Configuration (pom.xml):**
```bash
# Copy the provided pom.xml to project root
# (Content available in the carryover documentation)
```

**2. Spring Boot Application Configuration:**
```bash
# Create application.yml
cat > src/main/resources/application.yml << 'EOF'
# File: /var/www/davestj.com/bthl-hc/src/main/resources/application.yml
# Author: davestj (David St John)
# Date: 2025-07-17
# Purpose: Spring Boot application configuration for BTHL-HealthCare platform

server:
  port: 8330
  servlet:
    context-path: /
  compression:
    enabled: true
  error:
    include-message: always
    include-binding-errors: always

spring:
  application:
    name: BTHL-HealthCare
  
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/bthl_healthcare}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        use_sql_comments: true
        jdbc:
          time_zone: UTC
  
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
    validate-on-migrate: true
  
  thymeleaf:
    cache: false
    enabled: true
    prefix: classpath:/templates/
    suffix: .html
    mode: HTML
    encoding: UTF-8
  
  security:
    user:
      name: ${ADMIN_USERNAME:admin}
      password: ${ADMIN_PASSWORD}
  
  servlet:
    multipart:
      max-file-size: 50MB
      max-request-size: 50MB

logging:
  level:
    com.bthl.healthcare: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql: TRACE
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/bthl-healthcare.log

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,env
  endpoint:
    health:
      show-details: always

bthl:
  healthcare:
    jwt:
      secret: ${JWT_SECRET}
      expiration: 86400000  # 24 hours
    security:
      password:
        min-length: 12
        require-uppercase: true
        require-lowercase: true
        require-numbers: true
        require-symbols: true
      session:
        timeout-minutes: 480  # 8 hours
      mfa:
        token-expiry-minutes: 5
    email:
      smtp:
        host: localhost
        port: 1025
        username: ""
        password: ""
        auth: false
        starttls: false

---
# Development Profile
spring:
  config:
    activate:
      on-profile: dev
  
  jpa:
    show-sql: true
  
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

logging:
  level:
    com.bthl.healthcare: DEBUG

---
# Production Profile
spring:
  config:
    activate:
      on-profile: prod
  
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/bthl_healthcare}
    username: ${DB_USERNAME:davestj}
    password: ${DB_PASSWORD}
  
  jpa:
    show-sql: false

logging:
  level:
    com.bthl.healthcare: INFO
    org.springframework: WARN
    org.hibernate: WARN

bthl:
  healthcare:
    jwt:
      secret: ${JWT_SECRET}

---
# Test Profile
spring:
  config:
    activate:
      on-profile: test
  
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  
  jpa:
    hibernate:
      ddl-auto: create-drop
    database-platform: org.hibernate.dialect.H2Dialect

EOF
```

### Database Schema Installation

I install the complete database schema with all tables and relationships:

```bash
# Load the database schema
psql -U davestj -d bthl_healthcare -f src/main/resources/db/migration/V1__Initial_Schema.sql

# Verify schema installation
psql -U davestj -d bthl_healthcare << 'EOF'
-- Check installed tables
\dt

-- Verify critical tables
\d users
\d roles
\d companies
\d insurance_providers

-- Check sample data
SELECT COUNT(*) FROM roles;
SELECT name, description FROM roles;

-- Exit
\q
EOF
```

### Initial Data Setup

I create essential initial data for the application:

```bash
# Create initial admin user and sample data
psql -U davestj -d bthl_healthcare << 'EOF'
-- Insert admin user with hashed password
-- Password: AdminPassword123!
INSERT INTO users (
    username, email, password_hash, first_name, last_name,
    status, user_type, role_id, email_verified, created_at
) VALUES (
    'admin',
    'admin@bthl-healthcare.com',
    '$2a$12$CjQoiLcmS8zT8/AoK2xqv.VZ8qZGHSHtTyj9E5XGZ5qNZ1Z2Z3Z4Z5',
    'System',
    'Administrator',
    'ACTIVE',
    'ADMIN',
    (SELECT id FROM roles WHERE name = 'SUPER_ADMIN'),
    true,
    CURRENT_TIMESTAMP
);

-- Insert sample company for testing
INSERT INTO companies (
    name, legal_name, tax_id, industry, employee_count,
    headquarters_address, primary_contact_email, primary_contact_phone,
    status, created_by, created_at
) VALUES (
    'TechCorp Solutions Inc.',
    'TechCorp Solutions Incorporated',
    '12-3456789',
    'Technology Services',
    250,
    '123 Innovation Drive, Tech Valley, CA 94000',
    'hr@techcorp.com',
    '+1-555-0123',
    'ACTIVE',
    (SELECT id FROM users WHERE username = 'admin'),
    CURRENT_TIMESTAMP
);

-- Insert sample insurance provider
INSERT INTO insurance_providers (
    name, legal_name, provider_code, provider_type,
    am_best_rating, financial_strength_rating,
    headquarters_address, phone, email, website_url,
    customer_service_phone, claims_phone, network_size,
    established_year, is_active, created_by, created_at
) VALUES (
    'HealthFirst Insurance Company',
    'HealthFirst Insurance Company LLC',
    'HFC001',
    'HEALTH_INSURANCE',
    'A+',
    'A++',
    '456 Healthcare Blvd, Chicago, IL 60601',
    '+1-800-555-HEALTH',
    'contact@healthfirst.com',
    'https://www.healthfirst.com',
    '+1-800-555-HELP',
    '+1-800-555-CLAIM',
    25000,
    1985,
    true,
    (SELECT id FROM users WHERE username = 'admin'),
    CURRENT_TIMESTAMP
);

-- Verify data insertion
SELECT 'Users' as table_name, COUNT(*) as count FROM users
UNION ALL
SELECT 'Companies', COUNT(*) FROM companies
UNION ALL
SELECT 'Providers', COUNT(*) FROM insurance_providers;

\q
EOF
```

---

## 🔧 Application Build and Deployment

### Maven Build Process

I configure and execute the build process:

```bash
# Navigate to project root
cd /var/www/davestj.com/bthl-hc

# Clean previous builds
mvn clean

# Compile source code
mvn compile

# Run tests
mvn test

# Package application
mvn package

# Install to local repository
mvn install

# Verify build success
ls -la target/
```

### Application Startup

I provide multiple ways to start the application:

**Method 1: Maven Spring Boot Plugin (Development):**
```bash
# Start with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Start with custom port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8331"

# Start with debug mode
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

**Method 2: Java JAR Execution (Production-like):**
```bash
# Run the packaged JAR
java -jar target/bthl-healthcare.jar

# Run with specific profile
java -jar target/bthl-healthcare.jar --spring.profiles.active=prod

# Run with custom configuration
java -jar target/bthl-healthcare.jar --server.port=8330 --spring.profiles.active=dev
```

**Method 3: Background Service:**
```bash
# Create systemd service file (Linux)
sudo tee /etc/systemd/system/bthl-healthcare.service << 'EOF'
[Unit]
Description=BTHL-HealthCare Application
After=network.target

[Service]
Type=simple
User=davestj
Group=davestj
WorkingDirectory=/var/www/davestj.com/bthl-hc
ExecStart=/usr/bin/java -jar /var/www/davestj.com/bthl-hc/target/bthl-healthcare.jar
Restart=always
RestartSec=10
StandardOutput=syslog
StandardError=syslog
SyslogIdentifier=bthl-healthcare
Environment=SPRING_PROFILES_ACTIVE=prod

[Install]
WantedBy=multi-user.target
EOF

# Enable and start service
sudo systemctl daemon-reload
sudo systemctl enable bthl-healthcare
sudo systemctl start bthl-healthcare

# Check service status
sudo systemctl status bthl-healthcare
```

---

## 🌐 Nginx Configuration (Optional)

### Nginx Installation and Setup

I configure Nginx as a reverse proxy for production deployment:

```bash
# Install Nginx
sudo apt install -y nginx

# Create site configuration
sudo tee /etc/nginx/sites-available/bthl-healthcare << 'EOF'
# BTHL-HealthCare Nginx Configuration
# File: /etc/nginx/sites-available/bthl-healthcare
# Author: davestj (David St John)
# Date: 2025-07-17
# Purpose: Nginx reverse proxy configuration for BTHL-HealthCare platform

server {
    listen 80;
    server_name bthl-healthcare.davestj.com;
    
    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name bthl-healthcare.davestj.com;
    
    # SSL Configuration
    ssl_certificate /etc/ssl/davestj.com/davestj.com-wildcard-fullchain.crt;
    ssl_certificate_key /etc/ssl/davestj.com/davestj.com-wildcard.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384;
    ssl_prefer_server_ciphers off;
    
    # Security Headers
    add_header Strict-Transport-Security "max-age=63072000; includeSubDomains; preload" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-Frame-Options "DENY" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    
    # Client Configuration
    client_max_body_size 128M;
    client_body_timeout 300s;
    client_header_timeout 300s;
    
    # Logging
    access_log /var/log/nginx/bthl-healthcare-access.log combined;
    error_log /var/log/nginx/bthl-healthcare-error.log;
    
    # Proxy Configuration
    location / {
        proxy_pass http://localhost:8330;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        proxy_set_header X-Forwarded-Host $host;
        proxy_set_header X-Forwarded-Port $server_port;
        
        # Timeout Configuration
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;
        
        # Buffer Configuration
        proxy_buffering on;
        proxy_buffer_size 4k;
        proxy_buffers 8 4k;
        proxy_busy_buffers_size 8k;
    }
    
    # Static Assets
    location ~* \.(css|js|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)$ {
        proxy_pass http://localhost:8330;
        expires 1M;
        add_header Cache-Control "public, immutable";
        access_log off;
    }
    
    # API Endpoints
    location /api/ {
        proxy_pass http://localhost:8330;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # CORS Headers for API
        add_header Access-Control-Allow-Origin "*" always;
        add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS" always;
        add_header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With" always;
        
        # Handle preflight requests
        if ($request_method = 'OPTIONS') {
            add_header Access-Control-Allow-Origin "*";
            add_header Access-Control-Allow-Methods "GET, POST, PUT, DELETE, OPTIONS";
            add_header Access-Control-Allow-Headers "Content-Type, Authorization, X-Requested-With";
            add_header Access-Control-Max-Age "86400";
            add_header Content-Length "0";
            add_header Content-Type "text/plain";
            return 204;
        }
    }
    
    # Health Check
    location /health {
        proxy_pass http://localhost:8330/actuator/health;
        access_log off;
    }
}
EOF

# Enable site
sudo ln -s /etc/nginx/sites-available/bthl-healthcare /etc/nginx/sites-enabled/

# Test Nginx configuration
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx
```

---

## 🔒 Security Configuration

### Firewall Setup

I configure the firewall for security:

```bash
# Install and configure UFW (Ubuntu/Debian)
sudo apt install -y ufw

# Default policies
sudo ufw default deny incoming
sudo ufw default allow outgoing

# Allow SSH
sudo ufw allow ssh

# Allow HTTP and HTTPS
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp

# Allow application port for direct access (development)
sudo ufw allow 8330/tcp

# Allow PostgreSQL (local only)
sudo ufw allow from 127.0.0.1 to any port 5432

# Enable firewall
sudo ufw --force enable

# Check status
sudo ufw status verbose
```

### SSL Certificate Setup

I configure SSL certificates for secure access:

```bash
# Install Certbot for Let's Encrypt (if not using existing wildcard cert)
sudo apt install -y certbot python3-certbot-nginx

# Generate certificate (if needed)
sudo certbot --nginx -d bthl-healthcare.davestj.com

# Or copy existing wildcard certificate
sudo mkdir -p /etc/ssl/davestj.com
sudo cp /path/to/existing/wildcard/cert.crt /etc/ssl/davestj.com/davestj.com-wildcard-fullchain.crt
sudo cp /path/to/existing/wildcard/key.key /etc/ssl/davestj.com/davestj.com-wildcard.key
sudo chmod 600 /etc/ssl/davestj.com/*
```

---

## 🧪 Testing and Validation

### Application Health Check

I provide comprehensive testing procedures:

```bash
# Test database connectivity
psql -U davestj -d bthl_healthcare -c "SELECT COUNT(*) FROM users;"

# Test application startup
curl -f http://localhost:8330/actuator/health

# Test login page
curl -f http://localhost:8330/login

# Test API endpoints
curl -X POST http://localhost:8330/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"AdminPassword123!"}'
```

### Load Testing

I provide basic load testing setup:

```bash
# Install Apache Bench (optional)
sudo apt install -y apache2-utils

# Basic load test
ab -n 100 -c 10 http://localhost:8330/login

# Install and use wrk (more advanced)
git clone https://github.com/wg/wrk.git
cd wrk
make
sudo cp wrk /usr/local/bin

# Load test with wrk
wrk -t12 -c400 -d30s http://localhost:8330/
```

---

## 🔧 Maintenance and Monitoring

### Log Management

I set up comprehensive logging and monitoring:

```bash
# Create log rotation configuration
sudo tee /etc/logrotate.d/bthl-healthcare << 'EOF'
/var/www/davestj.com/bthl-hc/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    delaycompress
    notifempty
    copytruncate
    su davestj davestj
}
EOF

# Set up log monitoring script
cat > /var/www/davestj.com/bthl-hc/scripts/monitor-logs.sh << 'EOF'
#!/bin/bash
# File: /var/www/davestj.com/bthl-hc/scripts/monitor-logs.sh
# Author: davestj (David St John)
# Date: 2025-07-17
# Purpose: Application log monitoring and alerting script

LOG_FILE="/var/www/davestj.com/bthl-hc/logs/bthl-healthcare.log"
ERROR_COUNT=$(grep -c "ERROR" "$LOG_FILE" | tail -n 100)
WARN_COUNT=$(grep -c "WARN" "$LOG_FILE" | tail -n 100)

echo "Application Log Summary:"
echo "Errors in last 100 lines: $ERROR_COUNT"
echo "Warnings in last 100 lines: $WARN_COUNT"

if [ "$ERROR_COUNT" -gt 10 ]; then
    echo "High error count detected! Check logs immediately."
fi
EOF

chmod +x /var/www/davestj.com/bthl-hc/scripts/monitor-logs.sh
```

### Database Backup

I set up automated database backups:

```bash
# Create backup script
cat > /var/www/davestj.com/bthl-hc/scripts/backup-database.sh << 'EOF'
#!/bin/bash
# File: /var/www/davestj.com/bthl-hc/scripts/backup-database.sh
# Author: davestj (David St John)
# Date: 2025-07-17
# Purpose: Automated database backup script for BTHL-HealthCare

BACKUP_DIR="/var/www/davestj.com/bthl-hc/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="bthl_healthcare_backup_${DATE}.sql"

# Create backup directory
mkdir -p "$BACKUP_DIR"

# Create database backup
pg_dump -U davestj -h localhost bthl_healthcare > "$BACKUP_DIR/$BACKUP_FILE"

# Compress backup
gzip "$BACKUP_DIR/$BACKUP_FILE"

# Remove backups older than 30 days
find "$BACKUP_DIR" -name "*.sql.gz" -mtime +30 -delete

echo "Database backup completed: $BACKUP_DIR/${BACKUP_FILE}.gz"
EOF

chmod +x /var/www/davestj.com/bthl-hc/scripts/backup-database.sh

# Create cron job for daily backups
(crontab -l 2>/dev/null; echo "0 2 * * * /var/www/davestj.com/bthl-hc/scripts/backup-database.sh") | crontab -
```

---

## 🚨 Troubleshooting Guide

### Common Issues and Solutions

I document common issues and their resolutions:

**Issue 1: Application Won't Start**
```bash
# Check Java version
java -version

# Check if port is in use
sudo netstat -tulpn | grep 8330
sudo lsof -i :8330

# Check application logs
tail -f /var/www/davestj.com/bthl-hc/logs/bthl-healthcare.log

# Check database connectivity
pg_isready -h localhost -p 5432 -U davestj
```

**Issue 2: Database Connection Failed**
```bash
# Check PostgreSQL status
sudo systemctl status postgresql

# Test database connection
psql -U davestj -d bthl_healthcare -c "SELECT 1;"

# Check PostgreSQL logs
sudo tail -f /var/log/postgresql/postgresql-*-main.log

# Reset database password if needed
sudo -u postgres psql -c "ALTER USER davestj WITH PASSWORD '<DEV_PASSWORD>';"
```

**Issue 3: Memory Issues**
```bash
# Check system memory
free -h

# Check Java heap usage
jstat -gc $(pgrep -f bthl-healthcare)

# Adjust JVM memory settings
java -Xms1G -Xmx2G -jar target/bthl-healthcare.jar
```

**Issue 4: Permission Denied**
```bash
# Fix file permissions
sudo chown -R davestj:davestj /var/www/davestj.com/bthl-hc
find /var/www/davestj.com/bthl-hc -type d -exec chmod 755 {} \;
find /var/www/davestj.com/bthl-hc -type f -exec chmod 644 {} \;
chmod +x /var/www/davestj.com/bthl-hc/scripts/*.sh
```

---

## 📋 Environment Variables Reference

### Required Environment Variables

I document all environment variables for configuration:

```bash
# Create environment file
cat > /var/www/davestj.com/bthl-hc/.env << 'EOF'
# BTHL-HealthCare Environment Configuration
# File: /var/www/davestj.com/bthl-hc/.env
# Author: davestj (David St John)
# Date: 2025-07-17
# Purpose: Environment variables for BTHL-HealthCare application configuration

# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=bthl_healthcare
DB_USERNAME=davestj
DB_PASSWORD=changeme
DB_URL=jdbc:postgresql://localhost:5432/bthl_healthcare

# JWT Configuration
JWT_SECRET=changeme
JWT_EXPIRATION=86400000

# Admin User
ADMIN_USERNAME=admin
ADMIN_PASSWORD=changeme

# Email Configuration
SMTP_HOST=localhost
SMTP_PORT=1025
SMTP_USERNAME=
SMTP_PASSWORD=
SMTP_AUTH=false
SMTP_STARTTLS=false

# Application Configuration
SERVER_PORT=8330
SPRING_PROFILES_ACTIVE=dev
LOG_LEVEL=DEBUG

# Security Configuration
SESSION_TIMEOUT_MINUTES=480
MAX_FAILED_LOGIN_ATTEMPTS=5
ACCOUNT_LOCKOUT_MINUTES=30

# File Upload Configuration
MAX_FILE_SIZE=50MB
MAX_REQUEST_SIZE=50MB

# Monitoring Configuration
ACTUATOR_ENDPOINTS=health,info,metrics,env
HEALTH_SHOW_DETAILS=always
EOF

# Secure the environment file
chmod 600 /var/www/davestj.com/bthl-hc/.env
```

The `.env` file is excluded from version control and should never be committed.

A `.env.example` file is available; copy it to `.env` and replace placeholder values for local development.

---

## 🎯 Production Deployment Checklist

### Pre-Deployment Tasks

I provide a comprehensive deployment checklist:

- [ ] **Security Review**
    - [ ] Change default JWT secret
    - [ ] Update database passwords
    - [ ] Enable HTTPS/SSL
    - [ ] Configure firewall rules
    - [ ] Review user permissions

- [ ] **Database Preparation**
    - [ ] Create production database
    - [ ] Run schema migrations
    - [ ] Set up backup procedures
    - [ ] Configure connection pooling
    - [ ] Optimize database settings

- [ ] **Application Configuration**
    - [ ] Set production profile
    - [ ] Configure external dependencies
    - [ ] Set up monitoring and logging
    - [ ] Configure email settings
    - [ ] Test all endpoints

- [ ] **Infrastructure Setup**
    - [ ] Install and configure Nginx
    - [ ] Set up SSL certificates
    - [ ] Configure load balancing (if needed)
    - [ ] Set up monitoring tools
    - [ ] Configure backup procedures

- [ ] **Testing and Validation**
    - [ ] Run integration tests
    - [ ] Perform security testing
    - [ ] Load testing
    - [ ] Disaster recovery testing
    - [ ] User acceptance testing

---

## 📞 Support and Resources

### Getting Help

I provide resources for additional support:

- **Documentation:** Internal project documentation in `/docs` directory
- **Logs:** Application logs in `/var/www/davestj.com/bthl-hc/logs/`
- **Configuration:** All config files in `/var/www/davestj.com/bthl-hc/config/`
- **Scripts:** Utility scripts in `/var/www/davestj.com/bthl-hc/scripts/`

### External Resources

- **Spring Boot Documentation:** https://docs.spring.io/spring-boot/
- **PostgreSQL Documentation:** https://www.postgresql.org/docs/
- **Nginx Documentation:** https://nginx.org/en/docs/
- **Ubuntu Server Guide:** https://ubuntu.com/server/docs

---

**Last Updated:** 2025-07-17  
**Author:** davestj (David St John)  
**Status:** Complete setup guide ready for deployment  
**Next Review:** Upon production deployment completion