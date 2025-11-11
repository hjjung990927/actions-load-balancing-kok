create type request_status as enum('await','accept','reject');

create type status as enum('active', 'inactive');

create type role as enum('member', 'admin', 'company');

create table tbl_user
(
    id               bigint generated always as identity
        primary key,
    user_name        varchar(255)                       not null,
    user_phone       varchar(255)                       not null,
    user_email       varchar(255)
        unique,
    user_password    varchar(255),
    user_role        role                               not null,
    user_status      status    default 'active'::status not null,
    created_datetime timestamp default now(),
    updated_datetime timestamp default now(),
    sns_email        varchar(255)
);
delete from tbl_user_job_category where user_id = 25;
select * from tbl_user_job_category;
insert into tbl_user_job_category(user_id, job_category)  values (25,9);
select * from tbl_job_category;
select * from tbl_company_sector;
select * from tbl_user where user_role = 'company';
select * from tbl_follow;
