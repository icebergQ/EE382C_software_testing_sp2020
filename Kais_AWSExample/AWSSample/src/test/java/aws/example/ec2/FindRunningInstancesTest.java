package aws.example.ec2;
// snippet-start:[ec2.java2.running_instances.complete]


// snippet-start:[ec2.java2.running_instances.import]
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;

import java.util.List;

import static org.mockito.Mockito.*;
// snippet-end:[ec2.java2.running_instances.import]

/**
 * Locates all running EC2 instances using a Filter
 */
public class FindRunningInstancesTest {
    public static void main(String[] args) {
        //Ec2Client ec2 = Ec2Client.create();
        // snippet-start:[ec2.java2.running_instances.main]





            String nextToken = null;


                // Create a Filter to find all running instances
                Filter filter = Filter.builder()
                        .name("instance-state-name")
                        .values("running")
                        .build();

                //Create a DescribeInstancesRequest
                DescribeInstancesRequest request = DescribeInstancesRequest.builder()
                        .filters(filter)
                        .build();

                DescribeInstancesResponse res = mock(DescribeInstancesResponse.class);
                Reservation reserv = mock(Reservation.class);
                Instance inst = mock(Instance.class);

                List instList = mock(List.class);
                List reservList = mock(List.class);

                Ec2Client ec2 = mock(Ec2Client.class);
                when(ec2.describeInstances(request)).thenReturn(res);
                when(res.reservations()).thenReturn(reservList);
                when(reserv.instances()).thenReturn(instList);
                when(instList.get(0)).thenReturn(inst);
                when(reservList.get(0)).thenReturn(reserv);
                when(inst.instanceId()).thenReturn("FAKE_ID");

                // Find the running instances
                DescribeInstancesResponse response = ec2.describeInstances(request);

                List<Reservation> reservationList = response.reservations();
                Reservation reservation = reservationList.get(0);

                List<Instance> instanceList = reservation.instances();
                Instance instance = instanceList.get(0);
                System.out.println(instance.instanceId());

//                for (Reservation reservation : response.reservations()) {
//                    for (Instance instance : reservation.instances()) {
//                        System.out.printf(
//                                "Found reservation with id %s, " +
//                                        "AMI %s, " +
//                                        "type %s, " +
//                                        "state %s " +
//                                        "and monitoring state %s",
//                                instance.instanceId(),
//                                instance.imageId(),
//                                instance.instanceType(),
//                                instance.state().name(),
//                                instance.monitoring().state());
//                        System.out.println("");
//                    }
//                }
//                nextToken = response.nextToken();
//
//            } while (nextToken != null);



        }

        // snippet-end:[ec2.java2.running_instances.main]

}
