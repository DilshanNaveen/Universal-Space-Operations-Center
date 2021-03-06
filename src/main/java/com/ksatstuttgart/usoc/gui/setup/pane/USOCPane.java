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

package com.ksatstuttgart.usoc.gui.setup.pane;

import com.ksatstuttgart.usoc.gui.setup.configuration.Layout;
import com.ksatstuttgart.usoc.gui.setup.configuration.Parsable;
import com.ksatstuttgart.usoc.gui.setup.configuration.USOCPaneProperties;
import com.ksatstuttgart.usoc.gui.setup.configuration.entity.Chart;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * USOC Panel Pane (Charts + GNSS View)
 *
 * @author Pedro Portela (Pedro12909)
 */
public class USOCPane extends BorderPane implements Parsable {

    /**
     * Default Node Padding
     */
    private static final Insets DEFAULT_PADDING =
            new Insets(20);

    /**
     * Default Number of Chart Columns to be displayed
     */
    private static final int DEFAULT_CHART_COLS = 2;

    /**
     * Pane Enabled CheckBox
     */
    private final CheckBox enabledCheckBox =
            new CheckBox("Enabled");

    /**
     * GNSS View Enabled CheckBox
     */
    private final CheckBox gnssCheckBox =
            new CheckBox("GNSS View");

    /**
     * Chart Columns Text Field
     */
    private final TextField chartColumnsTextField =
            new TextField();

    /**
     * Chart VBox
     */
    private VBox chartBox = new VBox();

    /**
     * Holds all visible ChartRows
     */
    private List<ChartRow> chartRowList = new ArrayList<>();

    /**
     * Creates and prepares USOC Pane
     */
    public USOCPane() {
        prepareComponents();
    }

    /**
     * Prepares pane components
     */
    private void prepareComponents() {
        prepareTop();
        prepareCenter();
        prepareBot();
    }

    /**
     * Prepares Top Region of the Border Pane
     */
    private void prepareTop() {
        GridPane topPane = new GridPane();
        topPane.setHgap(30);
        topPane.setVgap(10);

        enabledCheckBox.setSelected(true);
        enabledCheckBox.selectedProperty().addListener((observableValue, oldValue, newValue) -> {
            boolean enabled = newValue;

            // If the component is not enabled/selected, deactivates both checkboxes
            gnssCheckBox.setDisable(!enabled);
            chartColumnsTextField.setDisable(!enabled);
            getCenter().setDisable(!enabled);
            getBottom().setDisable(!enabled);
        });

        topPane.add(enabledCheckBox, 0, 0);
        topPane.add(gnssCheckBox, 1, 0);

        GridPane labelTextFieldPane = new GridPane();
        labelTextFieldPane.setAlignment(Pos.CENTER_LEFT);
        labelTextFieldPane.setHgap(10);
        labelTextFieldPane.add(new Label("Chart Columns"), 0, 0);
        labelTextFieldPane.add(chartColumnsTextField, 1, 0);
        chartColumnsTextField.setPrefColumnCount(3);
        topPane.add(labelTextFieldPane, 2, 0);

        topPane.setPadding(DEFAULT_PADDING);

        setTop(topPane);
    }

    /**
     * Prepares Center Region of the BorderPane
     */
    private void prepareCenter() {
        chartBox.setSpacing(5);
        chartBox.setFillWidth(true);

        ScrollPane scrollPane = new ScrollPane(chartBox);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        setCenter(scrollPane);
    }

    /**
     * Prepares Bottom Region of the BorderPane
     */
    private void prepareBot() {
        Button plusButton = new Button("Add Chart");
        plusButton.setPrefSize(150, 30);
        plusButton.setOnAction(actionEvent -> {
            ChartRow newRow = new ChartRow(chartRowList.size());

            chartBox.getChildren().add(newRow);
            chartRowList.add(newRow);
        });

        HBox buttonBox = new HBox(plusButton);
        buttonBox.setPadding(DEFAULT_PADDING);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setSpacing(20);

        setBottom(buttonBox);
    }

