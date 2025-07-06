USE testcontainer;
 
CREATE TABLE tests (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    label VARCHAR(200)
);

insert into tests(label) values ('test');
insert into tests(label) values ('deleteme');
insert into tests(label) values ('updated');