/*
 * The MIT License
 *
 * Copyright 2017 KSat Stuttgart e.V..
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ksatstuttgart.usoc;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;

/**
 * This class builds the GUI FXML structure based on input 
 * parameters in the config.properties file and rebuilds parts
 * of it depending on changes.
 * 
 *
 * @author  Victor Hertel
 * @version 1.0
*/

public class GuiBuilder {
        
    
    
    
    public static void setExperimentName() {
        System.out.println("setExperimentName activeted!");
    }
    
    
    
    
    /**
     * Method stores values of config.properties file and the date
     * of it's last modification into configMod.properties file.
     * 
     * @throws java.io.IOException
    */  
    public static void chartBuilder() throws IOException {
 
        // Generates GridPane for charts        
        try {
            // Stores values of config.properties file into properties object 'config'
            Properties config = GetConfigData.getAllValues();
            int numberOfCharts = Integer.parseInt(config.getProperty("numberOfCharts"));
            int numberOfRows;
            
            // Sets number of rows depending on required number of charts
            if (numberOfCharts%2 == 0) {
                numberOfRows = numberOfCharts/2;
            } else {
                numberOfRows = (numberOfCharts + 1)/2;
            }
            
            // Initialize file object
            String fileName = "Charts.fxml";
            String filePath = "src/main/resources/fxml/";
            
            // Writes data in Charts.fxml file
            PrintWriter writer = new PrintWriter(filePath + fileName);
            writer.println("<?import javafx.scene.layout.*?> \n");
            writer.println("<GridPane> \n" +
                    "  <columnConstraints> \n" +
                    "    <ColumnConstraints hgrow=\"SOMETIMES\" minWidth=\"10.0\" prefWidth=\"100.0\" /> \n" +
                    "    <ColumnConstraints hgrow=\"SOMETIMES\" minWidth=\"10.0\" prefWidth=\"100.0\" /> \n" +
                    "  </columnConstraints> \n" +
                    "  <rowConstraints>");
            for (int i=1; i<=(numberOfRows); i++) {
                writer.println("    <RowConstraints minHeight=\"10.0\" prefHeight=\"30.0\" vgrow=\"SOMETIMES\" />");
            }
            writer.println("  </rowConstraints> \n" +
                    "</GridPane>");
            writer.close();
            
            
            
            
            
            
            // Generates GridPane for charts        

            
            System.out.println("Number of charts has been updated!");

        } catch (IOException e) {
            System.out.println("Exception: " + e);
        }         
    }
    
    
    

    public static void logBuilder() {
        System.out.println("logBuilder activeted!");
    }
}
