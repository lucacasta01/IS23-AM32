package it.polimi.myShelfie.utilities;
public class Settings {
    //MODEL
    public static int SHELFROW = 6;

    public static int SHELFCOLUMN = 5;
    public static int BOARD_DIM = 9;
    public static int TILES_GROUP = 22;

    public static final int TCPPORT = 10768;
    //RMI
    public static final int RMIPORT = 6667;
    public static final String RMINAME = "RMIServer";

    public static final String SERVER_IP = "localhost";

    public static final int PINGPERIOD = 3000;
    public static final int PINGTHRESHOLD = 4000;
    public static final int PINGFACTOR = 40;
    public static final boolean pingOn = false;
}
