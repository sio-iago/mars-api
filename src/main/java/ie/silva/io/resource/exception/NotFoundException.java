package ie.silva.io.resource.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RestException {

	private static final long serialVersionUID = -3774672174795298080L;
}
