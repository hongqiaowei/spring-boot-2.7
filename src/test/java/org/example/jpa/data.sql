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

INSERT INTO `t_user` (`id`, `name`, `age`) VALUES (10, 'user10', 23);

INSERT INTO `t_role` (`id`, `name`) VALUES (20, 'role20');
INSERT INTO `t_role` (`id`, `name`) VALUES (21, 'role21');

INSERT INTO `t_user_role` (`id`, `usr`, `role`) VALUES (30, 10, 20);
INSERT INTO `t_user_role` (`id`, `usr`, `role`) VALUES (31, 10, 21);
