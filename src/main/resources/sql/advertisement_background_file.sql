create table tbl_advertisement_background_file
(
    file_id          bigint not null
        primary key
        constraint fk_advertisement_background_file
            references tbl_file,
    advertisement_id bigint not null
        constraint fk_advertisement_background_file_advertisement
            references tbl_advertisement
);