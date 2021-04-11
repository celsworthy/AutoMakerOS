/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celuk.gcodeviewer.engine;

/**
 *
 * @author Tony
 */
public class LayerDetails {
    private int layerNumber;
    private int startLine;
    private int endLine;
    private int numberOfLines;
    private int startOffset;
    private int endOffset;
    private double layerHeight;
    private double layerThickness;
    private boolean layerOpen;

    public LayerDetails(int layerNumber, int startLine, int endLine,
                        double layerHeight, double layerThickness) {
        this.layerNumber = layerNumber;
        this.startLine = startLine;
        this.endLine = endLine;
        this.numberOfLines = endLine - startLine;
        this.startOffset = 0;
        this.endOffset = 0;
        this.layerHeight = layerHeight;
        this.layerThickness = layerThickness;
        this.layerOpen = false;
    }
    
    public int getLayerNumber() {
        return layerNumber;
    }

    public void setLayerNumber(int layerNumber) {
        this.layerNumber = layerNumber;
    }

    public int getStartLine() {
        return startLine;
    }

    public void setStartLine(int startLine) {
        this.startLine = startLine;
    }

    public int getEndLine() {
        return endLine;
    }

    public void setEndLine(int endLine) {
        this.endLine = endLine;
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public int calcNumberOfLines() {
        numberOfLines = endLine - startLine;
        return numberOfLines;
    }

   public double getLayerHeight() {
        return layerHeight;
    }

    public void setLayerHeight(double layerHeight) {
        this.layerHeight = layerHeight;
    }

    public double getLayerThickness() {
        return layerThickness;
    }

    public void setLayerThickness(double layerThickness) {
        this.layerThickness = layerThickness;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public boolean getLayerOpen() {
        return layerOpen;
    }

    public void setLayerOpen(boolean layerOpen) {
        this.layerOpen = layerOpen;
    }
}
