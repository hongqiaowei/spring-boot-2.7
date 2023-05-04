--[[
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
--]]

local current = redis.call('GET', KEYS[1])
if current then
    if current == ARGV[1] then
        redis.call('DEL', KEYS[1])
        redis.call('PUBLISH', KEYS[2], KEYS[1])
        return 1
    else
        return 0
    end
else
    return -1
end