    /**
     * Writes properties to respective POJO Class
     *
     * @param pojoClass POJO Class to set properties
     */
    @Override
    public void writeToPOJO(Layout pojoClass) {
        USOCPaneProperties properties = pojoClass.getUsocPaneProperties();

        properties.setEnabled(enabledCheckBox.isSelected());
        properties.setGnssEnabled(gnssCheckBox.isSelected());
        try {
            properties.setChartColumns(Integer.parseInt(chartColumnsTextField.getText()));
        } catch (NumberFormatException e) {
            properties.setChartColumns(DEFAULT_CHART_COLS);
        }

        List<Chart> charts = new ArrayList<>();
        for (int i = 0; i < chartRowList.size(); i++) {
            ChartRow chartRow = chartRowList.get(i);

            Chart chart = new Chart(chartRow.getChartTitle(),
                    chartRow.getXLabel(), chartRow.getYLabel());

            charts.add(chart);
        }

        properties.setCharts(charts);
    }

    /**
     * Represents a Chart Row (Chart Title + xLabel + yLabel)
     */
    private class ChartRow extends GridPane {

        /**
         * Chart Label
         */
        private Label chartLabel;

        /**
         * Chart Title Text Field
         */
        private TextField chartTitleTextField = new TextField();

        /**
         * xLabel Text Field
         */
        private TextField xLabelTextField = new TextField();

        /**
         * yLabel Text Field
         */
        private TextField yLabelTextField = new TextField();

        /**
         * Deletes Chart Row from the grid
         */
        private Button deleteButton = new Button("Delete");

        /**
         * Creates and prepares a ChartRow with a given index
         *
         * @param index position of ChartRow in the chartRowList
         */
        protected ChartRow(int index) {
            setProperties();
            prepareComponent(index);
        }

        /**
         * Sets Component Layout
         */
        private void setProperties() {
            setPadding(new Insets(5, 20, 5, 20));
            setHgap(50);
            setVgap(10);
            setAlignment(Pos.CENTER);

            chartTitleTextField.setPrefColumnCount(10);
            xLabelTextField.setPrefColumnCount(5);
            yLabelTextField.setPrefColumnCount(5);

            chartTitleTextField.setPromptText("Title");
            xLabelTextField.setPromptText("x Label");
            yLabelTextField.setPromptText("y Label");

            deleteButton.setOnAction(actionEvent -> {
                chartRowList.remove(ChartRow.this);
                chartBox.getChildren().remove(ChartRow.this);
                ChartRow.this.updateChartNumbers();
            });
        }

        /**
         * Adds Fields to Component
         *
         * @param index index of chartRow
         */
        private void prepareComponent(int index) {
            this.chartLabel = new Label(String.format("Chart[%d]", index));
            add(chartLabel, 0, 0);

            // Contains assignDataButton and Delete Button
            HBox assignDeleteBox = new HBox(deleteButton);
            assignDeleteBox.setSpacing(5);
            add(assignDeleteBox, 1, 1);

            add(chartTitleTextField, 1, 0);
            add(xLabelTextField, 2, 0);
            add(yLabelTextField, 2, 1);
        }

        /**
         * Updates all chart numbers
         */
        private void updateChartNumbers() {
            for (int i = 0; i < chartRowList.size(); i++) {
                ChartRow row = chartRowList.get(i);
                row.updateLabel(String.format("Chart[%d]", i));
            }
        }

        /**
         * Updates Chart Label
         *
         * @param text new label contents
         */
        private void updateLabel(String text) {
            chartLabel.setText(text);
        }

        /**
         * Retrieves chart title text field value
         *
         * @return chart title
         */
        private String getChartTitle() {
            return chartTitleTextField.getText();
        }

        /**
         * Gets xLabel Text Field Value
         *
         * @return xLabel
         */
        private String getXLabel() {
            return xLabelTextField.getText();
        }

        /**
         * Gets yLabel Text Field Value
         *
         * @return yLabel
         */
        private String getYLabel() {
            return yLabelTextField.getText();
        }
    }
}

