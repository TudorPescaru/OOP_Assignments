import database.Database;
import fileio.Input;
import fileio.InputLoader;
import fileio.Writer;

/**
 * Entry point to the simulation
 */
public final class Main {
    private Main() {
    }

    /**
     * Main function which reads the input file and starts simulation
     * @param args input and output files
     * @throws Exception might error when reading/writing/opening files, parsing JSON
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
