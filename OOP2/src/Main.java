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
        InputLoader inputLoader = new InputLoader(args[0]);
        Input input = inputLoader.readData();

        Database database = Database.getInstance();
        database.processInput(input);
        database.runGame();
        Writer outputWriter = new Writer(args[1]);
        outputWriter.writeOutput();
    }
}
