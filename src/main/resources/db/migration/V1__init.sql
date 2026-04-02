create table if not exists users
(
    id        bigserial
        primary key,
    name      varchar(255),
    email     varchar(255)
        unique,
    password  varchar(255)
        unique,
    user_role varchar(255)
);

alter table users
    owner to postgres;

create table if not exists flyway_schema_history
(
    installed_rank integer                 not null
        constraint flyway_schema_history_pk
            primary key,
    version        varchar(50),
    description    varchar(200)            not null,
    type           varchar(20)             not null,
    script         varchar(1000)           not null,
    checksum       integer,
    installed_by   varchar(100)            not null,
    installed_on   timestamp default now() not null,
    execution_time integer                 not null,
    success        boolean                 not null
);

alter table flyway_schema_history
    owner to postgres;

create index if not exists flyway_schema_history_s_idx
    on flyway_schema_history (success);

create table if not exists chats
(
    id    bigserial
        primary key,
    title varchar(30),
    type  varchar(20)
        constraint chats_type_check
            check ((type)::text = ANY ((ARRAY ['PERSONAL'::character varying, 'GROUP'::character varying])::text[]))
);

alter table chats
    owner to postgres;

create table if not exists notifications
(
    id      bigserial
        primary key,
    message varchar(255) not null,
    user_id bigint
        references users,
    chat_id bigint
        constraint notifications_chats_fk
            references chats
);

alter table notifications
    owner to postgres;

create table if not exists participants
(
    id        bigserial
        primary key,
    user_id   bigint not null
        constraint fk_user_participant
            references users,
    chat_id   bigint not null
        constraint fk_chat_participant
            references chats,
    chat_role varchar(20)
        constraint participants_chat_role_check
            check ((chat_role)::text = ANY ((ARRAY ['MEMBER'::character varying, 'ADMIN'::character varying])::text[])),
    constraint uk_chat_user
        unique (chat_id, user_id)
);

alter table participants
    owner to postgres;

create table if not exists message_statuses
(
    id              bigserial
        primary key,
    notification_id bigint not null
        references notifications
            on delete cascade,
    user_id         bigint not null
        references users
            on delete cascade,
    status          varchar(20)
        constraint message_statuses_status_check
            check ((status)::text = ANY
                   ((ARRAY ['отправлено'::character varying, 'получено'::character varying])::text[]))
);

alter table message_statuses
    owner to postgres;

