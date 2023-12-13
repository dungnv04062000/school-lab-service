package com.schoollab.common.error;

public class UnKnownException extends RuntimeException{
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs an <code>NotFoundException</code> with the
     * specified detail message.
     *
     * @param   s   the detail message.
     */
    public UnKnownException(String s) {
        super(s);
    }
}
