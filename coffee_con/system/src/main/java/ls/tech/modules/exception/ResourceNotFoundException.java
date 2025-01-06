package ls.tech.modules.exception;

/**
 * @program: coffee_con
 * @ClassName: ResourceNotFoundException
 * @author: skl
 * @create: 2025-01-04 09:54
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
