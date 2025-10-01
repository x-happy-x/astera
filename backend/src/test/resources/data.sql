-- Test data for H2 database
INSERT INTO users (id, email, full_name, role, password_hash, is_active, created_at)
VALUES ('11111111-1111-1111-1111-111111111111', 'admin@example.com', 'Administrator', 'admin', '$2a$10$YcZYZNkZJL9Y3HBqZzZQ.O7XvZvZLqZ2qZ3qZ4qZ5qZ6qZ7qZ8qZ9q', TRUE, NOW());