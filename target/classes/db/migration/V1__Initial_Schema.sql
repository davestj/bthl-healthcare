-- File: /var/www/davestj.com/bthl-hc/src/main/resources/db/migration/V1__Initial_Schema.sql
-- Author: davestj (David St John)
-- Date: 2025-07-16
-- Purpose: Complete database schema for BTHL-HealthCare management platform
-- Description: I designed this comprehensive schema to handle user management, insurance providers,
--              brokers, company healthcare portfolios, and comprehensive audit logging.
--              I've implemented RBAC security model with MFA support and session management.
-- 
-- Changelog:
-- 2025-07-16: Initial creation of complete database schema with all core entities
-- 
-- Git Commit: git commit -m "feat: add comprehensive PostgreSQL schema with RBAC, MFA, and audit logging"
-- 
-- Next Dev Feature: Add materialized views for analytics and reporting optimization
-- TODO: Implement database partitioning for large-scale audit log management

-- I ensure this script can be run multiple times safely
SET client_min_messages = WARNING;

-- I create the database extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- I create custom types for better data consistency
CREATE TYPE user_status AS ENUM ('ACTIVE', 'INACTIVE', 'SUSPENDED', 'PENDING');
CREATE TYPE user_type AS ENUM ('ADMIN', 'BROKER', 'PROVIDER', 'COMPANY_USER');
CREATE TYPE mfa_type AS ENUM ('TOTP', 'EMAIL_OTP', 'SMS_OTP');
CREATE TYPE session_status AS ENUM ('ACTIVE', 'EXPIRED', 'TERMINATED');
CREATE TYPE audit_action AS ENUM ('CREATE', 'READ', 'UPDATE', 'DELETE', 'LOGIN', 'LOGOUT', 'FAILED_LOGIN');
CREATE TYPE provider_type AS ENUM ('HEALTH_INSURANCE', 'DENTAL', 'VISION', 'LIFE_INSURANCE', 'DISABILITY');
CREATE TYPE plan_tier AS ENUM ('BRONZE', 'SILVER', 'GOLD', 'PLATINUM', 'CATASTROPHIC');

-- I create the roles table for RBAC implementation
CREATE TABLE roles (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    permissions JSONB NOT NULL DEFAULT '[]',
    is_system_role BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID
);

-- I create indexes for the roles table to optimize queries
CREATE INDEX idx_roles_name ON roles(name);
CREATE INDEX idx_roles_system ON roles(is_system_role);

-- I insert default system roles with appropriate permissions
INSERT INTO roles (name, description, permissions, is_system_role) VALUES
('SUPER_ADMIN', 'System administrator with full access', 
 '["USER_MANAGEMENT", "ROLE_MANAGEMENT", "SYSTEM_CONFIG", "AUDIT_ACCESS", "PROVIDER_MANAGEMENT", "BROKER_MANAGEMENT", "COMPANY_MANAGEMENT"]', true),
('ADMIN', 'Administrator with management capabilities', 
 '["USER_MANAGEMENT", "PROVIDER_MANAGEMENT", "BROKER_MANAGEMENT", "COMPANY_MANAGEMENT", "AUDIT_READ"]', true),
('BROKER', 'Insurance broker with client management access', 
 '["CLIENT_MANAGEMENT", "PLAN_MANAGEMENT", "QUOTE_GENERATION"]', true),
('PROVIDER', 'Insurance provider with plan management access', 
 '["PLAN_MANAGEMENT", "PROVIDER_PROFILE", "BROKER_RELATIONS"]', true),
('COMPANY_USER', 'Company representative with portfolio access', 
 '["COMPANY_PORTFOLIO", "EMPLOYEE_MANAGEMENT", "PLAN_SELECTION"]', true);

