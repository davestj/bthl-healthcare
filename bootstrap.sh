#!/bin/bash

# File: /var/www/davestj.com/bthl-hc/bootstrap.sh
# Author: davestj (David St John)
# Date: 2025-07-16
# Purpose: Bootstrap script for BTHL-HealthCare Spring Boot application setup
# Description: I created this script to automate the complete environment setup for my healthcare management platform.
#              I designed this to detect Debian 12 or Ubuntu 22/24 systems and install all required dependencies.
#
# Changelog:
# 2025-07-16: Initial creation of bootstrap environment setup script for BTHL-HealthCare
#
# Git Commit: git commit -m "feat: add bootstrap script for BTHL-HealthCare environment setup with Java/Spring/PostgreSQL"
#
# Next Dev Feature: Add automated SSL certificate generation and nginx virtual host configuration
# TODO: Integrate Docker containerization options for development environments

set -euo pipefail

# I define these environment variables for consistent path management across the application
export PROJECT_NAME="BTHL-HealthCare"
export PROJECT_ROOT="/var/www/davestj.com/bthl-hc"
export SSL_DIR="${PROJECT_ROOT}/ssl"
export LOG_DIR="${PROJECT_ROOT}/logs"
export CONFIG_DIR="${PROJECT_ROOT}/etc"
export DB_NAME="bthl_healthcare"
export DB_USER="davestj"
export JAVA_VERSION="21"
export SPRING_BOOT_VERSION="3.2.0"

# I created this color-coded logging system to make the bootstrap process more user-friendly
log_info() {
    echo -e "\033[32m[INFO]\033[0m $1"
}

log_warn() {
    echo -e "\033[33m[WARN]\033[0m $1"
}

log_error() {
    echo -e "\033[31m[ERROR]\033[0m $1"
}

log_step() {
    echo -e "\033[36m[STEP]\033[0m $1"
}

# I implement this function to detect the operating system and ensure compatibility
detect_os() {
    log_step "Detecting operating system..."

    if [[ -f /etc/os-release ]]; then
        source /etc/os-release
        OS_NAME="$NAME"
        OS_VERSION="$VERSION_ID"

        case "$ID" in
            "debian")
                if [[ "$VERSION_ID" == "12" ]]; then
                    log_info "Detected Debian 12 - Supported OS"
                    PACKAGE_MANAGER="apt"
                else
                    log_error "Unsupported Debian version: $VERSION_ID. This script requires Debian 12."
                    exit 1
                fi
                ;;
            "ubuntu")
                if [[ "$VERSION_ID" == "22.04" || "$VERSION_ID" == "24.04" ]]; then
                    log_info "Detected Ubuntu $VERSION_ID - Supported OS"
                    PACKAGE_MANAGER="apt"
                else
                    log_error "Unsupported Ubuntu version: $VERSION_ID. This script requires Ubuntu 22.04 or 24.04."
                    exit 1
                fi
                ;;
            *)
                log_error "Unsupported OS: $ID. This script supports Debian 12 and Ubuntu 22/24."
                exit 1
                ;;
        esac
    else
        log_error "Cannot detect OS. /etc/os-release not found."
        exit 1
    fi
}

# I created this function to ensure the script runs with appropriate privileges
check_prerequisites() {
    log_step "Checking prerequisites..."

    if [[ $EUID -ne 0 ]]; then
        log_error "This script must be run as root or with sudo privileges"
        exit 1
    fi

    if ! command -v curl &> /dev/null; then
        log_info "Installing curl..."
        $PACKAGE_MANAGER update
        $PACKAGE_MANAGER install -y curl
    fi

    if ! command -v wget &> /dev/null; then
        log_info "Installing wget..."
        $PACKAGE_MANAGER install -y wget
    fi
}

