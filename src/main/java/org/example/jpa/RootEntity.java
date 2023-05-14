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

import org.example.util.JacksonUtils;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * 业务实体继承本类，从而拥有id等属性
 * @author Hong Qiaowei
 */
@MappedSuperclass
public class RootEntity implements Serializable {

    public static final String ID = "id";

    @Id
    @GeneratedValue  ( strategy = GenerationType.SEQUENCE,     generator = EntityLongIdGenerator.ID         )
    @GenericGenerator( name     = EntityLongIdGenerator.ID,    strategy  = EntityLongIdGenerator.CLASS_NAME )
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return JacksonUtils.writeValueAsString(this);
    }

}
