package org.kaaproject.data_migration;


import com.redkite.gen.model.tables.pojos.Configuration;
import com.redkite.gen.model.tables.records.ConfigurationRecord;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.util.Base64;
import java.util.List;

import static com.redkite.gen.model.Tables.CONFIGURATION;

/**
 * Created by user482400 on 03.08.16.
 */
public final class ConfigurationUpgrade {
    private static final String UUID_FIELD = "__uuid";
    private static final String UUID_VALUE = "org.kaaproject.configuration.uuidT";

    private ConfigurationUpgrade() {}

    public static void run() throws Exception {
        updateUuids();
        //todo: transformation into CTL
    }

    private static void updateUuids() throws Exception {
        DSLContext create = DSL.using(DataSources.MARIADB.getDs(), SQLDialect.MARIADB);
        List<Configuration> configs = create.select().from(CONFIGURATION).fetchInto(Configuration.class);

        for (Configuration config : configs) {
            JsonNode json = new ObjectMapper().readTree(config.getConfigurationBody());
            JsonNode jsonEncoded = encodeUuids(json);
            byte[] encodedConfigurationBody = jsonEncoded.toString().getBytes();
            config.setConfigurationBody(encodedConfigurationBody);

            ConfigurationRecord record = create.newRecord(CONFIGURATION, config);
            create.executeUpdate(record);
        }
    }

    private static JsonNode encodeUuids(JsonNode json) throws Exception {
        if (json.has(UUID_FIELD)) {
            JsonNode j = json.get(UUID_FIELD);
            if (j.has(UUID_VALUE)) {
                String value = j.get(UUID_VALUE).asText();
                String encodedValue = Base64.getEncoder().encodeToString(value.getBytes("ISO-8859-1"));
                ((ObjectNode)j).put(UUID_VALUE, encodedValue);
            }
        }

        for (JsonNode node : json) {
            if (node.isContainerNode()) encodeUuids(node);
        }

        return json;
    }

    private static JsonNode decodeUuids(JsonNode json) throws Exception {
        if (json.has(UUID_FIELD)) {
            JsonNode j = json.get(UUID_FIELD);
            if (j.has(UUID_VALUE)) {
                String value = j.get(UUID_VALUE).asText();
                String encodedValue = new String (Base64.getDecoder().decode(value.getBytes("ISO-8859-1")), "ISO-8859-1");
                ((ObjectNode)j).put(UUID_VALUE, encodedValue);
            }
        }

        for (JsonNode node : json) {
            if (node.isContainerNode()) decodeUuids(node);
        }

        return json;
    }
}
