package it.polimi.myShelfie.utilities;

/**
 * this class acts like a list of ANSI text-format escape codes. It's used to make TUI more charming.
 */
public class ANSI {
    public static final String RESET_COLOR = "\u001B[0m";
    public static final String BLACK = "\u001B[30m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String BLUE = "\u001B[34m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String WHITE = "\u001B[37m";

    public static final String ITALIC = "\033[3m";
    public static final String BOLD = "\033[1m";
    public static final String RESET_STYLE = "\033[0m";

    public static final String CLEAR = "\u001B[2J";
}
