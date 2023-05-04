/*
 *  Copyright (C) 2023 Hong Qiaowei <hongqiaowei@163.com>. All rights reserved.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package org.example.jpa;

import org.example.util.ThreadContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.query.spi.NativeQueryImplementor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Hong Qiaowei
 */
@Repository
@Transactional(readOnly = true)
public interface UserRepository extends BaseJpaRepository<User, Long> {

    /**
     * 演示通过default方法自定义操作逻辑
     */
    public default Collection<User> customMethodAndNativeQuery() {
        SessionImplementor session = getSession();

        // 下面演示关联查询的处理

        NativeQueryImplementor nativeQuery = session.createNativeQuery(
                                                                           "SELECT   {u.*}, {r.*}                                      " +
                                                                           "FROM     t_user u, t_user_role ur, t_role r                " +
                                                                           "WHERE    u.id = ur.usr  and  ur.role = r.id  and  u.id = ? "
                                             );
        nativeQuery.setParameter(1, 10);
        nativeQuery.addEntity   ("u", User.class);
        nativeQuery.addEntity   ("r", Role.class);

        List rows = nativeQuery.list();

        // 因为User和Role的关联已标@Transient注解，所以像下面这样联系User和Role，没有自动化，有遗憾，就当mybatis关联的xml配置吧
        LinkedHashMap<Long, User> userMap = ThreadContext.getLinkedHashMap();
        for (int i = 0; i < rows.size(); i++) {
            Object[] entities = (Object[]) rows.get(i);
            User u = (User) entities[0];
            // session.detach(u); // 把实体置为游离态
            Role r = (Role) entities[1];
            // session.detach(r);
            Long uid = u.getId();
            if (!userMap.containsKey(uid)) {
                userMap.put(uid, u);
            }
            List<Role> roles = u.getRoles();
            if (roles == null) {
                roles = new ArrayList<>();
                u.setRoles(roles);
            }
            roles.add(r);
        }

        return userMap.values();
    }
}
