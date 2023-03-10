create table operations (id uuid not null, created_at timestamp(6), created_by uuid, deleted boolean, updated_at timestamp(6), updated_by uuid, cost float(53) not null, status varchar(255) not null, type varchar(255) not null, primary key (id));
create table records (id uuid not null, amount float(53) not null, created_at timestamp(6), created_by uuid, deleted boolean, updated_at timestamp(6), updated_by uuid, operation_response varchar(255) not null, user_balance float(53) not null, operation_id uuid, user_id uuid, primary key (id));
create table user_authorities (user_user_id uuid not null, authorities bytea not null, primary key (user_user_id, authorities));
create table users (user_id uuid not null, created_at timestamp(6), created_by uuid, deleted boolean, updated_at timestamp(6), updated_by uuid, balance float(53) not null, password varchar(255) not null, status varchar(255) not null, username varchar(255) not null, primary key (user_id));
alter table if exists records add constraint FKdkqyxwgnb4c1pbv44hqv3vn0t foreign key (operation_id) references operations;
alter table if exists records add constraint FK6p95uajgka0j0dc9vlbjw1sf1 foreign key (user_id) references users;
alter table if exists user_authorities add constraint FKswqqnqcyqob25xy8l2iw1mi9 foreign key (user_user_id) references users;
