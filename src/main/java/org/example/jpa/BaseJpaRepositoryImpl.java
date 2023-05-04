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

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.internal.SessionFactoryImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.query.EscapeCharacter;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Hong Qiaowei
 */
// @Repository
@Transactional(readOnly = true)
public class BaseJpaRepositoryImpl<E, ID> extends SimpleJpaRepository<E, ID> implements BaseJpaRepository<E, ID> {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseJpaRepositoryImpl.class);

    private static final PageImpl empty_page      = new PageImpl<>(Collections.emptyList());
    private static       int      jdbc_batch_size = 100;

    private final JpaEntityInformation<E, ?> entityInformation;
    private final Class<E>                   entityClass;
    private final EntityManager              entityManager;
    private final PersistenceProvider        persistenceProvider;

    @Nullable
    private CrudMethodMetadata metadata;
    private EscapeCharacter    escapeCharacter = EscapeCharacter.DEFAULT;
    private SessionFactoryImpl sessionFactoryImpl;

    public BaseJpaRepositoryImpl(JpaEntityInformation<E, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityClass = this.entityInformation.getJavaType();
        this.entityManager = entityManager;
        this.persistenceProvider = PersistenceProvider.fromEntityManager(this.entityManager);

        EntityManagerFactory entityManagerFactory = this.entityManager.getEntityManagerFactory();
        SessionFactoryImpl sessionFactory = entityManagerFactory.unwrap(SessionFactoryImpl.class);
        Map<String, Object> properties = sessionFactory.getProperties();

        String batchSize = (String) properties.get("hibernate.jdbc.batch_size");
        if (StringUtils.isNotBlank(batchSize)) {
            jdbc_batch_size = Integer.parseInt(batchSize);
        }
    }

    public BaseJpaRepositoryImpl(Class<E> entityClass, EntityManager entityManager) {
        this(JpaEntityInformationSupport.getEntityInformation(entityClass, entityManager), entityManager);
    }

    @Override
    public void setEscapeCharacter(EscapeCharacter escapeCharacter) {
        this.escapeCharacter = escapeCharacter;
    }

    @Override
    public JpaEntityInformation<E, ?> getEntityInformation() {
        return entityInformation;
    }

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public SessionImplementor getSession() {
        return entityManager.unwrap(SessionImplementor.class);
    }

    @Override
    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }

    @Override
    public DetachedCriteria getDetachedCriteria() {
        return DetachedCriteria.forClass(entityClass);
    }

    @Transactional
    @Override
    public void batchSave(Collection<E> entities) {
        int count = 0;
        for (E entity : entities) {
            entityManager.persist(entity);
            count++;
            if (count % jdbc_batch_size == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    @Override
    public List<E> findAll(DetachedCriteria dc) {
        SessionImplementor session = getSession();
        Criteria criteria = dc.getExecutableCriteria(session);
        return criteria.list();
    }

    @Override
    public List<E> findAll(DetachedCriteria dc, Sort sort) {
        SessionImplementor session = getSession();
        Criteria criteria = dc.getExecutableCriteria(session);
        for (Sort.Order so : sort) {
            String property = so.getProperty();
            if (so.isAscending()) {
                criteria.addOrder(org.hibernate.criterion.Order.asc(property));
            } else {
                criteria.addOrder(org.hibernate.criterion.Order.desc(property));
            }
        }
        return criteria.list();
    }

    @Override
    public Page<E> findAll(DetachedCriteria dc, Pageable pageable) {
        SessionImplementor session = getSession();
        Criteria criteria = dc.getExecutableCriteria(session);
        Long total = (Long) criteria.setProjection(Projections.rowCount()).uniqueResult();

        if (total == 0) {
            return empty_page;
        }

        Sort sort = pageable.getSort();
        for (Sort.Order so : sort) {
            String property = so.getProperty();
            if (so.isAscending()) {
                criteria.addOrder(org.hibernate.criterion.Order.asc(property));
            } else {
                criteria.addOrder(org.hibernate.criterion.Order.desc(property));
            }
        }

        criteria.setProjection(null);
        int pageSize = pageable.getPageSize();
        int pageNumber = pageable.getPageNumber();
        criteria.setFirstResult(pageNumber * pageSize);
        criteria.setMaxResults(pageSize);
        List<E> content = criteria.list();
        return new PageImpl<>(content, pageable, total);
    }

}
