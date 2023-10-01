package com.example.tgbot.repository;

import com.example.tgbot.model.UserModel;
import org.springframework.data.repository.CrudRepository;

public interface UserModelRepository extends CrudRepository<UserModel, Long> {
}
