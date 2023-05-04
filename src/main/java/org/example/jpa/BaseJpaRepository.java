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
import org.hibernate.engine.spi.SessionImplementor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collection;
import java.util.List;

/**
 * 业务Repository/Dao继承本接口实现数据库交互功能
 * <p>
 * 参UserRepository、UserRepositoryTests
 * @author Hong Qiaowei
 */
@NoRepositoryBean
public interface BaseJpaRepository<E, ID> extends JpaRepositoryImplementation<E, ID> {

    JpaEntityInformation<E, ?> getEntityInformation();

    EntityManager              getEntityManager();

    /**
     * @return hibernate session, 以便利用hibernate api进行相关操作
     */
    SessionImplementor         getSession();

    /**
     * @return jpa CriteriaBuilder, 主要用于动态更新，参UserRepositoryTests.dynamicUpdate
     */
    CriteriaBuilder            getCriteriaBuilder();

    /**
     * @return hibernate DetachedCriteria, 主要用于动态条件查询，参UserRepositoryTests.dynamicQueryAndPaginationTest
     */
    DetachedCriteria           getDetachedCriteria();

    /**
     * 替换父接口的saveAll方法，性能原因
     * @param entities 支持实体自定义整型id
     */
    void batchSave(Collection<E> entities);

    /**
     * 动态条件查询，参UserRepositoryTests.dynamicQueryAndPaginationTest
     * @param dc 条件
     */
    List<E> findAll(DetachedCriteria dc);

    /**
     * 动态条件查询，参UserRepositoryTests.dynamicQueryAndPaginationTest
     * @param dc 条件
     * @param sort 排序参数
     */
    List<E> findAll(DetachedCriteria dc, Sort sort);

    /**
     * 动态条件查询，参UserRepositoryTests.dynamicQueryAndPaginationTest
     * @param dc 条件
     * @param pageable 分页参数
     */
    Page<E> findAll(DetachedCriteria dc, Pageable pageable);
}
