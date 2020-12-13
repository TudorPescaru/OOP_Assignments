import database.Database;
import entities.Consumer;
import entities.Contract;
import entities.Distributor;
import fileio.Input;
import fileio.InputLoader;

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
        for (Consumer consumer : database.getConsumersMap().values()) {
            System.out.println("Consumer: " + consumer.getId() + " " + consumer.isBankrupt()
                    + " " + consumer.getBudget());
        }
        for (Distributor distributor : database.getDistributorsMap().values()) {
            System.out.println("Distributor: " + distributor.getId() + " "
                    + distributor.getBudget() + " " + distributor.isBankrupt());
            for (Contract contract : distributor.getContracts()) {
                System.out.println("Contract: " + contract.getConsumer().getId() + " "
                        + contract.getMonthlyCost() + " " + contract.getContractLength());
            }
        }
    }
}
