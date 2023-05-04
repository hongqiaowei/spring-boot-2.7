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

import org.example.SpringBoot27;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * @author Hong Qiaowei
 */
@Configuration
@EnableJpaRepositories(repositoryBaseClass = BaseJpaRepositoryImpl.class, basePackageClasses = {SpringBoot27.class})
@EntityScan(basePackageClasses = {SpringBoot27.class})
public class JpaConfig {

    /* private SessionFactoryImpl sessionFactoryImpl;

    public SessionFactoryImpl getSessionFactoryImpl() {
        if (sessionFactoryImpl == null) {
            LocalContainerEntityManagerFactoryBean factoryBean = SpringBoot2.APPLICATION_CONTEXT.getBean(LocalContainerEntityManagerFactoryBean.class);
            sessionFactoryImpl = (SessionFactoryImpl) factoryBean.getNativeEntityManagerFactory();
        }
        return sessionFactoryImpl;
    } */
}
