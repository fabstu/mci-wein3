package mci.controller;

import mci.aufnehmen.InternalControllerError;

import java.io.IOException;

public class ExceptionHelper {
    public static void handle(IOException e) {
        handle(new InternalControllerError(e.toString()));
    }
    public static void handle(InternalControllerError e) {
        AlertHelper.showErrorAlert(e);
    }
}
