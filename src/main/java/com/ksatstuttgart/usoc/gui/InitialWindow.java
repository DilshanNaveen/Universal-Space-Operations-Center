/*
 * MIT License
 *
 * Copyright (c) 2017 KSat e.V. and AerospaceResearch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.ksatstuttgart.usoc.gui;

import com.ksatstuttgart.usoc.controller.MainController;
import com.ksatstuttgart.usoc.gui.setup.LayoutCreator;
import com.ksatstuttgart.usoc.gui.setup.configuration.ConfigHandler;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

/**
 * Initial Popup Window that lets the user choose from
 * creating a new layout, loading a custom layout or
 * a default one
 *
 * @author Pedro Portela (Pedro12909)
 */
public class InitialWindow extends VBox {

    /**
     * Creates an instance of the Initial Window
     */
    public InitialWindow() {
        setProperties();
        createWindow();
    }

    /**
     * Set Window Properties
     */
    private void setProperties() {
        setSpacing(20);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(10));
    }

    /**
     * Creates the initial window that is presented to the user
     * when launching the application
     */
    private void createWindow() {
        Label header = new Label("Please select an option:");
        header.autosize();

        HBox buttonLayout = new HBox();

        Button newLayoutBtn = new Button("New Layout");
        Button loadLayoutBtn = new Button("Import Layout");
        Button defaultLayoutBtn = new Button("Default Layout");

        // Sets Click Listeners (Event handlers) for all the buttons
        newLayoutBtn.setOnAction(newLayoutBtnEventHandler());
        loadLayoutBtn.setOnAction(loadLayoutBtnEventHandler());
        defaultLayoutBtn.setOnAction(defaultLayoutBtnEventHandler());

        buttonLayout.setSpacing(20);
        buttonLayout.setAlignment(Pos.CENTER);
        buttonLayout.getChildren().addAll(newLayoutBtn, loadLayoutBtn, defaultLayoutBtn);

        getChildren().addAll(header, buttonLayout);

        MainController.getInstance().getStage().setTitle("Universal Space Operations Center");
    }

    /**
     * Click Listener/Event Handler for the New Layout Button
     *
     * @return EventHandler
     */
    private EventHandler newLayoutBtnEventHandler() {
        return event -> MainController.getInstance().getStage()
                .getScene().setRoot(new LayoutCreator());
    }

    /**
     * Click Listener/Event Handler for the Load Layout Button
     *
     * @return EventHandler
     */
    private EventHandler<ActionEvent> loadLayoutBtnEventHandler() {
        return actionEvent -> {
            // Show File Open Dialog
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open Layout File");
            fileChooser.setInitialDirectory(new File("layouts/"));
            fileChooser.setSelectedExtensionFilter(
                    new FileChooser.ExtensionFilter("JSON", "*.json"));
            File configFile =
                    fileChooser.showOpenDialog(MainController.getInstance().getStage());

            if (configFile == null) return;

            try {
                ConfigHandler.readConfigurationFile(configFile);
            } catch (IOException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setContentText("There was an error reading the layout file.");
                a.showAndWait();
                return;
            }

            MainController.getInstance().getStage()
                    .getScene().setRoot(new MainWindow());
        };
    }

    /**
     * Click Listener/Event Handler for the Default Layout Button
     *
     * @return EventHandler
     */
    private static EventHandler<ActionEvent> defaultLayoutBtnEventHandler() {
        return actionEvent -> {
            try {
                ConfigHandler.readConfigurationFile(new File("layouts/Default.json"));
            } catch (IOException e) {
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setTitle("Error");
                a.setContentText("The default configuration layout seems to have been deleted / corrupted");
                a.showAndWait();
                return;
            }

            MainController.getInstance().getStage()
                    .getScene().setRoot(new MainWindow());
        };
    }
}
