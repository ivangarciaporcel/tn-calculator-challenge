insert into users (user_id, created_at, created_by, deleted, updated_at, updated_by, balance, password, status, username)
VALUES ('2e51aa81-1533-4434-8eca-4db958d0ebe9', now(), 'system', false, now(), 'system', 0.0, '$2a$12$G779iqLSUuZ62km7L211iuGX6tK7yXqcrRskAKV59UKoKT7YJ2R1O', 'ACTIVE', 'admin@tncalculator.com');

INSERT INTO user_authorities (user_user_id, authorities) VALUES ('2e51aa81-1533-4434-8eca-4db958d0ebe9', E'\\xACED000573720030636F6D2E746E63616C63756C61746F722E63616C63756C61746F726170692E646F6D61696E2E6D6F64656C2E526F6C65B1E70A867E8F43280200014C0009617574686F726974797400124C6A6176612F6C616E672F537472696E673B787074000A555345525F41444D494E');

insert into operations (id, created_at, created_by, deleted, updated_at, updated_by, cost, status, type) VALUES (gen_random_uuid(), now(), 'system', false, now(), 'system', 10.0, 'APPROVED', 'ADDITION');
insert into operations (id, created_at, created_by, deleted, updated_at, updated_by, cost, status, type) VALUES (gen_random_uuid(), now(), 'system', false, now(), 'system', 10.0, 'APPROVED', 'SUBTRACTION');
insert into operations (id, created_at, created_by, deleted, updated_at, updated_by, cost, status, type) VALUES (gen_random_uuid(), now(), 'system', false, now(), 'system', 20.0, 'APPROVED', 'MULTIPLICATION');
insert into operations (id, created_at, created_by, deleted, updated_at, updated_by, cost, status, type) VALUES (gen_random_uuid(), now(), 'system', false, now(), 'system', 20.0, 'APPROVED', 'DIVISION');
insert into operations (id, created_at, created_by, deleted, updated_at, updated_by, cost, status, type) VALUES (gen_random_uuid(), now(), 'system', false, now(), 'system', 30.0, 'APPROVED', 'SQUARE_ROOT');
insert into operations (id, created_at, created_by, deleted, updated_at, updated_by, cost, status, type) VALUES (gen_random_uuid(), now(), 'system', false, now(), 'system', 40.0, 'APPROVED', 'RANDOM_STRING');
