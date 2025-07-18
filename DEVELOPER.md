# BTHL-HealthCare Developer Environment Setup Guide

**File:** `/var/www/davestj.com/bthl-hc/DEVELOPER.md`  
**Author:** davestj (David St John)  
**Date:** 2025-07-17  
**Purpose:** Comprehensive developer environment setup guide for BTHL-HealthCare platform  
**Description:** I created this comprehensive guide to help developers set up their development environment for my healthcare management platform. I've included setup instructions for both macOS (M2 ARM) and Linux (Debian 12), with detailed IDE configuration for Eclipse and IntelliJ IDEA.

**Changelog:**
- 2025-07-17: Initial creation of comprehensive developer environment setup guide

**Git Commit:** `git commit -m "docs: add comprehensive developer environment setup guide for macOS and Linux"`

**Next Dev Feature:** Add Docker development environment with hot-reload and debugging support  
**TODO:** Integrate VS Code configuration and remote development container setup

---

## 📋 Overview

This guide covers setting up a complete development environment for the BTHL-HealthCare platform, including:
- **Technology Stack:** Java 21, Spring Boot 3.2.0, PostgreSQL, Thymeleaf, Bootstrap 5
- **Supported Platforms:** macOS (M2 ARM), Linux (Debian 12)
- **IDEs:** Eclipse, IntelliJ IDEA
- **Development Tools:** Maven, Git, Node.js, PostgreSQL

---

## 🍎 macOS Development Setup (M2 ARM)

### Prerequisites

I recommend using Homebrew as the primary package manager for macOS development:

```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Update Homebrew
brew update && brew upgrade
```

### Java Development Kit (JDK 21)

I use OpenJDK 21 for optimal compatibility with Spring Boot 3.2.0:

```bash
# Install OpenJDK 21
brew install openjdk@21

# Create symlink for system-wide access
sudo ln -sfn $(brew --prefix)/opt/openjdk@21/libexec/openjdk.jdk \
     /Library/Java/JavaVirtualMachines/openjdk-21.jdk

# Add to your shell profile (.zshrc or .bash_profile)
echo 'export JAVA_HOME="$(brew --prefix)/opt/openjdk@21"' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc

# Reload shell configuration
source ~/.zshrc

# Verify installation
java -version
javac -version
```

### Maven Build Tool

I configure Maven for dependency management and build automation:

```bash
# Install Maven
brew install maven

# Verify installation
mvn -version

# Configure Maven settings (optional)
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
            <id>development</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <maven.compiler.source>21</maven.compiler.source>
                <maven.compiler.target>21</maven.compiler.target>
                <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            </properties>
        </profile>
    </profiles>
    
</settings>
EOF
```

### PostgreSQL Database

I set up PostgreSQL for local development with proper configuration:

```bash
# Install PostgreSQL
brew install postgresql@15

# Start PostgreSQL service
brew services start postgresql@15

# Add PostgreSQL to PATH
echo 'export PATH="$(brew --prefix)/opt/postgresql@15/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc

# Create development database and user
createdb bthl_healthcare
createuser -s davestj

# Set password for database user (optional for local dev)
psql -d bthl_healthcare -c "ALTER USER davestj WITH PASSWORD 'dev_password';"
```

### Node.js and npm

I include Node.js for potential frontend build tools and development utilities:

```bash
# Install Node.js LTS
brew install node

# Verify installation
node --version
npm --version

# Install global development tools
npm install -g typescript ts-node nodemon
```

### Git Configuration

I configure Git for version control with proper credentials:

```bash
# Install Git (if not already installed)
brew install git

# Configure Git user information
git config --global user.name "David St John"
git config --global user.email "davestj@gmail.com"

# Configure Git editor and merge tool
git config --global core.editor "nano"
git config --global merge.tool "opendiff"

# Enable Git credential helper
git config --global credential.helper osxkeychain
```

---

## 🐧 Linux Development Setup (Debian 12)

### System Update and Prerequisites

I ensure the system is up to date before installing development tools:

```bash
# Update package lists and upgrade system
sudo apt update && sudo apt upgrade -y

# Install essential build tools
sudo apt install -y build-essential curl wget apt-transport-https ca-certificates \
                    gnupg lsb-release software-properties-common unzip zip
```

### Java Development Kit (JDK 21)

I install OpenJDK 21 for Linux development:

