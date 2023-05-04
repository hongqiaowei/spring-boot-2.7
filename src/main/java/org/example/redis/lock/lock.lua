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

if (redis.call('EXISTS', KEYS[1]) == 0) then
    redis.call('SET', KEYS[1], ARGV[1])
    redis.call('PEXPIRE', KEYS[1], ARGV[2])
    return -1
else
    return redis.call('PTTL', KEYS[1])
end
