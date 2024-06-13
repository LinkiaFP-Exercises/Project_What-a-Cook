package com.whatacook.cookers.service.contracts;

import com.whatacook.cookers.model.favorites.FavoriteDto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface FavoriteDao extends ReactiveMongoRepository<FavoriteDto, String> {
}
