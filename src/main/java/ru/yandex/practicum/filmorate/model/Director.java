package ru.yandex.practicum.filmorate.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Valid
@Builder
public class Director {
    private Long id;

    @NotBlank(message = "Director name cannot be blank")
    @Size(min = 1, max = 100, message = "Director name must be 1-100 characters")
    private String name;
}
