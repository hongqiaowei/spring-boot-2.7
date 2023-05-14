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

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

/**
 * @author Hong Qiaowei
 */
@SpringJUnitConfig(classes = {JpaTestConfig.class})
@TestPropertySource("/application.properties")
public class UserRepositoryTests {

    @Resource
    JdbcTemplate jdbcTemplate;

    @Resource
    UserRepository userRepository;



    @Test
    @Transactional
    @Rollback(true) // 只回滚数据，不回滚ddl
    @Sql({"/org/example/jpa/createTable.sql", "/org/example/jpa/data.sql"})
    @Sql(
            scripts        = "/org/example/jpa/dropTable.sql",
            executionPhase = AFTER_TEST_METHOD
    )
    void test() {
        int rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "t_user", "name = 'user10'");
        assertEquals(rows, 1);
    }



    /**
     * 新增实体测试
     */
    @Test
    @Transactional
    @Rollback(true)
    @Sql("/org/example/jpa/createTable.sql")
    @Sql(
            scripts        = "/org/example/jpa/dropTable.sql",
            executionPhase = AFTER_TEST_METHOD
    )
    void addTest() {
        User user = new User();
        user.setName("user");
        user.setAge(28);
        Role role = new Role();
        role.setName("role");
        List<Role> roles = Collections.singletonList(role);
        user.setRoles(roles); // 不会持久到数据库
        User save = userRepository.save(user);
        userRepository.getEntityManager().flush();

        long saveId = save.getId();
        int rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "t_user", "age = 28");
        assertEquals(1, rows);
        Long userId = jdbcTemplate.queryForObject("select id from t_user where age = 28", Long.class);
        assertEquals(saveId, userId);
        rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "t_user_role");
        assertEquals(0, rows);
        rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "t_role");
        assertEquals(0, rows);
    }



    /**
     * 事务测试
     */
    @Test
    @Transactional // 1
    @Sql("/org/example/jpa/createTable.sql")
    @Sql( // 2
            scripts        = "/org/example/jpa/dropTable.sql",
            executionPhase = AFTER_TEST_METHOD
    )
    // 若注释掉 1 和 2 处，即使测试不通过，user1 也插入了数据库
    void transactionTest() {
        User user1 = new User();
        user1.setName("user1");
        user1.setAge(23);
        userRepository.save(user1);

        User user2 = new User();
        user2.setName("user2");
        user2.setAge(666); // 超出数据库字段存储的范围，抛出异常
        userRepository.save(user2);
        assertThrows(
                PersistenceException.class,
                () -> {
                    userRepository.getEntityManager().flush();
                }
        );
    }



    /**
     * 动态条件和分页查询测试
     */
    @Test
    @Transactional
    @Sql({"/org/example/jpa/createTable.sql", "/org/example/jpa/data.sql"})
    @Sql(
            scripts        = "/org/example/jpa/dropTable.sql",
            executionPhase = AFTER_TEST_METHOD
    )
    void dynamicQueryAndPaginationTest() {

        DetachedCriteria  dc  = userRepository.getDetachedCriteria();
        LogicalExpression and = Restrictions.and(
                                        Restrictions.eq(User.NAME, "user10"),
                                        Restrictions.or(
                                                Restrictions.eq(User.AGE, 23),
                                                Restrictions.eq(User.AGE, 28)
                                        )
                                );
        dc.add(and);

        Sort sort = Sort.by(User.ID);
        PageRequest pageRequest = PageRequest.of(0, 6, sort);
        Page<User> userPage = userRepository.findAll(dc, pageRequest);
        List<User> content  = userPage.getContent();
        if (content.isEmpty()) {
            fail();
        } else {
            long userId = content.get(0).getId();
            assertEquals(10, userId);
        }
    }



    /**
     * 测试：
     * 1. 在Repository中自定义方法
     * 2. 关联查询
     * 3. hibernate native query
     */
    @Test
    @Transactional
    @Rollback(true)
    @Sql({"/org/example/jpa/createTable.sql", "/org/example/jpa/data.sql"})
    @Sql(
            scripts        = "/org/example/jpa/dropTable.sql",
            executionPhase = AFTER_TEST_METHOD
    )
    void customMethodAndNativeQueryTest() {

        Collection<User> userCollection = userRepository.customMethodAndNativeQuery();
        EntityManager    entityManager  = userRepository.getEntityManager();
        for (User user : userCollection) {
            /* if (entityManager.contains(user)) {
                System.err.println("==========1");
            } else {
                System.err.println("==========2");
            } */
            user.setName("xxx");
        }
        entityManager.flush();
    }



    @Test
    @Transactional
    @Rollback(true)
    @Sql({"/org/example/jpa/createTable.sql", "/org/example/jpa/data.sql"})
    @Sql(
            scripts        = "/org/example/jpa/dropTable.sql",
            executionPhase = AFTER_TEST_METHOD
    )
    void sb27AliasToEntityResultTransformerTest() {
        List<?> rows = userRepository.sb27AliasToEntityResultTransformer();
        userRepository.getEntityManager().flush();
        Object[] entities = (Object[]) rows.get(1);
        Role role = (Role) entities[1];
        assertEquals("role21", role.getName());
    }



    @Test
    @Transactional
    @Rollback(true)
    @Sql({"/org/example/jpa/createTable.sql", "/org/example/jpa/data.sql"})
    @Sql(
            scripts        = "/org/example/jpa/dropTable.sql",
            executionPhase = AFTER_TEST_METHOD
    )
    void dynamicUpdate() {

        CriteriaBuilder cb = userRepository.getCriteriaBuilder();
        CriteriaUpdate<User> criteriaUpdate = cb.createCriteriaUpdate(User.class);
        Root<User> userRoot = criteriaUpdate.from(User.class);
        criteriaUpdate.set(User.AGE, 100)
                      .where(
                                 cb.equal(userRoot.get(User.NAME), "user10")
                      );

        EntityManager entityManager = userRepository.getEntityManager();
        int i = entityManager.createQuery(criteriaUpdate).executeUpdate();
        entityManager.flush();
        int rows = JdbcTestUtils.countRowsInTableWhere(jdbcTemplate, "t_user", "age = 100");
        assertEquals(1, rows);
    }



    @Test
    @Transactional
    @Rollback(true)
    @Sql({"/org/example/jpa/createTable.sql"})
    @Sql(
            scripts        = "/org/example/jpa/dropTable.sql",
            executionPhase = AFTER_TEST_METHOD
    )
    void batchSaveTest() {

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            User user = new User();
            user.setName("user" + i);
            user.setAge(i);
            users.add(user);
        }
        userRepository.batchSave(users);
        userRepository.getEntityManager().flush();
        int rows = JdbcTestUtils.countRowsInTable(jdbcTemplate, "t_user");
        assertEquals(4, rows);
    }
}
