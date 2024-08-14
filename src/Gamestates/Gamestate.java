package Gamestates;

/*
Enum enables for a variable to be a set of predefined constants. Enums are used to give names to constants, which makes the code easier to read and maintain, used when value wont change.
 */
public enum Gamestate {

    PLAYING, MENU, OPTIONS, QUIT;

    public static Gamestate state = MENU;
}
