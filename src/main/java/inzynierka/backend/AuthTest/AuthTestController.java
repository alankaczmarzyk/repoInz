package inzynierka.backend.AuthTest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
@SecurityRequirement(name = "Bearer Authentication")
public class AuthTestController {

    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }


    @GetMapping("/user")
    @PreAuthorize("hasRole('USER') or hasRole('ADVERTISER') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }


    @GetMapping("/advertiser")
    @PreAuthorize("hasRole('ADVERTISER')")
    public String moderatorAccess() {
        return "Advertiser Board.";
    }


    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }


    @GetMapping("/student")
    @PreAuthorize("hasRole('STUDENT')")
    public String studentAccess() {
        return "Student Board.";
    }

    @GetMapping("/lecturer")
    @PreAuthorize("hasRole('LECTURER')")
    public String lecturerAccess() {
        return "Lecturer Board.";
    }


}