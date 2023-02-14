/*
SQLyog Ultimate v11.11 (64 bit)
MySQL - 5.7.39-log : Database - test
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE
DATABASE /*!32312 IF NOT EXISTS*/`test` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

USE
`test`;

/*Table structure for table `pf_test` */

DROP TABLE IF EXISTS `pf_test`;

CREATE TABLE `pf_test`
(
    `id`           char(32)     NOT NULL COMMENT '主键ID',
    `create_time`  datetime              DEFAULT NULL COMMENT '创建时间',
    `age`          int(10) DEFAULT NULL COMMENT '年龄',
    `birthday`     date                  DEFAULT NULL COMMENT '生日',
    `name`         varchar(255)          DEFAULT '' COMMENT '名字',
    `sex`          varchar(10)           DEFAULT '男' COMMENT '性别',
    `department`   varchar(100)          DEFAULT '技术部' COMMENT '部门',
    `home_address` varchar(255) NOT NULL DEFAULT '' COMMENT '家庭住址',
    `photo`        varchar(200)          DEFAULT '' COMMENT '头像',
    `is_marry`     bit(1)                DEFAULT b'0' COMMENT '是否结婚(1 是 0 否)',
    `create_id`    varchar(64)           DEFAULT NULL,
    `update_id`    varchar(64)           DEFAULT NULL,
    `update_time`  datetime              DEFAULT NULL,
    `status`       varchar(20)  NOT NULL DEFAULT 'VALID' COMMENT '状态',
    PRIMARY KEY (`id`),
    KEY            `index` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `pf_test` */

insert into `pf_test`(`id`, `create_time`, `age`, `birthday`, `name`, `sex`, `department`, `home_address`, `photo`,
                      `is_marry`, `create_id`, `update_id`, `update_time`, `status`)
values ('402881876b1b10cf016b1b137a070030', '2019-06-03 10:03:10', 20, '2019-06-03', '范冰冰', '男', '信息部', '', '', '\0',
        NULL, NULL, '2020-11-10 09:59:10', 'VALID'),
       ('402881876b1b10cf016b1b137bbe0031', '2019-06-03 10:03:10', 23, '2019-06-03', '张思', '男', '信息部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b137c8a0032', '2019-06-03 10:03:10', 19, '2019-06-03', '牛莉', '女', '技术部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b137d6b0033', '2019-06-03 10:03:11', 33, '2019-06-03', '许绍洋', '男', '技术部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b1384930034', '2019-06-03 10:03:12', 23, '2019-06-03', '林志颖', '男', '财务部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b1385740035', '2019-06-03 10:03:13', 34, '2019-06-03', '林丹', '女', '技术部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b1386470036', '2019-06-03 10:03:13', 23, '2019-06-03', '姚明', '男', '技术部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b1386ff0037', '2019-06-03 10:03:13', 32, '2019-06-03', '库里', '男', '信息部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b1389080038', '2019-06-03 10:03:14', 55, '2019-06-03', '斯蒂芬', '男', '技术部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b1389e00039', '2019-06-03 10:03:14', 45, '2019-06-03', '詹姆斯', '男', '信息部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b138acd003a', '2019-06-03 10:03:14', 34, '2019-06-03', '科比', '男', '技术部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b138ba2003b', '2019-06-03 10:03:14', 32, '2019-06-03', '赵薇', '女', '财务部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b13a9f4003c', '2019-06-03 10:03:22', 38, '2019-06-03', '黄晓明', '男', '技术部', '', '', '\0',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:32:52', 'VALID'),
       ('402881876b1b10cf016b1b13aad2003d', '2019-06-03 10:03:22', 43, '2019-06-03', '刘亦菲', '女', '信息部', '', '', '\0',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:32:51', 'VALID'),
       ('402881876b1b10cf016b1b13adfd003e', '2019-06-03 10:03:23', 38, '2019-06-03', '鹿晗', '男', '技术部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b13af07003f', '2019-06-03 10:03:23', 32, '2019-06-03', '吴亦凡', '男', '财务部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b13b0c40040', '2019-06-03 10:03:24', 24, '2019-06-03', '黄磊', '男', '财务部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b13b1a80041', '2019-06-03 10:03:24', 38, '2019-06-03', '海清', '女', '技术部', '', '', '\0',
        NULL, NULL, NULL, 'VALID'),
       ('402881876b1b10cf016b1b13b46b0042', '2019-06-03 10:03:25', 20, '2019-06-03', '赵文卓', '男', '技术部', '', '', '\0',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID'),
       ('402881876b1b10cf016b1b13b5460043', '2019-06-03 10:03:25', 23, '2019-06-03', '李连杰', '男', '技术部', '', '', '\0',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID'),
       ('402881876b1b10cf016b1b13b6570044', '2019-06-03 10:03:25', 39, '2019-06-03', '乔丹', '男', '技术部', '', '', '\0',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID'),
       ('402881876b1b10cf016b1b13b7380045', '2019-06-03 10:03:25', 43, '2019-06-03', '艾佛森', '女', '技术部', '', '', '\0',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID'),
       ('402881876b1b10cf016b1b13b8170046', '2019-06-03 10:03:26', 58, '2019-06-03', '马龙', '男', '信息部', '', '', '\0',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID'),
       ('402881876b1b10cf016b1b13b9120047', '2019-06-03 10:03:26', 44, '2019-06-03', '张继科', '男', '技术部', '', '', '\0',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID'),
       ('402881876b1b10cf016b1b13bc3d0048', '2019-06-03 10:03:27', 55, '2019-06-03', '张怡宁', '女', '财务部', '', '', '',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:35:26', 'INVALID'),
       ('402881876b1b10cf016b1b13bd540049', '2019-06-03 10:03:27', 22, '2019-06-03', '王楠', '女', '技术部', '', '', '\0',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID'),
       ('402881876b1b10cf016b1b13be2c004a', '2019-06-03 10:03:27', 51, '2019-06-03', '李毅', '男', '技术部', '', '', '\0',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID'),
       ('402881876b1b10cf016b1b13fc84004b', '2019-06-03 10:03:43', 34, '2019-06-03', '梅西', '男', '技术部', '', '', '',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID'),
       ('402881876b1b10cf016b1b13fd43004c', '2019-06-03 10:03:43', 63, '2019-06-03', '姆巴佩', '男', '信息部', '', '', '',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID'),
       ('402881876b1b10cf016b1b13fdf1004d', '2019-06-03 10:03:43', 18, '2019-06-03', '郑秀文', '女', '技术部', '', '', '',
        NULL, '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID'),
       ('bdf2ad663f298be904104b60bbb942de', '2022-09-02 15:47:49', 23, '2022-08-30', 'test322222', '男', 'test123',
        '2323', '2022/09/02/1662104862046/test.jpeg', '', '0210dd81c50b35b0da0bc4144186a5d7',
        '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:35:19', 'INVALID'),
       ('cf81feaaf3cd9b5389c50c988fffd471', '2020-11-10 17:51:53', 100, '2020-11-10', 'string1', 'string', 'string',
        'string', '', '\0', '0210dd81c50b35b0da0bc4144186a5d7', '0210dd81c50b35b0da0bc4144186a5d7',
        '2022-11-23 17:33:16', 'VALID'),
       ('f61d3d3be1650d8b696c4cc32a5e5cc7', '2022-11-23 16:43:18', 23232, NULL, '22323', '男', '2323', '2323',
        '2022/11/23/1669192996590/test.jpeg', '\0', '0210dd81c50b35b0da0bc4144186a5d7',
        '0210dd81c50b35b0da0bc4144186a5d7', '2022-11-23 17:33:16', 'VALID');

/*Table structure for table `pf_user` */

DROP TABLE IF EXISTS `pf_user`;

CREATE TABLE `pf_user`
(
    `id`       varchar(64) NOT NULL,
    `username` varchar(64) NOT NULL COMMENT '用户名',
    `password` varchar(64) NOT NULL COMMENT '密码',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Data for the table `pf_user` */

insert into `pf_user`(`id`, `username`, `password`)
values ('0210dd81c50b35b0da0bc4144186a5d7', 'test', '96e79218965eb72c92a549dd5a330112');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
