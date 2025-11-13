create table tbl_experience_notice
(
    id                              bigint generated always as identity
        primary key,
    experience_notice_title         varchar(255) not null,
    experience_notice_subtitle      varchar(255) not null,
    experience_notice_introduce_job varchar(255) not null,
    experience_notice_etc           varchar(255) not null,
    experience_start_date           date         not null,
    experience_end_date             date         not null,
    experience_notice_status        status    default 'inactive'::status,
    created_datetime                timestamp default now(),
    updated_datetime                timestamp default now(),
    company_id                      bigint       not null
        constraint fk_experience_notice_company
            references tbl_company,
    experience_main_tasks           varchar(255),
    experience_notice_start_date    date         not null,
    experience_notice_end_date      date         not null
);

-- 순서대로
ALTER TABLE tbl_experience_notice
    ALTER COLUMN experience_notice_status SET DEFAULT 'active'::status;
ALTER TABLE tbl_experience_notice
    ALTER COLUMN experience_notice_status SET NOT NULL;

ALTER TABLE tbl_experience_notice
    ALTER COLUMN experience_main_tasks SET DEFAULT '';
ALTER TABLE tbl_experience_notice
    ALTER COLUMN experience_main_tasks SET NOT NULL;

ALTER TABLE tbl_experience_notice
    ALTER COLUMN company_id SET NOT NULL;
