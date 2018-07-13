package com.icthh.xm.ms.timeline.repository.cassandra;

import static com.datastax.driver.core.querybuilder.QueryBuilder.eq;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.querybuilder.Insert;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import com.datastax.driver.core.querybuilder.Select;
import org.springframework.stereotype.Service;

public class EntityMappingRepository {

    private static final String TABLE_KEY_ID = "entity_key_to_id";
    private static final String VIEW_ID_KEY = "entity_id_to_key";

    private static final String ENTITY_ID_COL = "entity_id";
    private static final String ENTITY_KEY_COL = "entity_key";


    private Session session;

    public EntityMappingRepository(Session session) {
        this.session = session;
    }

    /**
     * Get entity id by entity key.
     *
     * @param key    the entity key
     * @param tenant tenant name
     * @return entity id
     */
    public Long getIdByKey(String key, String tenant) {
        Select select = QueryBuilder.select(ENTITY_ID_COL).from(tenant, TABLE_KEY_ID);
        select.where(eq(ENTITY_KEY_COL, key));
        ResultSet resultSet = session.execute(select);
        Row row = resultSet.one();
        return  row == null ? null : row.getLong(ENTITY_ID_COL);
    }

    /**
     * Get entity key by entity id.
     *
     * @param entityId entity id
     * @param tenant   tenant name
     * @return entity key
     */
    public String getKeyById(Long entityId, String tenant) {
        Select select = QueryBuilder.select(ENTITY_KEY_COL).from(tenant, VIEW_ID_KEY);
        select.where(eq(ENTITY_ID_COL, entityId));
        ResultSet resultSet = session.execute(select);
        Row row = resultSet.one();
        return row == null ? null : row.getString(ENTITY_KEY_COL);
    }

    /**
     * Insert into cassandra.
     *
     * @param entityId  entity id
     * @param entityKey entity key
     * @param tenant    tenant name
     * @return the result of insert
     */
    public boolean insertKeyById(Long entityId, String entityKey, String tenant) {
        Insert insert = QueryBuilder.insertInto(tenant, TABLE_KEY_ID).value(ENTITY_ID_COL, entityId)
            .value(ENTITY_KEY_COL, entityKey);
        ResultSet resultSet = session.execute(insert);
        return resultSet.wasApplied();
    }

}
