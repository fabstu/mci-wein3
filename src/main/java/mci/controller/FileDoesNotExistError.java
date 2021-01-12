package mci.controller;

public class FileDoesNotExistError extends Exception {
    public String path;
    public Exception underlying;
    public FileDoesNotExistError(String path) {
        this.path = path;
    }
    public FileDoesNotExistError(String path, Exception underlying) {
        this.path = path;
        this.underlying = underlying;
    }

}
