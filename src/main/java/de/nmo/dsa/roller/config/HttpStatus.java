package de.nmo.dsa.roller.config;

public class HttpStatus {

    //2xx - Success
    public static final int OK = 200;
    public static final int CREATED = 201;

    //3xx - Umleitungen
    public static final int NOT_MODIFIED = 304;

    //4xx - Client Fehler
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int GONE = 410;

    //5xx -Server Fehler
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int NOT_IMPLEMENTED = 501;

}
