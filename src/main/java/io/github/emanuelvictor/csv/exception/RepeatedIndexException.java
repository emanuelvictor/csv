package io.github.emanuelvictor.csv.exception;

public class RepeatedIndexException extends RuntimeException {
    /**
     * use serialVersionUID from JDK 1.1.X for interoperability
     */
    private static final long serialVersionUID = 9176123495125254542L;

    /**
     * This field holds the exception ex if the
     * ClassNotFoundException(String s, Throwable ex) constructor was
     * used to instantiate the object
     *
     * @serial
     * @since 1.2
     */
    private Throwable ex;

    /**
     * Constructs a <code>ClassNotFoundException</code> with no detail message.
     */
    public RepeatedIndexException() {
        super((Throwable) null);  // Disallow initCause
    }

    /**
     * Constructs a <code>ClassNotFoundException</code> with the
     * specified detail message.
     *
     * @param s the detail message.
     */
    public RepeatedIndexException(String s) {
        super(s, null);  //  Disallow initCause
    }

    /**
     * Constructs a <code>ClassNotFoundException</code> with the
     * specified detail message and optional exception that was
     * raised while loading the class.
     *
     * @param s  the detail message
     * @param ex the exception that was raised while loading the class
     * @since 1.2
     */
    public RepeatedIndexException(String s, Throwable ex) {
        super(s, null);  //  Disallow initCause
        this.ex = ex;
    }

    /**
     * Returns the exception that was raised if an error occurred while
     * attempting to load the class. Otherwise, returns {@code null}.
     *
     * <p>This method predates the general-purpose exception chaining facility.
     * The {@link Throwable#getCause()} method is now the preferred means of
     * obtaining this information.
     *
     * @return the <code>Exception</code> that was raised while loading a class
     * @since 1.2
     */
    public Throwable getException() {
        return ex;
    }

    /**
     * Returns the cause of this exception (the exception that was raised
     * if an error occurred while attempting to load the class; otherwise
     * {@code null}).
     *
     * @return the cause of this exception.
     * @since 1.4
     */
    public Throwable getCause() {
        return ex;
    }
}