package inzynierka.backend.User;

import inzynierka.backend.Core.Entities.*;
import inzynierka.backend.Jwt.JwtUtils;
import inzynierka.backend.Jwt.Models.JwtResponse;
import inzynierka.backend.RefreshToken.RefreshTokenService;
import inzynierka.backend.Role.ERole;
import inzynierka.backend.User.Models.StudentDTO;
import inzynierka.backend.User.Models.StudentStatus;
import inzynierka.backend.User.Models.UserDTO;
import inzynierka.backend.User.Models.UserLogin;
import inzynierka.backend.UserDetails.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static inzynierka.backend.Role.ERole.ROLE_STUDENT;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder encoder;

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;


    public ResponseEntity<?> findAll() {
        List<User> users = userRepository.findAll();

        if (users.size() > 0) {
            return new ResponseEntity<>(users, HttpStatus.OK);
        }

        return new ResponseEntity<>("No users available", HttpStatus.NOT_FOUND);
    }


    public void save(User theUser) {
        userRepository.save(theUser);
    }


    public ResponseEntity<?> addUser(@Valid UserDTO theUser) {
        if (userRepository.existsByUsername(theUser.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(theUser.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        User user = new User(theUser.getFirstName(), theUser.getLastName(), theUser.getUsername(),
                encoder.encode(theUser.getPassword()), theUser.getEmail());

        Set<String> strRoles = theUser.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            AtomicInteger roleCount = new AtomicInteger(0);
            strRoles.forEach(role -> {
                switch (role) {
                    case "adm":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);

                        break;
                    case "adv":
                        Role modRole = roleRepository.findByName(ERole.ROLE_ADVERTISER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);

                        break;
                    case "stu":
                        Role studentRole = roleRepository.findByName(ROLE_STUDENT)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(studentRole);

                        break;

                    case "lec":
                        Role lecRole = roleRepository.findByName(ERole.ROLE_LECTURER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(lecRole);

                        break;
                    default:
                        if (roleCount.compareAndSet(0, 1)) {
                            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                            roles.add(userRole);
                        }
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    public ResponseEntity<?> addStudent(@Valid StudentDTO theStudent) {

        if (userRepository.existsByEmail(theStudent.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        String studentUsername = theStudent.getFirstName().toLowerCase() + "." + theStudent.getLastName().toLowerCase();

        User user = new User(theStudent.getFirstName(), theStudent.getLastName(), studentUsername,
                encoder.encode(theStudent.getPassword()), theStudent.getEmail());

        Set<Role> role = new HashSet<>();
        Set<String> strRoles = theStudent.getRoles();
        Role studentRole = roleRepository.findByName(ROLE_STUDENT).orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        if ((strRoles == null) || (strRoles.contains("stu") && strRoles.size()==1)){
            role.add(studentRole);
        }else{
            return ResponseEntity.badRequest().body("Error: Unsuitable role for a student!");
        }

        user.setRoles(role);
        user.setStatus(StudentStatus.PENDING);
        userRepository.save(user);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLogin theUser) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(theUser.getEmail(), theUser.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails.getEmail(), userDetails.getAuthorities());

        RefreshToken tkn;
        if (refreshTokenService.findTokenByUserId(userDetails.getId()).isPresent()) {
            RefreshToken refreshTokenToCheck = refreshTokenService.findTokenByUserId(userDetails.getId()).get();

            if (refreshTokenToCheck.getExpiryDate().compareTo(Instant.now()) < 0) {
                refreshTokenService.deleteByUserId(userDetails.getId());
                tkn = refreshTokenService.createRefreshToken(userDetails.getId());
            } else {
                tkn = refreshTokenToCheck;
            }
        } else {
            tkn = refreshTokenService.createRefreshToken(userDetails.getId());
        }

        return ResponseEntity.ok(new JwtResponse(jwt, tkn.getToken()));
    }


    public ResponseEntity<?> getById(String theId) {
        Optional<User> user = userRepository.findById(theId);

        if (user.isEmpty()) {
            return new ResponseEntity<>("User does not exist", HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(user, HttpStatus.OK);
    }


    public ResponseEntity<?> deleteById(String theId) {
        Optional<User> user = userRepository.findById(theId);

        if (user.isEmpty()) {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }

        userRepository.deleteById(theId);
        return new ResponseEntity<>("User deleted", HttpStatus.OK);
    }


    public ResponseEntity<?> updateById(String theId, User user) {
        Optional<User> userExist = userRepository.findById(theId);

        if (userExist.isPresent()) {
            User userToSave = userExist.get();

            userToSave.setFirstName(Optional.ofNullable(user.getFirstName()).orElse(userToSave.getFirstName()));
            userToSave.setLastName(Optional.ofNullable(user.getLastName()).orElse(userToSave.getLastName()));
            userToSave.setUsername(Optional.ofNullable(user.getUsername()).orElse(userToSave.getUsername()));
            if (user.getPassword() != null) {
                userToSave.setPassword(encoder.encode(user.getPassword()));
            }
            userToSave.setEmail(Optional.ofNullable(user.getEmail()).orElse(userToSave.getEmail()));
            userRepository.save(userToSave);
            return new ResponseEntity<>("User updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<?> pingById(User user, String theId) {
        Optional<User> userExist = userRepository.findById(theId);

        if (userExist.isPresent()) {
            User userToSave = userExist.get();
            userToSave.setAppVersion(Optional.ofNullable(user.getAppVersion()).orElse(userToSave.getAppVersion()));
            userToSave.setLastPing(Optional.ofNullable(user.getLastPing()).orElse(userToSave.getLastPing()));
            userRepository.save(userToSave);
            return new ResponseEntity<>("User updated", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
        }
    }


    public ResponseEntity<?> addRole(Role role) {
        roleRepository.save(role);
        return ResponseEntity.ok("Role added");
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;

    }

}
