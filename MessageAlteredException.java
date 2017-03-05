/*
 * MessageAlteredException.java
 *
 * Nathan Geist
 * 10152796
 * October 4th 2016
 *
 */
public class MessageAlteredException extends Exception {
    public MessageAlteredException() { super(); }
    public MessageAlteredException(String message) { super(message); }
    public MessageAlteredException(String message, Throwable cause) { super(message, cause); }
    public MessageAlteredException(Throwable cause) { super(cause); }
}

