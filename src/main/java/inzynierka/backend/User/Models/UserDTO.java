package inzynierka.backend.User.Models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class UserDTO {

    @NotBlank(message = "firstname cannot be empty")
    @Size(min = 2, max = 20, message = "field should have 2-20 characters")
    private String firstName;

    @NotBlank(message = "lastname cannot be empty")
    @Size(min = 2, max = 20, message = "field should have 2-20 characters")
    private String lastName;

    @NotBlank(message = "username cannot be empty")
    @Size(min = 5, max = 20, message = "field should have 5-20 characters")
    private String username;

    @NotBlank(message = "email cannot be empty")
    @Size(max = 50)
    @Email
    private String email;

    private Set<String> roles;

    @NotBlank(message = "password cannot be empty")
    @Size(min = 8, max = 40, message = "field should have 8-40 characters")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return this.roles;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}