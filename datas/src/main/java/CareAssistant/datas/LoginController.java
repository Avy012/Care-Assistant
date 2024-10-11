package CareAssistant.datas;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;


import java.util.Map;

@RestController
@AllArgsConstructor
public class LoginController {
    private final loginRepository loginRepository;
    private final PasswordEncoder passwordEncoder;
    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody Login user){
        try{
            if(loginRepository.findByUserId(user.getUserId()).isPresent())
                return ResponseEntity.status(HttpStatus.CONFLICT).body("UserID taken");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            Login save = loginRepository.save(user);
            return ResponseEntity.ok(HttpStatus.CREATED);
        } catch (Exception e ){
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

}
