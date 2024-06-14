package linkia.dam.whatacookrecipes.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.NoOpDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;

/**
 * Configuration class for setting up MongoDB with reactive support.
 */
@Configuration
@EnableReactiveMongoRepositories(basePackages = "linkia.dam.whatacookrecipes")
public class MongoConfig {

    @Value("${MONGO_URI_WHATACOOK_RECIPES}")
    private String mongoUri;

    public static final String DB_NAME = "cooking";

    /**
     * Creates a {@link MongoClient} bean to connect to MongoDB.
     *
     * @return the {@link MongoClient} instance.
     */
    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create(mongoUri);
    }

    /**
     * Creates a {@link ReactiveMongoDatabaseFactory} bean for reactive database operations.
     *
     * @param mongoClient the {@link MongoClient} instance.
     * @return the {@link ReactiveMongoDatabaseFactory} instance.
     */
    @Bean
    public ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleReactiveMongoDatabaseFactory(mongoClient, DB_NAME);
    }

    /**
     * Creates a {@link ReactiveMongoTemplate} bean for reactive database operations.
     *
     * @param reactiveMongoDatabaseFactory the {@link ReactiveMongoDatabaseFactory} instance.
     * @param mappingMongoConverter the {@link MappingMongoConverter} instance.
     * @return the {@link ReactiveMongoTemplate} instance.
     */
    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate(ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory, MappingMongoConverter mappingMongoConverter) {
        return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory, mappingMongoConverter);
    }

    /**
     * Creates a {@link MappingMongoConverter} bean to handle MongoDB mappings.
     *
     * @param mongoMappingContext the {@link MongoMappingContext} instance.
     * @return the {@link MappingMongoConverter} instance.
     */
    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoMappingContext mongoMappingContext) {
        MappingMongoConverter converter = new MappingMongoConverter(NoOpDbRefResolver.INSTANCE, mongoMappingContext);
        converter.setTypeMapper(new DefaultMongoTypeMapper(null));
        return converter;
    }
}
