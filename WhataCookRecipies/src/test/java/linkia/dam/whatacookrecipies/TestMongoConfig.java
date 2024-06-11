package linkia.dam.whatacookrecipies;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import com.mongodb.reactivestreams.client.MongoClients;

@TestConfiguration
public class TestMongoConfig {

    @Value("${MONGO_URI_WHATACOOK_RECIPIES}")
    private String mongoUri;

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(new SimpleReactiveMongoDatabaseFactory(MongoClients.create(mongoUri), "cooking"));
    }
}
