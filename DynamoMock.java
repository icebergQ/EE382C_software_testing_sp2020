package awstest.awstest;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Test;

import com.amazonaws.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.DeleteItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.LimitExceededException;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;


public class App 
{
	public void createDB() {
		
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
	            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
	            .build();

        DynamoDB dynamoDB = new DynamoDB(client);

        String tableName = "Movies";

        try {
            System.out.println("Attempting to create table; please wait...");
            Table table = dynamoDB.createTable(tableName,
                Arrays.asList(new KeySchemaElement("year", KeyType.HASH), // Partition
                                                                          // key
                    new KeySchemaElement("title", KeyType.RANGE)), // Sort key
                Arrays.asList(new AttributeDefinition("year", ScalarAttributeType.N),
                    new AttributeDefinition("title", ScalarAttributeType.S)),
                new ProvisionedThroughput(10L, 10L));
            table.waitForActive();
            System.out.println("Success.  Table status: " + table.getDescription().getTableStatus());

        }
        catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }

    }
	
	public void CreateTable() throws JsonParseException, IOException {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
	            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
	            .build();

	        DynamoDB dynamoDB = new DynamoDB(client);

	        Table table = dynamoDB.getTable("Movies");

	        JsonParser parser = new JsonFactory().createParser(new File("moviedata.json"));

	        JsonNode rootNode = new ObjectMapper().readTree(parser);
	        Iterator<JsonNode> iter = rootNode.iterator();

	        ObjectNode currentNode;

	        while (iter.hasNext()) {
	            currentNode = (ObjectNode) iter.next();

	            int year = currentNode.path("year").asInt();
	            String title = currentNode.path("title").asText();

	            try {
	                table.putItem(new Item().withPrimaryKey("year", year, "title", title).withJSON("info",
	                    currentNode.path("info").toString()));
	                System.out.println("PutItem succeeded: " + year + " " + title);

	            }
	            catch (Exception e) {
	                System.err.println("Unable to add movie: " + year + " " + title);
	                System.err.println(e.getMessage());
	                break;
	            }
	        }
	        parser.close();
	 }
		
	public void CreateTableItem() {
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
	            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
	            .build();

	        DynamoDB dynamoDB = new DynamoDB(client);

	        Table table = dynamoDB.getTable("Movies");

	        int year = 2015;
	        String title = "The Big New Movie";

	        final Map<String, Object> infoMap = new HashMap<String, Object>();
	        infoMap.put("plot", "Nothing happens at all.");
	        infoMap.put("rating", 0);

	        try {
	            System.out.println("Adding a new item...");
	            PutItemOutcome outcome = table
	                .putItem(new Item().withPrimaryKey("year", year, "title", title).withMap("info", infoMap));

	            System.out.println("PutItem succeeded:\n" + outcome.getPutItemResult());

	        }
	        catch (Exception e) {
	            System.err.println("Unable to add item: " + year + " " + title);
	            System.err.println(e.getMessage());
	        }
		
	}
	
	public void UpdateTableItem() {
		
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
	            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
	            .build();

	        DynamoDB dynamoDB = new DynamoDB(client);

	        Table table = dynamoDB.getTable("Movies");

	        int year = 2015;
	        String title = "The Big New Movie";

	        UpdateItemSpec updateItemSpec = new UpdateItemSpec().withPrimaryKey("year", year, "title", title)
	            .withUpdateExpression("set info.rating = :r, info.plot=:p, info.actors=:a")
	            .withValueMap(new ValueMap().withNumber(":r", 5.5).withString(":p", "Everything happens all at once.")
	                .withList(":a", Arrays.asList("Larry", "Moe", "Curly")))
	            .withReturnValues(ReturnValue.UPDATED_NEW);

	        try {
	            System.out.println("Updating the item...");
	            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);
	            System.out.println("UpdateItem succeeded:\n" + outcome.getItem().toJSONPretty());

	        }
	        catch (Exception e) {
	            System.err.println("Unable to update item: " + year + " " + title);
	            System.err.println(e.getMessage());
	        }
	}
	
	
	public void DeleteTableItem() {
		
		AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
	            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:8000", "us-west-2"))
	            .build();

	        DynamoDB dynamoDB = new DynamoDB(client);

	        Table table = dynamoDB.getTable("Movies");

	        int year = 2015;
	        String title = "The Big New Movie";

	        DeleteItemSpec deleteItemSpec = new DeleteItemSpec()
	            .withPrimaryKey(new PrimaryKey("year", year, "title", title)).withConditionExpression("info.rating <= :val")
	            .withValueMap(new ValueMap().withNumber(":val", 5.0));

	        // Conditional delete (we expect this to fail)

	        try {
	            System.out.println("Attempting a conditional delete...");
	            table.deleteItem(deleteItemSpec);
	            System.out.println("DeleteItem succeeded");
	        }
	        catch (Exception e) {
	            System.err.println("Unable to delete item: " + year + " " + title);
	            System.err.println(e.getMessage());
	        }
		
	}
	
    @Test(expected = LimitExceededException.class)
    public void LimitExceededExceptionTest() {
        App dynamoTester = new App();

        new Thread(new Runnable() {
            public void run() {
            	dynamoTester.deleteBucket();
                try {
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
        
        verify(s3, times(1)).listObjects(existentBucketName);
        assertTrue(true);
    }
	
	
	public static void main(String[] args) {
		System.out.print("hello");
		
	}
	
	
}
	
	
	
        

