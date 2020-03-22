package aws.example.s3;

public class MainClass {

    DatabaseDAO database;
    NetworkDAO network;

    //Setters and getters

    public boolean save(String fileName)
    {
        database.save(fileName);
        System.out.println("Saved in database in Main class");

        network.save(fileName);
        System.out.println("Saved in network in Main class");

        return true;
    }
}