```bash
# Install OpenJDK 21
sudo apt install -y openjdk-21-jdk openjdk-21-jre

# Configure JAVA_HOME
echo 'export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"' | sudo tee -a /etc/environment
echo 'export PATH="$JAVA_HOME/bin:$PATH"' | sudo tee -a /etc/environment

# Add to user profile
echo 'export JAVA_HOME="/usr/lib/jvm/java-21-openjdk-amd64"' >> ~/.bashrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.bashrc

# Reload environment
source ~/.bashrc

# Verify installation
java -version
javac -version
```

### Maven Build Tool

I set up Maven for build management on Linux:

```bash
# Install Maven
sudo apt install -y maven

# Verify installation
mvn -version

# Configure Maven settings
mkdir -p ~/.m2
cp /var/www/davestj.com/bthl-hc/config/maven-settings.xml ~/.m2/settings.xml
```

### PostgreSQL Database

I configure PostgreSQL for development on Linux:

```bash
# Install PostgreSQL
sudo apt install -y postgresql postgresql-contrib

# Start and enable PostgreSQL service
sudo systemctl start postgresql
sudo systemctl enable postgresql

# Create development database and user
sudo -u postgres createuser -s davestj
sudo -u postgres createdb bthl_healthcare -O davestj

# Configure peer authentication for local development
sudo -u postgres psql -c "ALTER USER davestj WITH PASSWORD 'dev_password';"
```

### Node.js and npm

I install Node.js using NodeSource repository for the latest LTS:

```bash
# Add NodeSource repository
curl -fsSL https://deb.nodesource.com/setup_lts.x | sudo -E bash -

# Install Node.js
sudo apt install -y nodejs

# Verify installation
node --version
npm --version

# Install global development tools
sudo npm install -g typescript ts-node nodemon
```

---

## 🌙 Eclipse IDE Configuration

### Installation

I set up Eclipse IDE for Java Enterprise development:

**macOS Installation:**
```bash
# Install Eclipse IDE for Enterprise Java and Web Developers
brew install --cask eclipse-jee

# Launch Eclipse
open /Applications/Eclipse\ JEE.app
```

**Linux Installation:**
```bash
# Download Eclipse IDE for Enterprise Java and Web Developers
wget -O eclipse-installer.tar.gz "https://www.eclipse.org/downloads/download.php?file=/oomph/epp/2023-12/R/eclipse-inst-jre-linux64.tar.gz&r=1"

# Extract and install
tar -xzf eclipse-installer.tar.gz
cd eclipse-installer
./eclipse-inst
```

### Workspace Setup

I configure Eclipse workspace for optimal BTHL-HealthCare development:

1. **Create Workspace:**
   - Location: `/Users/dstjohn/dev/bthl-healthcare-workspace` (macOS)
   - Location: `/home/davestj/dev/bthl-healthcare-workspace` (Linux)

2. **Import Project:**
   ```
   File → Import → Existing Maven Projects
   Root Directory: /var/www/davestj.com/bthl-hc
   ```

### Essential Eclipse Plugins

I install these plugins for enhanced development experience:

```
Help → Eclipse Marketplace → Install:

1. Spring Tools 4 (Spring Tool Suite)
   - Spring Boot Dashboard
   - Spring Configuration Editor
   - Spring Bean Explorer

2. Thymeleaf Plugin
   - Syntax highlighting for Thymeleaf templates
   - Auto-completion for Thymeleaf expressions

3. PostgreSQL Development Tools
   - Database perspective and tools
   - SQL editor with syntax highlighting

4. Maven Integration for Eclipse (m2e)
   - Enhanced Maven support
   - Dependency hierarchy view

5. Git Integration (EGit)
   - Git repositories view
   - Git staging and history
```

### Eclipse Project Configuration

I configure the project settings for optimal development:

**Project Properties → Java Build Path:**
- Source folders: `src/main/java`, `src/main/resources`, `src/test/java`
- Libraries: Maven Dependencies, JRE System Library (21)

**Project Properties → Java Compiler:**
- Compiler compliance level: 21
- Enable preview features: Yes

**Project Properties → Spring:**
- Enable Spring nature
- Configure Spring Boot configuration files

**Run Configuration:**
```
Run → Run Configurations → Spring Boot App
Name: BTHL-HealthCare Development
Project: bthl-healthcare
Main Type: com.bthl.healthcare.BthlHealthcareApplication
Profile: dev
VM Arguments: -Dspring.profiles.active=dev -Xmx2G -Xms1G
Program Arguments: --server.port=8330
```

