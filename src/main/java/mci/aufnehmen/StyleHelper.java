package mci.aufnehmen;


import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class StyleHelper {
    public static final String redTextFieldStyle = "-fx-control-inner-background: #DC143C";
    public static final String whiteTextFieldStyle = "-fx-control-inner-background: #FFFFFF";
    public static final String redChoiceBoxStyle = "-fx-base: #DC143C; -fx-control-inner-background: -fx-base ;";
    public static final String whiteChoiceBoxStyle = "-fx-base: #FFFFFF; -fx-control-inner-background: -fx-base ;";

    public static void setRedStyle(TextField textField) {
        textField.setStyle(redTextFieldStyle);
    }
    public static void setWhiteStyle(TextField textField) {
        textField.setStyle(whiteTextFieldStyle);
    }
    public static void setRedStyle(TextArea textField) {
        textField.setStyle(redTextFieldStyle);
    }
    public static void setWhiteStyle(TextArea textField) {
        textField.setStyle(whiteTextFieldStyle);
    }

    public static void setRedStyle(ChoiceBox textField) {
        textField.setStyle(redChoiceBoxStyle);
    }
    public static void setWhiteStyle(ChoiceBox textField) {
        textField.setStyle(whiteChoiceBoxStyle);
    }
}
