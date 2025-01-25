DROP TABLE IF EXISTS KQ_MEMBERS;
create table KQ_MEMBERS
(
    fuid        varchar(200) primary key           not null comment '用户ID',
    CREATED_TS  datetime default CURRENT_TIMESTAMP null comment '记录创建时间',
    SNO         varchar(200)                       not null comment '序号',
    USERNAME    varchar(200)                       not null comment '姓名',
    NICKNAME    varchar(200) comment '妮称',
    ICON        varchar(200) comment '头像',
    USER_STATUS varchar(10) comment '人员状态： ON-有效； OFF-失效',
    REMARK1     varchar(512) comment '备用字段1',
    REMARK2     varchar(1024) comment '备用字段2'
)
    comment '打卡人员';

DROP TABLE IF EXISTS KQ_QUN_100;
CREATE TABLE KQ_QUN_100
(
    CREATED_TS     DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    WORK_DATE      DATE         NOT NULL COMMENT '工作日',
    USERNAME       VARCHAR(512) NOT NULL COMMENT '姓名',
    CARD_TIME_DOOR DATETIME COMMENT '打卡时间-门禁',
    CARD_TIME_APP  DATETIME COMMENT '打卡时间-移动APP',
    REMARK1        VARCHAR(512) COMMENT '备用字段1',
    REMARK2        VARCHAR(1024) COMMENT '备用字段2'
) COMMENT '群报数打卡数据';

DROP TABLE IF EXISTS PUB_PARA;
CREATE TABLE PUB_PARA
(
    id         varchar(50) primary key not null comment 'ID',
    CREATED_TS DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    PARA_NAME  VARCHAR(100)            NOT NULL COMMENT '参数名',
    PARA_VALUE VARCHAR(200)            NOT NULL COMMENT '参数值',
    REMARK1    VARCHAR(512) COMMENT '备用字段1',
    REMARK2    VARCHAR(1024) COMMENT '备用字段2'
) COMMENT '公用参数';

