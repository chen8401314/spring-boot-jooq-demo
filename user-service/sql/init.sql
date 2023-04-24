CREATE TABLE `pf_user`
(
    `id`       varchar(20) NOT NULL,
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `password` varchar(64) NOT NULL COMMENT '密码',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
insert into `pf_user`(`id`, `username`, `password`)
values ('1650347742230818817', 'test', '96e79218965eb72c92a549dd5a330112');
CREATE TABLE `pf_test`
(
    `id`         varchar(20)  NOT NULL,
    `created_dt` DATETIME(6) COMMENT '创建时间',
    `created_by` VARCHAR(255) NOT NULL default '' COMMENT '创建人',
    `updated_dt` DATETIME(6) COMMENT '更新时间',
    `updated_by` VARCHAR(255) NOT NULL default '' COMMENT '更新人',
    `name`       VARCHAR(255) NOT NULL default '' COMMENT '名称',
    `birthday`   DATE COMMENT '生日',
    `home`       BIT(1)       NOT NULL default 0 COMMENT '是否在首页',
    `status`     VARCHAR(255) NOT NULL default 'VALID' COMMENT '状态',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