### Database Connection Setup

I configure database connection in Eclipse:

```
Window → Show View → Other → Data Management → Data Source Explorer

New Connection Profile:
- Connection Profile Type: PostgreSQL
- Driver: PostgreSQL JDBC Driver
- Database: bthl_healthcare
- URL: jdbc:postgresql://localhost:5432/bthl_healthcare
- User: davestj
- Password: dev_password
```

---

## 💡 IntelliJ IDEA Configuration

### Installation Verification

Since you already have IntelliJ IDEA setup, I'll provide the project-specific configuration:

### Project Import and Setup

I configure IntelliJ for optimal BTHL-HealthCare development:

1. **Import Project:**
   ```
   File → Open → /var/www/davestj.com/bthl-hc/pom.xml
   Open as Project → Import Maven project automatically
   ```

2. **Project Structure:**
   ```
   File → Project Structure
   Project SDK: 21 (java version "21")
   Project language level: 21 - Pattern matching for switch
   ```

### Essential IntelliJ Plugins

I recommend these plugins for enhanced development:

```
File → Settings → Plugins → Marketplace:

1. Spring Boot
   - Spring Boot run configurations
   - Application properties support
   - Spring Boot endpoints

2. Thymeleaf
   - Template syntax highlighting
   - Auto-completion and navigation

3. Database Tools and SQL
   - Built-in database browser
   - SQL console and query execution

4. Maven Helper
   - Maven project analysis
   - Dependency conflict resolution

5. GitToolBox
   - Enhanced Git integration
   - Blame annotations and status
```

### Run Configuration

I set up run configurations for development and testing:

**Spring Boot Application:**
```
Run → Edit Configurations → Add New → Spring Boot
Name: BTHL-HealthCare Development
Main class: com.bthl.healthcare.BthlHealthcareApplication
Active profiles: dev
VM options: -Dspring.profiles.active=dev -Xmx2G -Xms1G
Program arguments: --server.port=8330
Use classpath of module: bthl-healthcare.main
```

**Test Configuration:**
```
Run → Edit Configurations → Add New → JUnit
Name: BTHL-HealthCare Tests
Test kind: All in package
Package: com.bthl.healthcare
VM options: -Dspring.profiles.active=test
Use classpath of module: bthl-healthcare.test
```

### Database Configuration

I configure database connection in IntelliJ:

```
View → Tool Windows → Database

Add New Data Source → PostgreSQL
Host: localhost
Port: 5432
Database: bthl_healthcare
User: davestj
Password: dev_password
URL: jdbc:postgresql://localhost:5432/bthl_healthcare
```

### Code Style and Formatting

I configure code style for consistent formatting:

```
File → Settings → Editor → Code Style → Java
Import scheme → Google Style (recommended)

File → Settings → Editor → Code Style → SQL
Dialect: PostgreSQL

File → Settings → Tools → Actions on Save
- Reformat code
- Optimize imports
- Rearrange code
```

---

## 🗄️ Database Development Setup

### Schema Installation

I provide the complete process for setting up the database schema:

```bash
# Connect to PostgreSQL
psql -U davestj -d bthl_healthcare

# Load the schema (from project root)
\i src/main/resources/db/migration/V1__Initial_Schema.sql

# Verify schema installation
\dt

# Check specific tables
\d users
\d roles
\d companies
\d insurance_providers

# Exit PostgreSQL
\q
```

### Sample Data Generation

I create sample data for development testing:

