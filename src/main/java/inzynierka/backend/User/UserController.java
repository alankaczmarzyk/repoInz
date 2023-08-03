package inzynierka.backend.User;


import com.fasterxml.jackson.annotation.JsonView;
import inzynierka.backend.Core.Entities.Role;
import inzynierka.backend.Core.Entities.User;
import inzynierka.backend.JsonView.Views;
import inzynierka.backend.User.Models.StudentDTO;
import inzynierka.backend.User.Models.UserDTO;
import inzynierka.backend.User.Models.UserLogin;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class UserController {

    private final UserService userService;

    @GetMapping("/all")
    public ResponseEntity<?> findAll() {
        return userService.findAll();

    }


    @PostMapping()
    public ResponseEntity<?> addUser(@Valid @RequestBody UserDTO theUser) {
        return userService.addUser(theUser);

    }

    @PostMapping("/register/student")
    public ResponseEntity<?> addStudent(@Valid @RequestBody StudentDTO theStudent)  {
        return userService.addStudent(theStudent);
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLogin theUser) {
        return userService.loginUser(theUser);

    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") String theId) {
        return userService.getById(theId);

    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable("id") String theId) {
        return userService.deleteById(theId);

    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateById(@PathVariable("id") String theId, @RequestBody User userToUpdate) {
        return userService.updateById(theId, userToUpdate);

    }


    @PutMapping("ping/{id}")
    public String pingById(@NonNull @PathVariable("id") String theId, @NonNull @RequestBody User userToUpdate) {
        userService.pingById(userToUpdate, theId);
        return "User " + theId + " ping";
    }


    @PostMapping("/role/add")
    public ResponseEntity<?> addRole(@RequestBody Role role) {
        return userService.addRole(role);

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        return userService.handleValidationExceptions(ex);
    }
}