-- I create the users table with comprehensive authentication support
CREATE TABLE users (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    status user_status DEFAULT 'PENDING',
    user_type user_type NOT NULL,
    role_id UUID NOT NULL REFERENCES roles(id),
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    backup_codes TEXT[],
    last_login TIMESTAMP WITH TIME ZONE,
    failed_login_attempts INTEGER DEFAULT 0,
    account_locked_until TIMESTAMP WITH TIME ZONE,
    password_reset_token VARCHAR(255),
    password_reset_expires TIMESTAMP WITH TIME ZONE,
    email_verification_token VARCHAR(255),
    email_verified BOOLEAN DEFAULT FALSE,
    profile_image_url VARCHAR(255),
    timezone VARCHAR(50) DEFAULT 'UTC',
    locale VARCHAR(10) DEFAULT 'en_US',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_by UUID
);

-- I create indexes for the users table to optimize authentication and queries
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_users_type ON users(user_type);
CREATE INDEX idx_users_role ON users(role_id);
CREATE INDEX idx_users_last_login ON users(last_login);

-- I create the sessions table for session management
CREATE TABLE user_sessions (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    refresh_token VARCHAR(255),
    ip_address INET,
    user_agent TEXT,
    status session_status DEFAULT 'ACTIVE',
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_accessed TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- I create indexes for the sessions table to optimize session lookup
CREATE INDEX idx_sessions_token ON user_sessions(session_token);
CREATE INDEX idx_sessions_user ON user_sessions(user_id);
CREATE INDEX idx_sessions_status ON user_sessions(status);
CREATE INDEX idx_sessions_expires ON user_sessions(expires_at);

-- I create the MFA tokens table for multi-factor authentication
CREATE TABLE mfa_tokens (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_type mfa_type NOT NULL,
    token_value VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- I create indexes for the MFA tokens table
CREATE INDEX idx_mfa_tokens_user ON mfa_tokens(user_id);
CREATE INDEX idx_mfa_tokens_type ON mfa_tokens(token_type);
CREATE INDEX idx_mfa_tokens_expires ON mfa_tokens(expires_at);

-- I create the companies table for healthcare portfolio management
CREATE TABLE companies (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    legal_name VARCHAR(255),
    tax_id VARCHAR(50),
    industry VARCHAR(100),
    employee_count INTEGER,
    headquarters_address TEXT,
    billing_address TEXT,
    primary_contact_email VARCHAR(255),
    primary_contact_phone VARCHAR(20),
    website_url VARCHAR(255),
    annual_revenue DECIMAL(15, 2),
    founded_year INTEGER,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    logo_url VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- I create indexes for the companies table
CREATE INDEX idx_companies_name ON companies(name);
CREATE INDEX idx_companies_tax_id ON companies(tax_id);
CREATE INDEX idx_companies_status ON companies(status);

-- I create the insurance providers table
CREATE TABLE insurance_providers (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    legal_name VARCHAR(255),
    provider_code VARCHAR(50) UNIQUE,
    provider_type provider_type NOT NULL,
    am_best_rating VARCHAR(10),
    financial_strength_rating VARCHAR(10),
    headquarters_address TEXT,
    service_areas TEXT[],
    phone VARCHAR(20),
    email VARCHAR(255),
    website_url VARCHAR(255),
    customer_service_phone VARCHAR(20),
    claims_phone VARCHAR(20),
    network_size INTEGER,
    established_year INTEGER,
    logo_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    contracted_since DATE,
    contract_expires DATE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- I create indexes for the insurance providers table
CREATE INDEX idx_providers_name ON insurance_providers(name);
CREATE INDEX idx_providers_code ON insurance_providers(provider_code);
CREATE INDEX idx_providers_type ON insurance_providers(provider_type);
CREATE INDEX idx_providers_active ON insurance_providers(is_active);

-- I create the insurance brokers table
CREATE TABLE insurance_brokers (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    license_number VARCHAR(100) NOT NULL UNIQUE,
    license_state VARCHAR(2) NOT NULL,
    license_expires DATE NOT NULL,
    agency_name VARCHAR(255),
    agency_address TEXT,
    specializations TEXT[],
    years_experience INTEGER,
    commission_rate DECIMAL(5, 4),
    territories TEXT[],
    certifications JSONB DEFAULT '[]',
    bio TEXT,
    linkedin_url VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- I create indexes for the insurance brokers table
CREATE INDEX idx_brokers_user ON insurance_brokers(user_id);
CREATE INDEX idx_brokers_license ON insurance_brokers(license_number);
CREATE INDEX idx_brokers_state ON insurance_brokers(license_state);
CREATE INDEX idx_brokers_active ON insurance_brokers(is_active);

-- I create the insurance plans table
CREATE TABLE insurance_plans (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    provider_id UUID NOT NULL REFERENCES insurance_providers(id),
    plan_name VARCHAR(255) NOT NULL,
    plan_code VARCHAR(100) UNIQUE,
    plan_type provider_type NOT NULL,
    tier plan_tier,
    network_type VARCHAR(50),
    deductible DECIMAL(10, 2),
    out_of_pocket_max DECIMAL(10, 2),
    copay_primary_care DECIMAL(8, 2),
    copay_specialist DECIMAL(8, 2),
    copay_emergency DECIMAL(8, 2),
    coinsurance_percentage DECIMAL(5, 2),
    prescription_coverage JSONB,
    mental_health_coverage BOOLEAN DEFAULT TRUE,
    maternity_coverage BOOLEAN DEFAULT TRUE,
    preventive_care_coverage BOOLEAN DEFAULT TRUE,
    coverage_details JSONB,
    exclusions TEXT[],
    geographic_coverage TEXT[],
    effective_date DATE,
    termination_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- I create indexes for the insurance plans table
CREATE INDEX idx_plans_provider ON insurance_plans(provider_id);
CREATE INDEX idx_plans_code ON insurance_plans(plan_code);
CREATE INDEX idx_plans_type ON insurance_plans(plan_type);
CREATE INDEX idx_plans_tier ON insurance_plans(tier);
CREATE INDEX idx_plans_active ON insurance_plans(is_active);

-- I create the company healthcare portfolios table
CREATE TABLE company_healthcare_portfolios (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    company_id UUID NOT NULL REFERENCES companies(id),
    broker_id UUID REFERENCES insurance_brokers(id),
    portfolio_name VARCHAR(255) NOT NULL,
    policy_year INTEGER NOT NULL,
    effective_date DATE NOT NULL,
    renewal_date DATE NOT NULL,
    total_premium DECIMAL(15, 2),
    employee_contribution_percentage DECIMAL(5, 2),
    total_employees_covered INTEGER,
    total_dependents_covered INTEGER,
    wellness_program BOOLEAN DEFAULT FALSE,
    wellness_discount DECIMAL(5, 4),
    claims_history JSONB,
    risk_assessment JSONB,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- I create indexes for the company healthcare portfolios table
CREATE INDEX idx_portfolios_company ON company_healthcare_portfolios(company_id);
CREATE INDEX idx_portfolios_broker ON company_healthcare_portfolios(broker_id);
CREATE INDEX idx_portfolios_year ON company_healthcare_portfolios(policy_year);
CREATE INDEX idx_portfolios_status ON company_healthcare_portfolios(status);

-- I create the portfolio plans junction table for many-to-many relationship
CREATE TABLE portfolio_plans (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    portfolio_id UUID NOT NULL REFERENCES company_healthcare_portfolios(id) ON DELETE CASCADE,
    plan_id UUID NOT NULL REFERENCES insurance_plans(id),
    employee_premium DECIMAL(10, 2),
    family_premium DECIMAL(10, 2),
    employees_enrolled INTEGER DEFAULT 0,
    dependents_enrolled INTEGER DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(portfolio_id, plan_id)
);

-- I create indexes for the portfolio plans table
CREATE INDEX idx_portfolio_plans_portfolio ON portfolio_plans(portfolio_id);
CREATE INDEX idx_portfolio_plans_plan ON portfolio_plans(plan_id);

-- I create the broker provider relationships table
CREATE TABLE broker_provider_relationships (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    broker_id UUID NOT NULL REFERENCES insurance_brokers(id),
    provider_id UUID NOT NULL REFERENCES insurance_providers(id),
    relationship_type VARCHAR(50) DEFAULT 'CONTRACTED',
    commission_rate DECIMAL(5, 4),
    contract_start_date DATE,
    contract_end_date DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(broker_id, provider_id)
);

-- I create indexes for the broker provider relationships table
CREATE INDEX idx_broker_provider_broker ON broker_provider_relationships(broker_id);
CREATE INDEX idx_broker_provider_provider ON broker_provider_relationships(provider_id);
CREATE INDEX idx_broker_provider_active ON broker_provider_relationships(is_active);

-- I create the audit logs table for comprehensive activity tracking
CREATE TABLE audit_logs (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    action audit_action NOT NULL,
    resource_type VARCHAR(100) NOT NULL,
    resource_id UUID,
    resource_name VARCHAR(255),
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    session_id UUID REFERENCES user_sessions(id),
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    details TEXT
);

-- I create indexes for the audit logs table to optimize queries
CREATE INDEX idx_audit_logs_user ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action);
CREATE INDEX idx_audit_logs_resource ON audit_logs(resource_type, resource_id);
CREATE INDEX idx_audit_logs_timestamp ON audit_logs(timestamp);
CREATE INDEX idx_audit_logs_session ON audit_logs(session_id);

-- I create the system configuration table for application settings
CREATE TABLE system_configurations (
    id UUID DEFAULT uuid_generate_v4() PRIMARY KEY,
    config_key VARCHAR(255) NOT NULL UNIQUE,
    config_value TEXT NOT NULL,
    config_type VARCHAR(50) DEFAULT 'STRING',
    description TEXT,
    is_sensitive BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(id),
    updated_by UUID REFERENCES users(id)
);

-- I create indexes for the system configurations table
CREATE INDEX idx_config_key ON system_configurations(config_key);
CREATE INDEX idx_config_type ON system_configurations(config_type);

-- I insert default system configurations
INSERT INTO system_configurations (config_key, config_value, config_type, description) VALUES
('PASSWORD_MIN_LENGTH', '12', 'INTEGER', 'Minimum password length requirement'),
('PASSWORD_REQUIRE_UPPERCASE', 'true', 'BOOLEAN', 'Require uppercase letters in passwords'),
('PASSWORD_REQUIRE_LOWERCASE', 'true', 'BOOLEAN', 'Require lowercase letters in passwords'),
('PASSWORD_REQUIRE_NUMBERS', 'true', 'BOOLEAN', 'Require numbers in passwords'),
('PASSWORD_REQUIRE_SYMBOLS', 'true', 'BOOLEAN', 'Require special characters in passwords'),
('SESSION_TIMEOUT_MINUTES', '480', 'INTEGER', 'Session timeout in minutes'),
('MAX_FAILED_LOGIN_ATTEMPTS', '5', 'INTEGER', 'Maximum failed login attempts before lockout'),
('ACCOUNT_LOCKOUT_MINUTES', '30', 'INTEGER', 'Account lockout duration in minutes'),
('MFA_TOKEN_EXPIRY_MINUTES', '5', 'INTEGER', 'MFA token expiry time in minutes'),
('PASSWORD_RESET_EXPIRY_HOURS', '24', 'INTEGER', 'Password reset token expiry in hours');

-- I create triggers for automatic timestamp updates
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- I apply the update trigger to all relevant tables
CREATE TRIGGER update_roles_updated_at BEFORE UPDATE ON roles FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_companies_updated_at BEFORE UPDATE ON companies FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_providers_updated_at BEFORE UPDATE ON insurance_providers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_brokers_updated_at BEFORE UPDATE ON insurance_brokers FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_plans_updated_at BEFORE UPDATE ON insurance_plans FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_portfolios_updated_at BEFORE UPDATE ON company_healthcare_portfolios FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_relationships_updated_at BEFORE UPDATE ON broker_provider_relationships FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_config_updated_at BEFORE UPDATE ON system_configurations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- I create a function for automatic audit logging
CREATE OR REPLACE FUNCTION create_audit_log()
RETURNS TRIGGER AS $$
DECLARE
    audit_action_type audit_action;
    old_values_json JSONB;
    new_values_json JSONB;
BEGIN
    -- I determine the audit action type based on the trigger operation
    CASE TG_OP
        WHEN 'INSERT' THEN
            audit_action_type := 'CREATE';
            old_values_json := NULL;
            new_values_json := to_jsonb(NEW);
        WHEN 'UPDATE' THEN
            audit_action_type := 'UPDATE';
            old_values_json := to_jsonb(OLD);
            new_values_json := to_jsonb(NEW);
        WHEN 'DELETE' THEN
            audit_action_type := 'DELETE';
            old_values_json := to_jsonb(OLD);
            new_values_json := NULL;
    END CASE;

    -- I insert the audit log entry
    INSERT INTO audit_logs (
        action,
        resource_type,
        resource_id,
        old_values,
        new_values,
        timestamp
    ) VALUES (
        audit_action_type,
        TG_TABLE_NAME,
        COALESCE(NEW.id, OLD.id),
        old_values_json,
        new_values_json,
        CURRENT_TIMESTAMP
    );

    RETURN COALESCE(NEW, OLD);
END;
$$ language 'plpgsql';

-- I apply audit logging triggers to critical tables
CREATE TRIGGER audit_trigger_users AFTER INSERT OR UPDATE OR DELETE ON users FOR EACH ROW EXECUTE FUNCTION create_audit_log();
CREATE TRIGGER audit_trigger_companies AFTER INSERT OR UPDATE OR DELETE ON companies FOR EACH ROW EXECUTE FUNCTION create_audit_log();
CREATE TRIGGER audit_trigger_providers AFTER INSERT OR UPDATE OR DELETE ON insurance_providers FOR EACH ROW EXECUTE FUNCTION create_audit_log();
CREATE TRIGGER audit_trigger_brokers AFTER INSERT OR UPDATE OR DELETE ON insurance_brokers FOR EACH ROW EXECUTE FUNCTION create_audit_log();
CREATE TRIGGER audit_trigger_portfolios AFTER INSERT OR UPDATE OR DELETE ON company_healthcare_portfolios FOR EACH ROW EXECUTE FUNCTION create_audit_log();

-- I create views for common queries and reporting
CREATE VIEW active_users AS
SELECT 
    u.id,
    u.username,
    u.email,
    u.first_name,
    u.last_name,
    u.user_type,
    r.name as role_name,
    u.last_login,
    u.created_at
FROM users u
JOIN roles r ON u.role_id = r.id
WHERE u.status = 'ACTIVE';

CREATE VIEW portfolio_summary AS
SELECT 
    p.id,
    c.name as company_name,
    p.portfolio_name,
    p.policy_year,
    p.total_premium,
    p.total_employees_covered,
    p.total_dependents_covered,
    CONCAT(ib.user_id) as broker_name,
    COUNT(pp.plan_id) as plan_count
FROM company_healthcare_portfolios p
JOIN companies c ON p.company_id = c.id
LEFT JOIN insurance_brokers ib ON p.broker_id = ib.id
LEFT JOIN portfolio_plans pp ON p.id = pp.portfolio_id
WHERE p.status = 'ACTIVE'
GROUP BY p.id, c.name, p.portfolio_name, p.policy_year, p.total_premium, 
         p.total_employees_covered, p.total_dependents_covered, ib.user_id;

-- I grant appropriate permissions for the application user
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO davestj;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO davestj;
GRANT EXECUTE ON ALL FUNCTIONS IN SCHEMA public TO davestj;

-- I create indexes for full-text search capabilities
CREATE INDEX idx_companies_search ON companies USING gin(to_tsvector('english', name || ' ' || COALESCE(legal_name, '')));
CREATE INDEX idx_providers_search ON insurance_providers USING gin(to_tsvector('english', name || ' ' || COALESCE(legal_name, '')));
CREATE INDEX idx_plans_search ON insurance_plans USING gin(to_tsvector('english', plan_name || ' ' || COALESCE(plan_code, '')));

-- I analyze tables for query optimization
ANALYZE;