```sql
-- Connect to database
psql -U davestj -d bthl_healthcare

-- Insert sample admin user (password: 'AdminPassword123!')
INSERT INTO users (
    username, email, password_hash, first_name, last_name, 
    status, user_type, role_id, email_verified
) VALUES (
    'admin',
    'admin@bthl-healthcare.com',
    '$2a$12$8mCkX9F5Z8vP.nN5wJ2oYO8rF5J5xJ5K5M5P5Q5R5S5T5U5V5W5X5Y', -- AdminPassword123!
    'System',
    'Administrator',
    'ACTIVE',
    'ADMIN',
    (SELECT id FROM roles WHERE name = 'SUPER_ADMIN'),
    true
);

-- Insert sample company
INSERT INTO companies (
    name, legal_name, tax_id, industry, employee_count,
    headquarters_address, primary_contact_email, primary_contact_phone,
    status, created_by
) VALUES (
    'TechCorp Solutions',
    'TechCorp Solutions LLC',
    '12-3456789',
    'Technology',
    250,
    '123 Tech Street, Silicon Valley, CA 94000',
    'hr@techcorp.com',
    '+1-555-0123',
    'ACTIVE',
    (SELECT id FROM users WHERE username = 'admin')
);

-- Insert sample insurance provider
INSERT INTO insurance_providers (
    name, legal_name, provider_code, provider_type,
    am_best_rating, headquarters_address, phone, email,
    website_url, is_active, created_by
) VALUES (
    'HealthFirst Insurance',
    'HealthFirst Insurance Company',
    'HFI001',
    'HEALTH_INSURANCE',
    'A+',
    '456 Insurance Blvd, Chicago, IL 60601',
    '+1-800-555-HEALTH',
    'contact@healthfirst.com',
    'https://www.healthfirst.com',
    true,
    (SELECT id FROM users WHERE username = 'admin')
);
```

### Database Backup and Restore

I provide scripts for database backup and restore operations:

**Backup Script:**
```bash
#!/bin/bash
# File: /var/www/davestj.com/bthl-hc/scripts/backup-db.sh

BACKUP_DIR="/var/www/davestj.com/bthl-hc/backups"
DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="bthl_healthcare_backup_${DATE}.sql"

# Create backup directory if it doesn't exist
mkdir -p ${BACKUP_DIR}

# Create database backup
pg_dump -U davestj -h localhost bthl_healthcare > ${BACKUP_DIR}/${BACKUP_FILE}

# Compress backup
gzip ${BACKUP_DIR}/${BACKUP_FILE}

echo "Database backup created: ${BACKUP_DIR}/${BACKUP_FILE}.gz"
```

**Restore Script:**
```bash
#!/bin/bash
# File: /var/www/davestj.com/bthl-hc/scripts/restore-db.sh

if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <backup_file.sql.gz>"
    exit 1
fi

BACKUP_FILE=$1

# Drop and recreate database
dropdb -U davestj bthl_healthcare
createdb -U davestj bthl_healthcare

# Restore from backup
gunzip -c ${BACKUP_FILE} | psql -U davestj bthl_healthcare

echo "Database restored from: ${BACKUP_FILE}"
```

---

## 🔧 Development Workflow

### Daily Development Process

I recommend this workflow for consistent development:

1. **Start Development Session:**
   ```bash
   # Navigate to project directory
   cd /var/www/davestj.com/bthl-hc
   
   # Pull latest changes
   git pull origin main
   
   # Update dependencies
   mvn clean install
   
   # Start database (if not running)
   # macOS:
   brew services start postgresql@15
   # Linux:
   sudo systemctl start postgresql
   
   # Run application
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

2. **Testing and Validation:**
   ```bash
   # Run unit tests
   mvn test
   
   # Run integration tests
   mvn test -P integration-tests
   
   # Run specific test class
   mvn test -Dtest=UserServiceTest
   
   # Generate test coverage report
   mvn jacoco:report
   ```

3. **Code Quality Checks:**
   ```bash
   # Compile and check for errors
   mvn compile
   
   # Run checkstyle
   mvn checkstyle:check
   
   # Run SpotBugs analysis
   mvn spotbugs:check
   ```

### Environment Variables

I configure environment variables for different development scenarios:

**Development Environment (.env.dev):**
```bash
# Database Configuration
DB_HOST=localhost
DB_PORT=5432
DB_NAME=bthl_healthcare
DB_USERNAME=davestj
DB_PASSWORD=dev_password

# JWT Configuration
JWT_SECRET=development_secret_key_change_in_production
JWT_EXPIRATION=86400000

# Email Configuration (for development)
SMTP_HOST=localhost
SMTP_PORT=1025
SMTP_USERNAME=
SMTP_PASSWORD=

# Logging Configuration
LOG_LEVEL=DEBUG
LOG_FILE=/var/www/davestj.com/bthl-hc/logs/application.log
```

### Hot Reload Configuration

I enable hot reload for faster development cycles:

**Spring Boot DevTools Configuration:**
```yaml
# Add to application-dev.yml
spring:
  devtools:
    restart:
      enabled: true
      additional-paths: src/main/java
    livereload:
      enabled: true
      port: 35729
    remote:
      restart:
        enabled: false
