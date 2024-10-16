package CareAssistant.datas;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface loginRepository extends MongoRepository<Login, String> {
    Optional<Login> findByUserId(String userId);
}
