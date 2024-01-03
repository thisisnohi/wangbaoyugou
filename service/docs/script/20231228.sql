DROP TABLE IF EXISTS team_user;
CREATE TABLE team_user  (
                            id varchar(50) PRIMARY KEY  NOT NULL comment 'id主键',
                            name varchar(100) NOT NULL  comment '姓名',
                            phone varchar(40)  comment '手机号',
                            accesstoken varchar(100) comment 'accesstoken',
                            cardid varchar(100) comment 'cardid',
                            personId varchar(100) comment 'personId'
)comment='团队用户';
;

CREATE TABLE team_sign  (
                            id varchar(50) PRIMARY KEY  NOT NULL comment 'id主键',
                            user_id varchar(50) comment '用户id',
                            date date comment '打卡日期',
                            time varchar(20) comment '打卡时间',
                            sign_time timestamp comment '打卡时间戳'
)comment='打卡记录';


insert into team_user (id, name, phone, accesstoken, cardid, personId)
values ('1', '丁龙海', '18012920403', '4ab231edc9334145929595a33a6484ad','lr1xXRrwLzCiQFCkFzs1FA==','19dbe154031e11ed800a005056b51891');
insert into team_user (id, name, phone, accesstoken, cardid, personId)
values ('2', '杜帅', '18012920403', '9af122279f08403c99488cb0edc50d9a','vW4X9DSl7A1AWfx6YHFUaQ==','19dc44cd031e11ed800a005056b51891');
insert into team_user (id, name, phone, accesstoken, cardid, personId)
values ('3', '郑峰', '', '','','');
insert into team_user (id, name, phone, accesstoken, cardid, personId)
values ('4', '夏志铭', '', '','','');
insert into team_user (id, name, phone, accesstoken, cardid, personId)
values ('5', '李春亭',  '', '','','');
insert into team_user (id, name, phone, accesstoken, cardid, personId)
values ('6', '吕晓朋',  '', '','','');
