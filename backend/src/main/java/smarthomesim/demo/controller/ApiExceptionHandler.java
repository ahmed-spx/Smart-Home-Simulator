package smarthomesim.demo.controller;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import smarthomesim.demo.domain.exception.DeviceNotFoundException;
import smarthomesim.demo.domain.exception.DomainException;
import smarthomesim.demo.domain.exception.InvalidDeviceTypeException;
import smarthomesim.demo.domain.exception.ConflictException;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(DeviceNotFoundException.class)
  public ProblemDetail handleNotFound(DeviceNotFoundException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    pd.setType(URI.create("https://smarthomesim/problems/device-not-found"));
    pd.setTitle("Device not found");
    return pd;
  }

  @ExceptionHandler(InvalidDeviceTypeException.class)
  public ProblemDetail handleInvalidType(InvalidDeviceTypeException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setType(URI.create("https://smarthomesim/problems/invalid-device-type"));
    pd.setTitle("Invalid device type");
    return pd;
  }

  @ExceptionHandler(DomainException.class)
  public ProblemDetail handleDomain(DomainException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setType(URI.create("https://smarthomesim/problems/domain-rule"));
    pd.setTitle("Domain rule violation");
    return pd;
  }

  // Catches enum-parsing failures and similar
  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArg(IllegalArgumentException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    pd.setType(URI.create("https://smarthomesim/problems/invalid-request"));
    pd.setTitle("Invalid request");
    return pd;
  }
  
  @ExceptionHandler(ConflictException.class)
  public ProblemDetail handleConflict(ConflictException ex) {
    ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    pd.setType(URI.create("https://smarthomesim/problems/conflict"));
    pd.setTitle("Conflict");
    return pd;
  }
}