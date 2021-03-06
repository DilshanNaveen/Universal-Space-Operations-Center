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
package com.ksatstuttgart.usoc.gui.controller;

import com.ksatstuttgart.usoc.controller.DataModification;
import com.ksatstuttgart.usoc.controller.MainController;
import com.ksatstuttgart.usoc.controller.MessageController;
import com.ksatstuttgart.usoc.data.ErrorEvent;
import com.ksatstuttgart.usoc.data.USOCEvent;
import com.ksatstuttgart.usoc.data.message.Var;
import com.ksatstuttgart.usoc.data.message.dataPackage.Sensor;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.util.ArrayList;

/**
 * <h1>MainPanelController</h1>
 * This class ensure the functionality of the MainPanel.
 * <p>
 * The charts are declared and records are assigned. The MainPanelController is generated automatically
 *
 * @author Victor Hertel
 * @version 1.0
 */
public class ChartController extends DataController {

    private ArrayList<LineChart<Number, Number>> lineCharts;

    public ChartController() {
        MainController.getInstance().addDataUpdateListener(new UpdateListener());

        lineCharts = new ArrayList<>();

    }

    public void addChart(LineChart<Number, Number> chart) {
        chart.getXAxis().setAutoRanging(true);
        chart.getYAxis().setAutoRanging(true);

        lineCharts.add(chart);
    }

    @Override
    public void updateData(MessageController mc, USOCEvent e) {

        //in case this is an error event, ignore it
        if (e instanceof ErrorEvent) {
            return;
        }

        for (LineChart<Number, Number> chart : lineCharts) {
            //go through the data and update the charts
            for (Sensor sensor : mc.getData().getSensors()) {
                //adjust values for the sensor specific to the current experiment
                Sensor adjusted = DataModification.adjustSensorData(sensor);

                //search for thermocouple sensors
                if (adjusted.getSensorName().equals(chart.getTitle())) {
                    //thermocouple sensors have only one variable with the current
                    //data scheme it uses the sensor name as variable name 
                    for (Var var : adjusted.getVars()) {
                        addVarToChart(var, chart);
                    }
                }
            }
        }
    }

    private void addVarToChart(Var var, LineChart<Number, Number> chart) {
        XYChart.Series series = getSeriesForChart(var, chart);
        for (Long time : var.getValues().keySet()) {
            series.getData().add(new XYChart.Data<>(time, var.getValues().get(time)));
        }
    }

    private XYChart.Series getSeriesForChart(Var var, LineChart<Number, Number> chart) {
        for (XYChart.Series<Number, Number> series : chart.getData()) {
            if (series.getName().equals(var.getDataName())) {
                return series;
            }
        }
        XYChart.Series series = new XYChart.Series<>();
        series.setName(var.getDataName());
        chart.getData().add(series);
        return series;
    }
}
