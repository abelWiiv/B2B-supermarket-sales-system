-- Create extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Roles table
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Privileges table
CREATE TABLE IF NOT EXISTS privileges (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- User-Role mapping table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Role-Privilege mapping table
CREATE TABLE IF NOT EXISTS role_privileges (
    role_id UUID NOT NULL,
    privilege_id UUID NOT NULL,
    PRIMARY KEY (role_id, privilege_id),
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    FOREIGN KEY (privilege_id) REFERENCES privileges(id) ON DELETE CASCADE
);

-- Refresh tokens table
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    token VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Indexes
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_token ON refresh_tokens(token);
CREATE INDEX IF NOT EXISTS idx_refresh_tokens_user_id ON refresh_tokens(user_id);

-- Insert default privileges
INSERT INTO privileges (id, name, description, created_at, updated_at) VALUES
(uuid_generate_v4(), 'READ_USER', 'Read user information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'CREATE_USER', 'Create new users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'UPDATE_USER', 'Update user information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'DELETE_USER', 'Delete users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'MANAGE_ROLES', 'Manage user roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'MANAGE_PRIVILEGES', 'Manage privileges', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'READ_CUSTOMER', 'Read customer information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'CREATE_CUSTOMER', 'Create new customers', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'UPDATE_CUSTOMER', 'Update customer information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'DELETE_CUSTOMER', 'Delete customers', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'READ_PRODUCT', 'Read product information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'CREATE_PRODUCT', 'Create new products', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'UPDATE_PRODUCT', 'Update product information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'DELETE_PRODUCT', 'Delete products', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'READ_CATEGORY', 'Read category information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'CREATE_CATEGORY', 'Create new categories', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'UPDATE_CATEGORY', 'Update category information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'DELETE_CATEGORY', 'Delete categories', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'READ_SUPPLIER', 'Read supplier information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'CREATE_SUPPLIER', 'Create new suppliers', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'UPDATE_SUPPLIER', 'Update supplier information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'DELETE_SUPPLIER', 'Delete suppliers', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'READ_PRICE_LIST', 'Read price list information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'CREATE_PRICE_LIST', 'Create new price lists', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'UPDATE_PRICE_LIST', 'Update price list information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'DELETE_PRICE_LIST', 'Delete price lists', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'READ_SHOP', 'Read shop information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'CREATE_SHOP', 'Create new shops', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'UPDATE_SHOP', 'Update shop information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'DELETE_SHOP', 'Delete shops', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'CREATE_SALES_ORDER', 'Create sales order', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'READ_SALES_ORDER', 'Read sales order', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'UPDATE_SALES_ORDER', 'Update status for sales order', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'DELETE_SALES_ORDER', 'delete sales order', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'CREATE_INVOICE', 'Create invoice', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'READ_INVOICE', 'Read invoice', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'UPDATE_INVOICE', 'Update the status for invoice', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'CONFIRM_SALES_ORDER', 'Update the status for sales order', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)

ON CONFLICT (name) DO NOTHING;

-- Insert default roles
INSERT INTO roles (id, name, description, created_at, updated_at) VALUES
(uuid_generate_v4(), 'ROLE_USER', 'Standard user with basic access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'ROLE_ADMIN', 'Administrator with full access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'ROLE_CUSTOMER_MANAGER', 'Manager for customer operations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'ROLE_PRODUCT_MANAGER', 'Manager for product operations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(uuid_generate_v4(), 'ROLE_SALES_MANAGER', 'Manager for sales operations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (name) DO NOTHING;

-- Assign all privileges to ROLE_ADMIN
INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id
FROM roles r, privileges p
WHERE r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

-- Assign READ_USER privilege to ROLE_USER
INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id
FROM roles r, privileges p
WHERE r.name = 'ROLE_USER' AND p.name = 'READ_USER'
ON CONFLICT DO NOTHING;

-- Assign customer-related privileges to ROLE_CUSTOMER_MANAGER
INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id
FROM roles r, privileges p
WHERE r.name = 'ROLE_CUSTOMER_MANAGER' AND p.name IN ('READ_CUSTOMER', 'CREATE_CUSTOMER', 'UPDATE_CUSTOMER', 'DELETE_CUSTOMER')
ON CONFLICT DO NOTHING;

-- Assign product-related privileges to ROLE_PRODUCT_MANAGER
INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id
FROM roles r, privileges p
WHERE r.name = 'ROLE_PRODUCT_MANAGER' AND p.name IN (
    'READ_PRODUCT', 'CREATE_PRODUCT', 'UPDATE_PRODUCT', 'DELETE_PRODUCT',
    'READ_CATEGORY', 'CREATE_CATEGORY', 'UPDATE_CATEGORY', 'DELETE_CATEGORY',
    'READ_SUPPLIER', 'CREATE_SUPPLIER', 'UPDATE_SUPPLIER', 'DELETE_SUPPLIER')
ON CONFLICT DO NOTHING;

-- Assign price list-related privileges to ROLE_SALES_MANAGER
INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id FROM roles r, privileges p
WHERE r.name = 'ROLE_SALES_MANAGER' AND p.name IN (
    'READ_PRICE_LIST', 'CREATE_PRICE_LIST', 'UPDATE_PRICE_LIST', 'DELETE_PRICE_LIST'
    ,'READ_SHOP', 'CREATE_SHOP', 'UPDATE_SHOP', 'DELETE_SHOP', 'READ_PRODUCT',
    'CREATE_SALES_ORDER', 'READ_SALES_ORDER', 'UPDATE_SALES_ORDER', 'CREATE_INVOICE',
    'READ_INVOICE', 'UPDATE_INVOICE', 'CONFIRM_SALES_ORDER', 'DELETE_SALES_ORDER'
    --Read Product privileges added to sales manager since there is an API call to check the product ID
)
ON CONFLICT DO NOTHING;;

-- Create initial admin user (password: admin123, encrypted with BCrypt)
INSERT INTO users (id, username, email, password, first_name, last_name, is_active, created_at, updated_at)
VALUES (
    uuid_generate_v4(),
    'admin',
    'admin@supermarket.com',
    '$2a$12$RWPMseZ1ibiCV.buirr.IuoUacQAx703z9AA92ntbMMwNHKwmA92C',
    'System',
    'Admin',
    true,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
) ON CONFLICT (username) DO NOTHING;

-- Assign ROLE_ADMIN to the admin user
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;
---- Create extension for UUID generation
--CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--
---- Users table
--CREATE TABLE users (
--    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--    username VARCHAR(50) NOT NULL UNIQUE,
--    email VARCHAR(100) NOT NULL UNIQUE,
--    password VARCHAR(100) NOT NULL,
--    first_name VARCHAR(50),
--    last_name VARCHAR(50),
--    is_active BOOLEAN DEFAULT TRUE,
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--);
--
---- Roles table
--CREATE TABLE roles (
--    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--    name VARCHAR(50) NOT NULL UNIQUE,
--    description VARCHAR(255),
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--);
--
---- Privileges table
--CREATE TABLE privileges (
--    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--    name VARCHAR(50) NOT NULL UNIQUE,
--    description VARCHAR(255),
--    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
--    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
--);
--
---- User-Role mapping table
--CREATE TABLE user_roles (
--    user_id UUID NOT NULL,
--    role_id UUID NOT NULL,
--    PRIMARY KEY (user_id, role_id),
--    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
--    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
--);
--
---- Role-Privilege mapping table
--CREATE TABLE role_privileges (
--    role_id UUID NOT NULL,
--    privilege_id UUID NOT NULL,
--    PRIMARY KEY (role_id, privilege_id),
--    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
--    FOREIGN KEY (privilege_id) REFERENCES privileges(id) ON DELETE CASCADE
--);
--
---- Refresh tokens table
--CREATE TABLE refresh_tokens (
--    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--    user_id UUID NOT NULL,
--    token VARCHAR(255) NOT NULL UNIQUE,
--    expiry_date TIMESTAMP NOT NULL,
--    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
--);
--
---- Indexes
--CREATE INDEX idx_users_email ON users(email);
--CREATE INDEX idx_users_username ON users(username);
--CREATE INDEX idx_refresh_tokens_token ON refresh_tokens(token);
--CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
--
--
---- Ensure UUID extension is enabled
--CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
--
---- Insert default privileges
--INSERT INTO privileges (id, name, description, created_at, updated_at) VALUES
--(uuid_generate_v4(), 'READ_USER', 'Read user information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(uuid_generate_v4(), 'CREATE_USER', 'Create new users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(uuid_generate_v4(), 'UPDATE_USER', 'Update user information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(uuid_generate_v4(), 'DELETE_USER', 'Delete users', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(uuid_generate_v4(), 'MANAGE_ROLES', 'Manage user roles', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(uuid_generate_v4(), 'MANAGE_PRIVILEGES', 'Manage privileges', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
--
---- Insert default roles
--INSERT INTO roles (id, name, description, created_at, updated_at) VALUES
--(uuid_generate_v4(), 'ROLE_USER', 'Standard user with basic access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(uuid_generate_v4(), 'ROLE_ADMIN', 'Administrator with full access', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(uuid_generate_v4(), 'ROLE_CUSTOMER_MANAGER', 'Manager for customer operations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(uuid_generate_v4(), 'ROLE_PRODUCT_MANAGER', 'Manager for product operations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
--(uuid_generate_v4(), 'ROLE_SALES_MANAGER', 'Manager for sales operations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);
--
---- Assign all privileges to ROLE_ADMIN
--INSERT INTO role_privileges (role_id, privilege_id)
--SELECT r.id, p.id
--FROM roles r, privileges p
--WHERE r.name = 'ROLE_ADMIN';
--
---- Assign READ_USER privilege to ROLE_USER
--INSERT INTO role_privileges (role_id, privilege_id)
--SELECT r.id, p.id
--FROM roles r, privileges p
--WHERE r.name = 'ROLE_USER' AND p.name = 'READ_USER';
--
---- Create initial admin user (password: admin123, encrypted with BCrypt)
--INSERT INTO users (id, username, email, password, first_name, last_name, is_active, created_at, updated_at)
--VALUES (
--    uuid_generate_v4(),
--    'admin',
--    'admin@supermarket.com',
--    '$2a$12$RWPMseZ1ibiCV.buirr.IuoUacQAx703z9AA92ntbMMwNHKwmA92C',
--    'System',
--    'Admin',
--    true,
--    CURRENT_TIMESTAMP,
--    CURRENT_TIMESTAMP
--);
--
---- Assign ROLE_ADMIN to the admin user
--INSERT INTO user_roles (user_id, role_id)
--SELECT u.id, r.id
--FROM users u, roles r
--WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';
--
------ Insert default privileges
----INSERT INTO privileges (name, description) VALUES
----('READ_USER', 'Read user information'),
----('CREATE_USER', 'Create new users'),
----('UPDATE_USER', 'Update user information'),
----('DELETE_USER', 'Delete users'),
----('MANAGE_ROLES', 'Manage user roles'),
----('MANAGE_PRIVILEGES', 'Manage privileges');
----
------ Insert default roles
----INSERT INTO roles (name, description) VALUES
----('ROLE_USER', 'Standard user with basic access'),
----('ROLE_ADMIN', 'Administrator with full access'),
----('ROLE_CUSTOMER_MANAGER', 'Manager for customer operations'),
----('ROLE_PRODUCT_MANAGER', 'Manager for product operations'),
----('ROLE_SALES_MANAGER', 'Manager for sales operations');
----
------ Assign privileges to ROLE_ADMIN
----INSERT INTO role_privileges (role_id, privilege_id)
----SELECT r.id, p.id FROM roles r, privileges p
----WHERE r.name = 'ROLE_ADMIN';
----
------ Assign basic privileges to ROLE_USER
----INSERT INTO role_privileges (role_id, privilege_id)
----SELECT r.id, p.id FROM roles r, privileges p
----WHERE r.name = 'ROLE_USER' AND p.name = 'READ_USER';
----
------ Create initial admin user (password: admin123)
----INSERT INTO users (username, email, password, first_name, last_name, is_active)
----VALUES ('admin', 'admin@supermarket.com', '$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.', 'System', 'Admin', true);
----
------ Assign admin role to initial admin user
----INSERT INTO user_roles (user_id, role_id)
----SELECT u.id, r.id FROM users u, roles r
----WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN';