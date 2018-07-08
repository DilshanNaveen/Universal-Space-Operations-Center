package com.ksatstuttgart.usoc.gui;

import com.ksatstuttgart.usoc.controller.MainController;
import com.ksatstuttgart.usoc.gui.panel.LogPanel;
import com.ksatstuttgart.usoc.gui.panel.StatePanel;
import com.ksatstuttgart.usoc.gui.panel.USOCPanel;
import com.ksatstuttgart.usoc.gui.setup.USOCTabPane;
import com.ksatstuttgart.usoc.gui.setup.configuration.Layout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import sun.applet.Main;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * Main Application Window
 * Former Scene from GUIBuilder
 */
public class MainWindow extends BorderPane {

    private static final String FILE_MENU_TITLE = "File";
    private static final String VIEW_MENU_TITLE = "View";
    private static final String LAYOUT_MENU_TITLE = "Layout";

    private static final double FIRST_DIVIDER_POSITION = 0;
    private static final double SECOND_DIVIDER_POSITION = 0.75;

    private SplitPane mainWindowSplitPane;

    private StatePanel statePanel;
    private USOCPanel usocPanel;
    private LogPanel logPanel;

    public MainWindow() {
        // Loads the configuration file
        Layout properties = MainController.getInstance().getLayout();

        setProperties(properties);
        createWindow(properties);
    }

    /**
     * Sets Window Properties
     */
    private void setProperties(Layout properties) {
        // Gets main stage
        Stage mainStage = MainController.getInstance().getStage();

        // Sets experiment title as Stage title
        mainStage.setTitle(properties.getExperimentName());

        // Handles window size
        double width;
        double height;
        if (properties.isMaximized()) {
            Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
            width = screenSize.getWidth();
            height = screenSize.getHeight();
        } else {
            width = properties.getWidth();
            height = properties.getHeight();
        }
        mainStage.setWidth(width);
        mainStage.setHeight(height);

        // Handles window resizable
        mainStage.setResizable(properties.isResizable());
    }

    /**
     * Creates the main application scene
     *
     * @return Scene
     */
    private void createWindow(Layout properties) {
        // Create the MenuBar
        setTop(prepareMenuBar());

        mainWindowSplitPane = new SplitPane();

        statePanel = new StatePanel();
        usocPanel = new USOCPanel();
        logPanel = new LogPanel();

        addPanelsToSplitPane();

        setCenter(mainWindowSplitPane);
    }

    private void addPanelsToSplitPane() {
        boolean isStatePanelEnabled = MainController.getInstance()
                .getLayout().getStatePaneProperties().isEnabled();

        boolean isUSOCPanelEnabled = MainController.getInstance()
                .getLayout().getUsocPaneProperties().isEnabled();

        boolean isLogPanelEnabled = MainController.getInstance()
                .getLayout().getLogPaneProperties().isEnabled();

        mainWindowSplitPane.getItems().clear();

        if (isStatePanelEnabled) {
            mainWindowSplitPane.getItems().add(statePanel);
        }

        if (isUSOCPanelEnabled) {
            mainWindowSplitPane.getItems().add(usocPanel);
        }

        if (isLogPanelEnabled) {
            mainWindowSplitPane.getItems().add(logPanel);
        }

        if (mainWindowSplitPane.getItems().size() == 2) {
            mainWindowSplitPane.setDividerPositions(0.5);
        } else if (mainWindowSplitPane.getItems().size() == 3) {
            mainWindowSplitPane.setDividerPositions(FIRST_DIVIDER_POSITION,
                    SECOND_DIVIDER_POSITION);
        }
    }

    /**
     * Creates the menu bar for the main stage
     *
     * @return menu bar
     */
    private MenuBar prepareMenuBar() {
        // Main MenuBar
        MenuBar menuBar = new MenuBar();

        // File Menu
        Menu fileMenu = new Menu(FILE_MENU_TITLE);

        // Load Protocol Menu Item
        Menu loadProtocolSubMenu = new Menu("Protocol");
        // Get List of Protocols in /protocols
        List<String> protocols = getAvailableProtocols();

        // Below condition should never happen
        // By default, defaultProtocol is loaded
        if (protocols.isEmpty()) {
            loadProtocolSubMenu.setDisable(true);
        } else {
            // Toggle Group ensures only one radio button is selected at a time
            ToggleGroup group = new ToggleGroup();
            for (final String protocol : protocols) {
                RadioMenuItem radioMenuItem = new RadioMenuItem(protocol);
                radioMenuItem.setOnAction(actionEvent -> {
                    MainController.getInstance()
                            .loadProtocol("protocols/" + protocol);
                    Alert a = new Alert(Alert.AlertType.INFORMATION,
                            protocol, ButtonType.OK);
                    a.setTitle("Success");
                    a.setHeaderText("Protocol Loaded");
                    a.showAndWait();
                });

                if (protocol.equals(MainController.getInstance().getLayout().getProtocolName())) {
                    radioMenuItem.setSelected(true);
                }
                group.getToggles().add(radioMenuItem);
                loadProtocolSubMenu.getItems().add(radioMenuItem);
            }
        }

        // Quit Menu Item
        MenuItem quitMenuItem = new MenuItem("Quit");
        quitMenuItem.setOnAction(actionEvent ->
                MainController.getInstance().getStage().close());

        fileMenu.getItems().addAll(loadProtocolSubMenu, new SeparatorMenuItem(), quitMenuItem);

        // View Menu
        Menu viewMenu = new Menu(VIEW_MENU_TITLE);
        CheckMenuItem statePanelItem = new CheckMenuItem("State Panel");
        statePanelItem.setOnAction(actionEvent -> {
            MainController.getInstance().getLayout()
                    .getStatePaneProperties().setEnabled(statePanelItem.isSelected());
            addPanelsToSplitPane();
        });

        CheckMenuItem usocPanelItem = new CheckMenuItem("USOC Panel");
        usocPanelItem.setOnAction(actionEvent -> {
            MainController.getInstance().getLayout()
                    .getUsocPaneProperties().setEnabled(usocPanelItem.isSelected());
            addPanelsToSplitPane();
        });
        CheckMenuItem logPanelItem = new CheckMenuItem("Log Panel");
        logPanelItem.setOnAction(actionEvent -> {
            MainController.getInstance().getLayout()
                    .getLogPaneProperties().setEnabled(logPanelItem.isSelected());
            addPanelsToSplitPane();
        });

        boolean isStatePanelEnabled = MainController.getInstance()
                .getLayout().getStatePaneProperties().isEnabled();

        boolean isUSOCPanelEnabled = MainController.getInstance()
                .getLayout().getUsocPaneProperties().isEnabled();

        boolean isLogPanelEnabled = MainController.getInstance()
                .getLayout().getLogPaneProperties().isEnabled();

        statePanelItem.setSelected(isStatePanelEnabled);
        usocPanelItem.setSelected(isUSOCPanelEnabled);
        logPanelItem.setSelected(isLogPanelEnabled);

        viewMenu.getItems().addAll(statePanelItem, usocPanelItem, logPanelItem);

        // Layout Menu
        Menu layoutMenu = new Menu(LAYOUT_MENU_TITLE);

        // Adds all Menus to Menubar
        menuBar.getMenus().addAll(fileMenu, viewMenu, layoutMenu);

        return menuBar;
    }

    /**
     * Gets a list of protocol names inside the protocols/ folder
     *
     * @return list of available protocols
     */
    private List<String> getAvailableProtocols() {
        List<String> availableProtocols = new LinkedList<>();

        File[] allProtocols = new File("protocols/").listFiles();

        for (File f : allProtocols) {
            availableProtocols.add(f.getName());
        }

        return availableProtocols;
    }

}
