package linkia.dam.whatacookrecipies.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import static java.util.Objects.nonNull;

@Configuration
@EnableReactiveMongoRepositories
@EnableConfigurationProperties(MongoProperties.class)
@RequiredArgsConstructor
@Slf4j
public class MongoConfiguration extends AbstractReactiveMongoConfiguration {
    private final MongoProperties mongoProperties;
    private final ConfigurableEnvironment env;

    @Override
    protected String getDatabaseName() {
        return mongoProperties.getMongoClientDatabase();
    }

    @Override
    @Bean(destroyMethod = "close")
    public MongoClient reactiveMongoClient() {
        Integer localPort = env.getProperty("local.mongo.port", Integer.class);

        if (nonNull(localPort)) {
            log.debug("Found embedded Mongo running on port: {}.", localPort);
            return MongoClients.create(String.format("mongodb://localhost:%d/%s", localPort, getDatabaseName()));
        }

        return MongoClients.create(mongoProperties.determineUri());
    }

/*
    @Profile("embedded")
    @Configuration
    @Import({EmbeddedMongoAutoConfiguration.class})
    public static class EmbeddedMongoConfiguration {
    }
 */

}
