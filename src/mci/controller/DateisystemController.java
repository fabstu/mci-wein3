package mci.controller;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mci.aufnehmen.InternalControllerError;
import mci.wein.Wein;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DateisystemController {
    private static final String applicationName = "WeinApplikation";
    private static final String defaultFilename = "WeinListe.csv";
    File lastOpenedFile;
    Stage stage;

    public DateisystemController(Stage stage) {
    }

    public List<Wein> loadLastOpenedFile() throws DecodingError, IOException, KeineWeindateiError {
        try {
            try {
                lastOpenedFile = getLastOpenFile();
            } catch (IOException e) {
                System.out.println("Failed to get last open file.");
                e.printStackTrace();
                lastOpenedFile = null;
            }

            System.out.println("existingDataFolder() = " + existingDataFolder());

            System.out.println("Loading last opened file.");
            if(lastOpenedFile != null) {
                return openFromFile(lastOpenedFile);
            } else {
                System.out.println("Did not read last opened file since lastOpenedFile is nil.");
            }
        } catch (InternalControllerError internalControllerError) {
            internalControllerError.printStackTrace();
        }
        return new ArrayList<>();
    }

    private List<Wein> openFromFile(File lastOpenedFile) throws IOException, DecodingError, KeineWeindateiError {
        String content = loadFile(lastOpenedFile);
        return decodeFromCSV(content);
    }

    public void speichern(List<Wein> weinListe) throws ChoosingFileFailed, IOException, InternalControllerError {
        if(lastOpenedFile == null) {
            setLastOpenFile(chooseSaveFile());
        }
        speichereInDatei(weinListe, lastOpenedFile);
    }
    public void speichernUnter(List<Wein> weinListe) throws ChoosingFileFailed, IOException, InternalControllerError {
        setLastOpenFile(chooseSaveFile());
        speichereInDatei(weinListe, lastOpenedFile);
    }
    void speichereInDatei(List<Wein> weinListe, File lastOpenedFile) throws IOException {
        System.out.println("Writing to " + lastOpenedFile.toString());
        var csv = encodeToCSV(weinListe);
        Files.writeString(lastOpenedFile.toPath(), csv);
        System.out.println("Writing succeeded.");
        System.out.println(csv);
    }
    List<Wein> öffnen() throws ChoosingFileFailed, IOException, DecodingError, KeineWeindateiError, InternalControllerError {
        setLastOpenFile(chooseLoadFile());
        return openFromFile(lastOpenedFile);
    }

    private String encodeToCSV(List<Wein> weinListe) {
        return String.join("\n", weinListe.stream().map(wein -> wein.toCSV()).toArray(String[]::new));
    }
    private List<Wein> decodeFromCSV(String csv) throws DecodingError, KeineWeindateiError {
        String[] zeilen = csv.split("\n");

        InvalidFormatException failedOne = null;
        int countDamaged = 0;

        List<Wein> output = new ArrayList<>();
        for(String zeile: zeilen) {
            try {
                var eingelesenerWein = new Wein(zeile);
                if(eingelesenerWein.isValid()) {
                    output.add(eingelesenerWein);
                } else {
                    System.out.println("Skipped Wein. Fehlerhafter Eintrag.");
                    countDamaged++;
                }
            } catch (InvalidFormatException e) {
                failedOne = e;
                System.out.println("Skipped Wein. " + e.toString());
                e.printStackTrace();
                countDamaged++;
            }
        }
        if(countDamaged > 0) {
            if(output.size() == 0) {
                throw new KeineWeindateiError();
            }else {
                throw new DecodingError(output, countDamaged, output.size());
            }
        }
        return output;
    }
    private File getLastOpenFile() throws InternalControllerError, IOException {
        File file = getLastOpenedFileStoragePath();

        if(!file.exists()) {
            return null;
        }

        var content = loadFile(file);

        return new File(content);
    }
    private File getLastOpenedFileStoragePath() throws InternalControllerError {
        var fileName = "LastOpenFile.txt";
        var filePath = existingDataFolder() + "/" + fileName;
        return new File(filePath);
    }
    private void setLastOpenFile(File file) throws IOException, InternalControllerError {
        System.out.printf("Writing last opened file to %s.\n", file.toString());
        lastOpenedFile = file;
        Files.writeString(getLastOpenedFileStoragePath().toPath(), lastOpenedFile.toString());
    }
    private File chooseSaveFile() throws ChoosingFileFailed {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a save file");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Bitte geben sie die Datei an in die sie speichern möchten.", ".csv"));
        setInitialFileName(fileChooser);


        File file = fileChooser.showSaveDialog(stage);
        if(file == null) {
            throw new ChoosingFileFailed("");
        }
        // Add missing extension.
        if(!file.toPath().toString().endsWith(".csv")) {
            file = new File(file.toPath().toString() + ".csv");
        }

        return file;
    }
    private File chooseLoadFile() throws ChoosingFileFailed {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open the file.");
        fileChooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("Bitte wählen sie die Datei die sie laden möchten.", ".csv"));
        setInitialFileName(fileChooser);

        File file = fileChooser.showOpenDialog(stage);
        if(file == null) {
            throw new ChoosingFileFailed("");
        }
        var filepath = file.toPath().toString();
        if(!filepath.endsWith(".csv")) {
            System.out.println("Wrong file extension. " + filepath);
            throw new WrongFileExtension(filepath);
        }
        return file;
    }

    private void setInitialFileName(FileChooser fileChooser) {
        if(lastOpenedFile != null) {
            fileChooser.setInitialDirectory(lastOpenedFile.getParentFile());
            fileChooser.setInitialFileName(lastOpenedFile.getName());
        } else {
            fileChooser.setInitialFileName(defaultFilename);
            fileChooser.setInitialDirectory(existingDocumentsDataFolder());
        }
    }

    private String loadFile(File file) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();

        String str = new String(data, "UTF-8");
        return str;
    }

    private String existingDataFolder() throws InternalControllerError {
        var dataFolder = appDataFolder() + "/" + applicationName;

        var dataFolderFile = new File(dataFolder);
        if(!dataFolderFile.exists()) {
            var success = (dataFolderFile).mkdirs();
            if (!success) {
                throw new InternalControllerError("Failed to create application folder.");
            }
        }

        return dataFolder;
    }
    private String appDataFolder() {
        var appDataPath = System.getenv("APPDATA");
        if(appDataPath != null) {
            return appDataPath;
        }

        return System.getProperty("user.home") + "/Library/Application Support";
    }
    private File existingDocumentsDataFolder() {
        var home = System.getenv("user.home");
        if(home == null) {
            home = "/Users/fabiansturm";
        }

        var file = new File(home + "/Documents/WeinApplication");
        if(!file.exists()) {
            file.mkdirs();
        }
        System.out.println("Existing documents data folder = " + file.toString());
        return file;
    }

}
