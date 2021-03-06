/*
 * Copyright (C) 2012-2017 DuyHai DOAN
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package info.archinnov.achilles.internals.entities;

import java.util.UUID;

import info.archinnov.achilles.annotations.*;

@Table(keyspace = "missing_schema", table = "missing_static")
public class EntityWithMissingStaticCol {

    @PartitionKey
    private Long id;

    @Static
    @Column
    private String staticCol;

    @ClusteringColumn
    private UUID clust;

    @Column
    private String value;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStaticCol() {
        return staticCol;
    }

    public void setStaticCol(String staticCol) {
        this.staticCol = staticCol;
    }

    public UUID getClust() {
        return clust;
    }

    public void setClust(UUID clust) {
        this.clust = clust;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
