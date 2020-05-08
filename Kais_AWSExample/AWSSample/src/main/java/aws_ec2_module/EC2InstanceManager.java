package aws_ec2_module;


import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.regions.Region;

import java.util.LinkedList;
import java.util.List;

//Reference: https://github.com/awsdocs/aws-doc-sdk-examples/tree/master/javav2/example_code/ec2/src/main/java/com/example/ec2
public class EC2InstanceManager {


    public static String createEC2Instance(Ec2Client ec2, String name, String amiId ) {

        if(name.equals("")) return "";
        if(amiId.equals("")) return "";

        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .imageId(amiId)
                .instanceType(InstanceType.T1_MICRO)
                .maxCount(1)
                .minCount(1)
                .build();

        RunInstancesResponse response = ec2.runInstances(runRequest);
        String instanceId = response.instances().get(0).instanceId();

        Tag tag = Tag.builder()
                .key("Name")
                .value(name)
                .build();

        CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                .resources(instanceId)
                .tags(tag)
                .build();


//        try {
            ec2.createTags(tagRequest);
            System.out.printf(
                    "Successfully started EC2 instance %s based on AMI %s",
                    instanceId, amiId);

            return instanceId;

//        } catch (Ec2Exception e) {
//            System.err.println(e.awsErrorDetails().errorMessage());
//            System.exit(1);
//        }
//        // snippet-end:[ec2.java2.create_instance.main]
//        return "";
    }

    public static List<String> findRunningEC2Instances(Ec2Client ec2) {

//        try {

            String nextToken = null;
            List<String> listInstances = new LinkedList<>();

            do {
                // Create a Filter object to find all running instances
                Filter filter = Filter.builder()
                        .name("instance-state-name")
                        .values("running")
                        .build();

                // Create a DescribeInstancesRequest object
                DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                        .filters(filter)
                        .build();

                // Find the running instances
                DescribeInstancesResponse response = ec2.describeInstances(request);

                for (Reservation reservation : response.reservations()) {
                    for (Instance instance : reservation.instances()) {
                        listInstances.add(instance.instanceId());
                    }
                }
                nextToken = response.nextToken();

            } while (nextToken != null);

            return listInstances;
//        } catch (Ec2Exception e) {
//            System.err.println(e.awsErrorDetails().errorMessage());
//            System.exit(1);
//        }
//        // snippet-end:[ec2.java2.running_instances.main]
//        return null;
    }
    // snippet-start:[ec2.java2.start_stop_instance.start]
    public static void startInstance(Ec2Client ec2, String instanceId) {

        StartInstancesRequest request = StartInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        ec2.startInstances(request);

        // snippet-end:[ec2.java2.start_stop_instance.start]
        System.out.printf("Successfully started instance %s", instanceId);
    }

    // snippet-start:[ec2.java2.start_stop_instance.stop]
    public static void stopInstance(Ec2Client ec2, String instanceId) {

        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        ec2.stopInstances(request);

        // snippet-end:[ec2.java2.start_stop_instance.stop]
        System.out.printf("Successfully stopped instance %s", instanceId);
    }
    public static String terminateEC2( Ec2Client ec2, String instanceID) {
        if(instanceID.equals("")) return "";



//        try{
            //stop and then terminate

            // snippet-end:[ec2.java2.start_stop_instance.stop]
            System.out.printf("Successfully stopped instance %s", instanceID);


            TerminateInstancesRequest ti = TerminateInstancesRequest.builder()
                    .instanceIds(instanceID)
                    .build();

            TerminateInstancesResponse response = ec2.terminateInstances(ti);

            List<InstanceStateChange> list = response.terminatingInstances();

            InstanceStateChange sc = (list.get(0));
                //System.out.println("The ID of the terminated instance is "+sc.instanceId());
                return sc.instanceId();

//        } catch (Ec2Exception e) {
//            System.err.println(e.awsErrorDetails().errorMessage());
//            System.exit(1);
//        }
//        return "";
    }
}
