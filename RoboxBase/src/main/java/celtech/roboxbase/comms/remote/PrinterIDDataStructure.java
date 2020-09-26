/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package celtech.roboxbase.comms.remote;

/**
 *
 * @author Ian Hudson @ Liberty Systems Limited
 */
public class PrinterIDDataStructure
{

    // modelBytes + editionBytes + weekOfManufactureBytes + yearOfManufactureBytes + poNumberBytes + serialNumberBytes
    // checkByteBytes + electronicsVersionBytes + firstPadBytes + printerFriendlyNameBytes + secondPadBytes + colourBytes = 256.
    /**
     *
     */
    public static final int modelBytes = 5;

    /**
     *
     */
    public static final int editionBytes = 2;

    /**
     *
     */
    public static final int weekOfManufactureBytes = 2;

    /**
     *
     */
    public static final int yearOfManufactureBytes = 2;

    /**
     *
     */
    public static final int poNumberBytes = 7;

    /**
     *
     */
    public static final int serialNumberBytes = 4;

    /**
     *
     */
    public static final int checkByteBytes = 1;

    /**
     *
     */
    public static final int electronicsVersionBytes = 1;

    /**
     *
     */
    public static final int firstPadBytes = 40;

    /**
     *
     */
    public static final int printerFriendlyNameBytes = 100;

    /**
     *
     */
    public static final int secondPadBytes = 86;

    /**
     *
     */
    public static final int colourBytes = 6;
}
