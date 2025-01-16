package com.openclassrooms.safetynet.exception;

import com.openclassrooms.safetynet.exception.fireStation.FireStationNotFoundException;
import com.openclassrooms.safetynet.exception.person.EmailNotFoundException;
import com.openclassrooms.safetynet.exception.person.PersonNotFoundException;
import com.openclassrooms.safetynet.model.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@RestControllerAdvice
public class ExceptionHandling implements ErrorController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String METHOD_IS_NOT_ALLOWED = "Cette méthode de requête n'est pas autorisée : %s. Veuillez envoyer une requête valide.";
    private static final String ERROR_PROCESSING_FILE = "Une erreur s'est produite lors du traitement du fichier.";
    private static final String INTERNAL_SERVER_ERROR_MSG = "Une erreur s'est produite lors du traitement de la demande.";
    private static final String RESOURCE_NOT_FOUND_MSG = "La ressource demandée n'existe pas.";
    private static final String EMAIL_NOT_FOUND_MSG = "L'addresse email demandée n'existe pas.";
    private static final String ERROR_PATH = "/error";

    private static final String PERSON_NOT_FOUND_MSG = "Aucune person n'a été trouvé";
    private static final String FIRE_STATION_NOT_FOUND_MSG = "Aucune addresse ou Station pompier n'a été trouvé";


    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException exception, WebRequest request) {
        return createHttpResponse(NOT_FOUND, RESOURCE_NOT_FOUND_MSG + exception.getHttpMethod(),request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception, WebRequest request) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod), request);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception, WebRequest request) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE, request);
    }

    /**
     * La méthode internalServerErrorException(Exception exception, WebRequest request) agit comme un filet de sécurité.
     * Si une exception est levée mais n'est pas spécifiquement gérée par une autre méthode @ExceptionHandler,
     * cette méthode s'en occupera. Elle garantit que l'application ne renvoie pas une trace de pile brute à
     * l'utilisateur et affiche à la place un message d'erreur générique.
     *
     * @param exception
     * @param request
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception, WebRequest request) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG, request);
    }

    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message, WebRequest request) {
        HttpResponse response = new HttpResponse(
                httpStatus.value(),
                httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(),
                message.toUpperCase(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, httpStatus);
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception, WebRequest request) {
        return createHttpResponse(NOT_FOUND, EMAIL_NOT_FOUND_MSG, request);
    }

    @ExceptionHandler(PersonNotFoundException.class)
    public ResponseEntity<HttpResponse> handlePersonNotFoundException(PersonNotFoundException exception, WebRequest request) {
        return createHttpResponse(BAD_REQUEST, PERSON_NOT_FOUND_MSG, request);
    }

    @ExceptionHandler(FireStationNotFoundException.class)
    public ResponseEntity<HttpResponse> handleFireStationNotFoundException(FireStationNotFoundException exception, WebRequest request) {
        return createHttpResponse(BAD_REQUEST, FIRE_STATION_NOT_FOUND_MSG, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HttpResponse> handleIllegalArgumentException(IllegalArgumentException exception, WebRequest request) {
        //return ResponseEntity.badRequest().body(ex.getMessage());
        return createHttpResponse(BAD_REQUEST, INTERNAL_SERVER_ERROR_MSG, request);
    }

    /* appel plusieur controller confli à resoudre
    @RequestMapping(ERROR_PATH)
    public ResponseEntity<HttpResponse> pageNotFound(WebRequest request) {
        return createHttpResponse(NOT_FOUND, RESOURCE_NOT_FOUND_MSG, request);
    }

     */
}
