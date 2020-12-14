import database.Database;
import fileio.Input;
import fileio.InputLoader;
import fileio.Writer;

public final class Main {
    private Main() {
    }

    /**
     * Entry point of program
     * @param args path to input and output files
     * @throws Exception in case of errors during operation
     */
    public static void main(final String[] args) throws Exception {
        // Create input loader and process input form input file
        InputLoader inputLoader = new InputLoader(args[0]);
        Input input = inputLoader.readData();

        // Create database singleton
        Database database = Database.getInstance();
        // Process the given input and populate database
        database.processInput(input);
        // Run simulation of the game
        database.runGame();
        // Create output writer and write output to given output file
        Writer outputWriter = new Writer(args[1]);
        outputWriter.writeOutput();
    }
}
