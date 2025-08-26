package ru.practicum.shareit.exception.exceptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ValidationException extends RuntimeException {
  final String parameter;
  final String reason;

  public ValidationException(String parameter, String reason) {
    this.parameter = parameter;
    this.reason = reason;
  }
}
