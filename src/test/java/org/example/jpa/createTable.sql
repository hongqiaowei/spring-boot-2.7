/*
    Copyright (C) 2023 Hong Qiaowei <hongqiaowei@163.com>. All rights reserved.
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    any later version.
    
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License
    along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

DROP TABLE if EXISTS `t_user`;
CREATE TABLE `t_user` (
	`id`   BIGINT(64)  NOT NULL,
	`name` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_unicode_ci',	
	`age`  TINYINT(2)  UNSIGNED NOT NULL,
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_unicode_ci'
ENGINE=InnoDB
;

DROP TABLE if EXISTS `t_role`;
CREATE TABLE `t_role` (
	`id`   BIGINT(64)  NOT NULL,
	`name` VARCHAR(50) NOT NULL COLLATE 'utf8mb4_unicode_ci',	
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_unicode_ci'
ENGINE=InnoDB
;

DROP TABLE if EXISTS `t_user_role`; -- user和role的关联表
CREATE TABLE `t_user_role` (
	`id`   BIGINT(64) NOT NULL,
	`usr`  BIGINT(64) NOT NULL,     -- t_user的id
	`role` BIGINT(64) NOT NULL,     -- t_role的id
	PRIMARY KEY (`id`) USING BTREE
)
COLLATE='utf8mb4_unicode_ci'
ENGINE=InnoDB
;
