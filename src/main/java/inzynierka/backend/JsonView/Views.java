package inzynierka.backend.JsonView;

public interface Views {
    class UserView {} // Widok dla podstawowych danych użytkownika (bez pól specyficznych dla ról)
    class StudentView extends UserView {} // Widok dla studenta (zawiera pola specyficzne dla studenta)
    class LecturerView extends UserView {} // Widok dla wykładowcy (zawiera pola specyficzne dla wykładowcy)
}
