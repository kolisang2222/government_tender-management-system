package dao;

/**
 * Custom exception class for Data Access Object errors.
 * Provides consistent error handling across all DAO implementations.
 * 
 * @author YourName
 * @version 1.0
 */
public class DAOException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Constructs a new DAOException with null as its detail message.
     */
    public DAOException() {
        super();
    }
    
    /**
     * Constructs a new DAOException with the specified detail message.
     * 
     * @param message The detail message
     */
    public DAOException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new DAOException with the specified detail message and cause.
     * 
     * @param message The detail message
     * @param cause The cause of the exception
     */
    public DAOException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Constructs a new DAOException with the specified cause.
     * 
     * @param cause The cause of the exception
     */
    public DAOException(Throwable cause) {
        super(cause);
    }
    
    /**
     * Returns a user-friendly error message suitable for display.
     * 
     * @return User-friendly error message
     */
    public String getUserFriendlyMessage() {
        String message = getMessage();
        
        if (message == null || message.isEmpty()) {
            return "A database error occurred. Please try again later.";
        }
        
        if (message.contains("Duplicate entry") || message.contains("unique constraint")) {
            return "This record already exists in the system.";
        }
        
        if (message.contains("foreign key constraint")) {
            return "This record cannot be deleted because it is referenced by other data.";
        }
        
        if (message.contains("connection") || message.contains("timeout")) {
            return "Unable to connect to the database. Please try again later.";
        }
        
        return "A system error occurred. Please contact ICT Support if the problem persists.";
    }
}