```

---

## 🐛 Debugging and Troubleshooting

### Common Issues and Solutions

I document common development issues and their solutions:

**Issue 1: Port 8330 Already in Use**
```bash
# Find process using port 8330
lsof -i :8330
# or
netstat -tulpn | grep 8330

# Kill the process
kill -9 <PID>

# Alternative: Use different port
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8331"
```

**Issue 2: Database Connection Failed**
```bash
# Check PostgreSQL status
# macOS:
brew services list | grep postgresql
# Linux:
sudo systemctl status postgresql

# Test database connection
psql -U davestj -d bthl_healthcare -c "SELECT version();"

# Reset database permissions
sudo -u postgres psql -c "ALTER USER davestj WITH SUPERUSER;"
```

**Issue 3: Maven Dependencies Not Resolved**
```bash
# Clear Maven cache
rm -rf ~/.m2/repository

# Reload dependencies
mvn dependency:resolve

# Force update snapshots
mvn clean install -U
```

### Remote Debugging

I configure remote debugging for thorough troubleshooting:

**IntelliJ Remote Debug Configuration:**
```
Run → Edit Configurations → Add New → Remote JVM Debug
Name: BTHL-HealthCare Remote Debug
Host: localhost
Port: 5005
Command line arguments: -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
```

**Start Application with Debug:**
```bash
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
```

---

## 📊 Performance Monitoring

### Development Metrics

I set up monitoring for development performance:

**JVM Monitoring:**
```bash
# Install JVM monitoring tools
# macOS:
brew install jstat jmap jconsole
# Linux:
sudo apt install openjdk-21-jdk-headless

# Monitor JVM performance
jstat -gc -t $(pgrep -f "bthl-healthcare") 5s

# Generate heap dump for analysis
jmap -dump:format=b,file=heap_dump.hprof $(pgrep -f "bthl-healthcare")
```

**Database Performance:**
```sql
-- Monitor active connections
SELECT * FROM pg_stat_activity WHERE datname = 'bthl_healthcare';

-- Check query performance
SELECT query, mean_exec_time, calls 
FROM pg_stat_statements 
ORDER BY mean_exec_time DESC 
LIMIT 10;

-- Monitor table sizes
SELECT schemaname, tablename, 
       pg_size_pretty(pg_total_relation_size(tablename::regclass)) as size
FROM pg_tables 
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(tablename::regclass) DESC;
```

---

## 🚀 Deployment Preparation

### Production Build

I configure production builds for deployment readiness:

```bash
# Create production build
mvn clean package -P prod

# Run production build locally for testing
java -jar target/bthl-healthcare.jar --spring.profiles.active=prod

# Create Docker image (if Docker is available)
docker build -t bthl-healthcare:latest .
```

### Environment-Specific Configurations

I maintain separate configurations for different environments:

**Development (application-dev.yml):**
```yaml
server:
  port: 8330
logging:
  level:
    com.bthl.healthcare: DEBUG
    org.springframework.security: DEBUG
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bthl_healthcare
    username: davestj
    password: dev_password
```

**Production (application-prod.yml):**
```yaml
server:
  port: 8330
logging:
  level:
    com.bthl.healthcare: INFO
    root: WARN
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/bthl_healthcare}
    username: ${DB_USERNAME:davestj}
    password: ${DB_PASSWORD}
```

---

## 📚 Additional Resources

### Documentation and Learning

I provide additional resources for continued learning:

- **Spring Boot Reference:** https://docs.spring.io/spring-boot/docs/current/reference/html/
- **Spring Security Reference:** https://docs.spring.io/spring-security/reference/
- **PostgreSQL Documentation:** https://www.postgresql.org/docs/
- **Thymeleaf Documentation:** https://www.thymeleaf.org/documentation.html
- **Maven Documentation:** https://maven.apache.org/guides/

### Code Quality Tools

I recommend these tools for maintaining code quality:

```bash
# Install SonarQube (optional)
# macOS:
brew install sonarqube
# Linux:
# Follow SonarQube installation guide

# Install Git hooks for code quality
curl -o .git/hooks/pre-commit https://raw.githubusercontent.com/davestj/git-hooks/main/pre-commit
chmod +x .git/hooks/pre-commit
```

---

**Last Updated:** 2025-07-17  
**Author:** davestj (David St John)  
**Status:** Ready for development environment setup  
**Next Review:** Upon IDE configuration completion
