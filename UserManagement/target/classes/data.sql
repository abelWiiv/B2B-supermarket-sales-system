INSERT INTO privileges (name, description) VALUES
('READ_USER', 'Read user information'),
('CREATE_USER', 'Create new users'),
('UPDATE_USER', 'Update user information'),
('DELETE_USER', 'Delete users'),
('MANAGE_ROLES', 'Manage user roles'),
('MANAGE_PRIVILEGES', 'Manage privileges')
ON CONFLICT (name) DO NOTHING;

INSERT INTO roles (name, description) VALUES
('ROLE_USER', 'Standard user with basic access'),
('ROLE_ADMIN', 'Administrator with full access'),
('ROLE_CUSTOMER_MANAGER', 'Manager for customer operations'),
('ROLE_PRODUCT_MANAGER', 'Manager for product operations'),
('ROLE_SALES_MANAGER', 'Manager for sales operations')
ON CONFLICT (name) DO NOTHING;

INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id FROM roles r, privileges p
WHERE r.name = 'ROLE_ADMIN'
ON CONFLICT (role_id, privilege_id) DO NOTHING;

INSERT INTO role_privileges (role_id, privilege_id)
SELECT r.id, p.id FROM roles r, privileges p
WHERE r.name = 'ROLE_USER' AND p.name = 'READ_USER'
ON CONFLICT (role_id, privilege_id) DO NOTHING;

INSERT INTO users (username, email, password, first_name, last_name, is_active)
VALUES ('admin', 'admin@supermarket.com', '$2a$10$XURPShQNCsLjp1ESc2laoObo9QZDhxz73hJPaEv7/cBha4pk0AgP.', 'System', 'Admin', true)
ON CONFLICT (username) DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.username = 'admin' AND r.name = 'ROLE_ADMIN'
ON CONFLICT (user_id, role_id) DO NOTHING;