# I implement this function to create the complete directory structure for my healthcare application
create_directories() {
    log_step "Creating project directory structure..."

    # I create the main project directories with proper permissions
    mkdir -p "${PROJECT_ROOT}"/{src,config,logs,ssl,etc,backups,scripts}
    mkdir -p "${PROJECT_ROOT}/src/main/java/com/bthl/healthcare"
    mkdir -p "${PROJECT_ROOT}/src/main/resources"/{static/{css,js,images},templates,db/migration}
    mkdir -p "${PROJECT_ROOT}/src/test/java/com/bthl/healthcare"

    # I ensure the davestj user owns the project directory
    if id "davestj" &>/dev/null; then
        chown -R davestj:davestj "${PROJECT_ROOT}"
        log_info "Set ownership of ${PROJECT_ROOT} to davestj:davestj"
    else
        log_warn "User 'davestj' not found. Creating user..."
        useradd -m -s /bin/bash davestj
        chown -R davestj:davestj "${PROJECT_ROOT}"
    fi

    chmod -R 755 "${PROJECT_ROOT}"
    chmod -R 700 "${SSL_DIR}"
    chmod -R 755 "${LOG_DIR}"
}

# I created this function to install and configure Java Development Kit
install_java() {
    log_step "Installing Java JDK ${JAVA_VERSION}..."

    if command -v java &> /dev/null; then
        CURRENT_JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
        if [[ "$CURRENT_JAVA_VERSION" == "$JAVA_VERSION" ]]; then
            log_info "Java ${JAVA_VERSION} is already installed"
            return
        fi
    fi

    # I install OpenJDK which is the recommended Java implementation for Spring Boot
    $PACKAGE_MANAGER update
    $PACKAGE_MANAGER install -y openjdk-${JAVA_VERSION}-jdk openjdk-${JAVA_VERSION}-jre

    # I configure JAVA_HOME environment variable for system-wide access
    JAVA_HOME_PATH="/usr/lib/jvm/java-${JAVA_VERSION}-openjdk-amd64"
    echo "export JAVA_HOME=${JAVA_HOME_PATH}" >> /etc/environment
    echo "export PATH=\$PATH:\$JAVA_HOME/bin" >> /etc/environment

    # I create a profile script for immediate availability
    cat > /etc/profile.d/java.sh << EOF
export JAVA_HOME=${JAVA_HOME_PATH}
export PATH=\$PATH:\$JAVA_HOME/bin
EOF

    chmod +x /etc/profile.d/java.sh
    source /etc/profile.d/java.sh

    log_info "Java ${JAVA_VERSION} installation completed"
    java -version
}

# I implement this function to install Apache Maven for project build management
install_maven() {
    log_step "Installing Apache Maven..."

    if command -v mvn &> /dev/null; then
        log_info "Maven is already installed: $(mvn -version | head -1)"
        return
    fi

    $PACKAGE_MANAGER install -y maven

    # I verify the Maven installation
    mvn -version
    log_info "Maven installation completed"
}

# I created this function to install and configure PostgreSQL database
install_postgresql() {
    log_step "Installing and configuring PostgreSQL..."

    if systemctl is-active --quiet postgresql; then
        log_info "PostgreSQL is already running"
    else
        $PACKAGE_MANAGER install -y postgresql postgresql-contrib
        systemctl enable postgresql
        systemctl start postgresql
    fi

    # I configure PostgreSQL for local development with peer authentication
    sudo -u postgres createuser -s "${DB_USER}" 2>/dev/null || log_info "User ${DB_USER} already exists"
    sudo -u postgres createdb "${DB_NAME}" -O "${DB_USER}" 2>/dev/null || log_info "Database ${DB_NAME} already exists"

    # I configure pg_hba.conf for local trusted access
    PG_VERSION=$(sudo -u postgres psql -t -c "SELECT version();" | grep -o '[0-9]\+\.[0-9]\+' | head -1)
    PG_CONFIG_DIR="/etc/postgresql/${PG_VERSION}/main"

    if [[ -f "${PG_CONFIG_DIR}/pg_hba.conf" ]]; then
        # I backup the original configuration
        cp "${PG_CONFIG_DIR}/pg_hba.conf" "${PG_CONFIG_DIR}/pg_hba.conf.backup.$(date +%Y%m%d_%H%M%S)"

        # I configure peer authentication for local connections
        sed -i 's/local   all             all                                     peer/local   all             all                                     peer/' "${PG_CONFIG_DIR}/pg_hba.conf"

        systemctl reload postgresql
        log_info "PostgreSQL configuration updated for local trusted access"
    fi
}

# I implement this function to install Node.js for potential frontend build tools
install_nodejs() {
    log_step "Installing Node.js..."

    if command -v node &> /dev/null; then
        log_info "Node.js is already installed: $(node --version)"
        return
    fi

    # I install Node.js using NodeSource repository for the latest LTS version
    curl -fsSL https://deb.nodesource.com/setup_lts.x | bash -
    $PACKAGE_MANAGER install -y nodejs

    log_info "Node.js installation completed: $(node --version)"
    log_info "npm version: $(npm --version)"
}

# I created this function to install additional development tools
install_dev_tools() {
    log_step "Installing development tools..."

    $PACKAGE_MANAGER install -y \
        git \
        vim \
        nano \
        htop \
        tree \
        unzip \
        zip \
        build-essential \
        software-properties-common \
        apt-transport-https \
        ca-certificates \
        gnupg \
        lsb-release

    log_info "Development tools installation completed"
}

# I implement this function to create the initial Spring Boot project structure
create_spring_project_structure() {
    log_step "Creating Spring Boot project structure..."

    cd "${PROJECT_ROOT}"

    # I create the Maven directory structure
    mkdir -p src/main/java/com/bthl/healthcare/{controller,service,repository,model,config,security,dto,exception}
    mkdir -p src/main/resources/{static/{css,js,images},templates,db/migration}
    mkdir -p src/test/java/com/bthl/healthcare

    log_info "Spring Boot project structure created successfully"
}

# I created this function to set up proper file permissions and ownership
configure_permissions() {
    log_step "Configuring file permissions..."

    # I ensure proper ownership for the project
    chown -R davestj:davestj "${PROJECT_ROOT}"

    # I set secure permissions for different directories
    chmod -R 755 "${PROJECT_ROOT}/src"
    chmod -R 755 "${PROJECT_ROOT}/config"
    chmod -R 755 "${LOG_DIR}"
    chmod -R 700 "${SSL_DIR}"

    # I make scripts executable
    find "${PROJECT_ROOT}/scripts" -name "*.sh" -type f -exec chmod +x {} \;

    log_info "File permissions configured successfully"
}

# I implement this function to provide final setup instructions
display_setup_completion() {
    log_step "Setup completion summary..."

    echo "=============================================="
    echo "BTHL-HealthCare Environment Setup Complete!"
    echo "=============================================="
    echo ""
    echo "Project Details:"
    echo "  - Project Root: ${PROJECT_ROOT}"
    echo "  - Database: ${DB_NAME}"
    echo "  - DB User: ${DB_USER}"
    echo "  - Java Version: $(java -version 2>&1 | head -1)"
    echo "  - Maven Version: $(mvn -version 2>&1 | head -1)"
    echo "  - Node.js Version: $(node --version 2>/dev/null || echo 'Not available')"
    echo ""
    echo "Next Steps:"
    echo "  1. Navigate to project directory: cd ${PROJECT_ROOT}"
    echo "  2. Build the project: mvn clean install"
    echo "  3. Run the application: mvn spring-boot:run"
    echo "  4. Access the application: http://localhost:8330"
    echo ""
    echo "Configuration Files:"
    echo "  - Application config: ${PROJECT_ROOT}/src/main/resources/application.yml"
    echo "  - Database scripts: ${PROJECT_ROOT}/src/main/resources/db/migration/"
    echo "  - Logs: ${LOG_DIR}"
    echo ""
    echo "Environment Variables Set:"
    echo "  - JAVA_HOME: ${JAVA_HOME:-'Not set'}"
    echo "  - PROJECT_ROOT: ${PROJECT_ROOT}"
    echo ""
}

# I created this main function to orchestrate the entire bootstrap process
main() {
    log_info "Starting BTHL-HealthCare bootstrap process..."
    echo "================================================="

    detect_os
    check_prerequisites
    create_directories
    install_java
    install_maven
    install_postgresql
    install_nodejs
    install_dev_tools
    create_spring_project_structure
    configure_permissions
    display_setup_completion

    log_info "Bootstrap process completed successfully!"
}

# I execute the main function to start the bootstrap process
main "$@"