package ie.silva.io.resource.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class RestException extends Exception {

    private static final long serialVersionUID = -2707796861988854524L;
}
