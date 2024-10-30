package com.archgeek.data.meta.exception;

/**
 * @author pizhihui
 * @date 2024-10-28 14:49
 */
public class MetaRuntimeException extends RuntimeException{

    public MetaRuntimeException() {
        super();
    }

    public MetaRuntimeException(String message, Object... args) {
        super(args.length > 0 ? String.format(message, args) : message);
    }

    public MetaRuntimeException(Throwable cause, String formatMsg, Object... args) {
        super(String.format(formatMsg, args), cause);
    }
}
