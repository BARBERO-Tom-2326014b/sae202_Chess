module com.example.sae202 {
    requires javafx.controls;
    requires javafx.fxml;
            
                        requires org.kordamp.bootstrapfx.core;
    requires java.desktop;

    opens com.example.sae202 to javafx.fxml;
    exports com.example.sae202;
}