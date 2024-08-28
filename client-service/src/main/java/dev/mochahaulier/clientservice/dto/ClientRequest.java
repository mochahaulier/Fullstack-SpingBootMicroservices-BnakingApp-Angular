package dev.mochahaulier.clientservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ClientRequest(@NotNull(message = "First name is required.") String firstName,
        @NotNull(message = "Last name is required.") String lastName,
        @NotNull(message = "E-mail is required.") @Email(regexp = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,3}", flags = Pattern.Flag.CASE_INSENSITIVE) String email,
        @NotNull(message = "Phone is required.") String phone) {
}
