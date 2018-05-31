package com.ksatstuttgart.usoc.gui.setup.pane;

import com.ksatstuttgart.usoc.gui.setup.configuration.Layout;
import com.ksatstuttgart.usoc.gui.setup.configuration.LogPaneProperties;
import com.ksatstuttgart.usoc.gui.setup.configuration.Parsable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.GridPane;

/**
 * Creates and prepares the LogPanel Pane
 */
public class LogPane extends GridPane implements Parsable {

    /**
     * Pane enabled CheckBox
     */
    private final CheckBox enabledCheckBox =
            new CheckBox("Enabled");

    /**
     * Iridium Panel CheckBox
     */
    private final CheckBox iridiumPanelCheckBox =
            new CheckBox("Iridium Panel");

    /**
     * Serial Panel Enabled CheckBox
     */
    private final CheckBox serialPanelCheckBox =
            new CheckBox("Serial Panel");

    /**
     * Default Node Padding
     */
    private static final Insets DEFAULT_PADDING =
            new Insets(20);

    /**
     * Creates an instance of the Log Panel
     */
    public LogPane() {
        setProperties();

        prepareComponents();
    }

    /**
     * Sets Pane Layout
     */
    private void setProperties() {
        setPadding(DEFAULT_PADDING);
        setHgap(100);
        setVgap(40);
    }

    /**
     * Prepares Pane Components
     */
    private void prepareComponents() {
        enabledCheckBox.setSelected(true);
        enabledCheckBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            boolean selected = newValue;

            // If the component is not enabled/selected, deactivates both checkboxes
            serialPanelCheckBox.setDisable(!selected);
            iridiumPanelCheckBox.setDisable(!selected);
        });

        add(enabledCheckBox, 0, 0);
        add(serialPanelCheckBox, 0, 1);
        add(iridiumPanelCheckBox, 1, 1);
    }

    @Override
    public void writeToPOJO(Layout pojoClass) {
        LogPaneProperties properties = pojoClass.getLogPaneProperties();

        properties.setEnabled(enabledCheckBox.isSelected());
        properties.setSerialPanelEnabled(serialPanelCheckBox.isSelected());
        properties.setIridiumPanelEnabled(iridiumPanelCheckBox.isSelected());
    }
}