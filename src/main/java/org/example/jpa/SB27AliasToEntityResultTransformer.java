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

import org.example.util.ReflectionUtils;
import org.example.util.SqlUtils;
import org.hibernate.HibernateException;
import org.hibernate.property.access.internal.PropertyAccessStrategyBasicImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyChainedImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyFieldImpl;
import org.hibernate.property.access.internal.PropertyAccessStrategyMapImpl;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.transform.AliasedTupleSubsetResultTransformer;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Hong Qiaowei
 */
public class SB27AliasToEntityResultTransformer extends AliasedTupleSubsetResultTransformer {

    private boolean isInitialized = false;

    private Map<String, String> alias2EntityClassFieldMap = Collections.emptyMap();

    private final Map<String, Setter> alias2SetterMap = new HashMap<>();

    private final Map<String, Class<?>> alias2EntityClassMap = new HashMap<>();

    private int entityCnt;

    private String[] aliases;

    private List<Class<?>> entityClasses;

    private final Map<Class<?>, Integer> entityClass2InstancePosition = new HashMap<>();

    public SB27AliasToEntityResultTransformer(Class<?> entityClass) {
        new SB27AliasToEntityResultTransformer(Collections.singletonList(entityClass), null);
    }

    public SB27AliasToEntityResultTransformer(Class<?> entityClass, Map<String, String> alias2EntityClassFieldMap) {
        new SB27AliasToEntityResultTransformer(Collections.singletonList(entityClass), alias2EntityClassFieldMap);
    }

    public SB27AliasToEntityResultTransformer(List<Class<?>> entityClasses) {
        new SB27AliasToEntityResultTransformer(entityClasses, null);
    }

    public SB27AliasToEntityResultTransformer(List<Class<?>> entityClasses, Map<String, String> alias2EntityClassFieldMap) {
        this.entityClasses = entityClasses;
        entityCnt = entityClasses.size();
        for (int i = 0; i < entityClasses.size(); i++) {
            entityClass2InstancePosition.put(entityClasses.get(i), i);
        }
        if (alias2EntityClassFieldMap != null) {
            this.alias2EntityClassFieldMap = alias2EntityClassFieldMap;
        }
    }

    @Override
    public boolean isTransformedValueATupleElement(String[] aliases, int tupleLength) {
        return false;
    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        if (!isInitialized) {
            initialize(aliases);
        } else {
            check(aliases);
        }

        Object[] entities = new Object[entityCnt];
        try {
            for (int i = 0; i < entityClasses.size(); i++) {
                Class<?> aClass = entityClasses.get(i);
                Object entity = aClass.newInstance();
                entities[i] = entity;
            }

            for (int i = 0; i < aliases.length; i++) {
                String alias = aliases[i];
                if (alias != null) {
                    Class<?> aClass = alias2EntityClassMap.get(alias);
                    int position = entityClass2InstancePosition.get(aClass);
                    Object entity = entities[position];
                    Setter setter = alias2SetterMap.get(alias);
                    setter.set(entity, tuple[i], null);
                }
            }
            return entities;

        } catch (InstantiationException | IllegalAccessException e) {
            throw new HibernateException(e);
        }
    }

    private synchronized void initialize(String[] aliases) {
        if (isInitialized) {
            return;
        }

        PropertyAccessStrategyChainedImpl propertyAccessStrategy = new PropertyAccessStrategyChainedImpl(
                PropertyAccessStrategyBasicImpl.INSTANCE,
                PropertyAccessStrategyFieldImpl.INSTANCE,
                PropertyAccessStrategyMapImpl.INSTANCE
        );
        this.aliases = new String[aliases.length];

        for (int i = 0; i < aliases.length; i++) {
            String alias = aliases[i];
            if (alias != null) {
                this.aliases[i] = alias;
                String entityClassField = alias2EntityClassFieldMap.get(alias);

                if (entityClassField != null) {
                    Class<?> aClass = getEntityClass(entityClassField);
                    String field = getField(entityClassField);
                    alias2SetterMap.put(alias, propertyAccessStrategy.buildPropertyAccess(aClass, field).getSetter());
                    alias2EntityClassMap.put(alias, aClass);
                    continue;
                }

                String field = alias;
                if (alias.indexOf('_') > -1) {
                    field = SqlUtils.toCamelCase(alias, '_');
                }

                boolean notFound = true;
                for (Class<?> aClass : entityClasses) {
                    Field f = ReflectionUtils.findField(aClass, field);
                    if (f != null) {
                        alias2SetterMap.put(alias, propertyAccessStrategy.buildPropertyAccess(aClass, field).getSetter());
                        alias2EntityClassMap.put(alias, aClass);
                        notFound = false;
                        break;
                    }
                }
                if (notFound) {
                    throw new HibernateException("no entity class has " + alias + " field");
                }
            }
        }

        isInitialized = true;
    }

    private Class<?> getEntityClass(String entityClassField) {
        int i = entityClassField.indexOf('[');
        String className = entityClassField.substring(0, i);
        for (Class<?> entityClass : entityClasses) {
            if (entityClass.getName().equals(className)) {
                return entityClass;
            }
        }
        throw new HibernateException("entity class " + className + " not found, in " + entityClassField);
    }

    private String getField(String entityClassField) {
        int i = entityClassField.indexOf('[');
        return entityClassField.substring(i + 1, entityClassField.length() - 1);
    }

    private void check(String[] aliases) {
        if (!Arrays.equals(aliases, this.aliases)) {
            throw new IllegalStateException(
                    "aliases are different from what is cached; aliases=" + Arrays.asList(aliases) +
                            " cached=" + Arrays.asList(this.aliases));
        }
    }

    public List<?> transformList(List list) {
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SB27AliasToEntityResultTransformer that = (SB27AliasToEntityResultTransformer) o;

        if (entityClasses.size() == that.entityClasses.size()) {
            for (int i = 0; i < entityClasses.size(); i++) {
                if (!entityClasses.get(i).equals(that.entityClasses.get(i))) {
                    return false;
                }
            }
        } else {
            return false;
        }

        if (!Arrays.equals(aliases, that.aliases)) {
            return false;
        }

        return alias2EntityClassFieldMap.equals(that.alias2EntityClassFieldMap);
    }

    @Override
    public int hashCode() {
        int result = entityClasses.hashCode();
        result = 31 * result + (aliases == null ? 0 : Arrays.hashCode(aliases));
        result = 31 * result + alias2EntityClassFieldMap.hashCode();
        return result;
    }
}
