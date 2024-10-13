package CareAssistant.datas;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.util.List;

@Document(collection = "logins")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class Login {
    @Id
    private String userId;
    private String name;
    private String password;
    private boolean active;
}
