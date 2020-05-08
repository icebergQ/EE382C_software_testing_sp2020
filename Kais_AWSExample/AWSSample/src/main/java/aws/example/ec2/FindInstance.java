// snippet-comment:[These are tags for the AWS doc team's sample catalog. Do not remove.]
// snippet-sourcedescription:[FindRunningInstances.java demonstrates how to use a Filter to find running instances]
// snippet-service:[ec2]
// snippet-keyword:[Java]
// snippet-keyword:[Code Sample]
// snippet-sourcetype:[full-example]
// snippet-sourcedate:[2020-01-10]
// snippet-sourceauthor:[AWS-scmacdon]

/**
 * Copyright 2010-2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 */

// snippet-start:[ec2.java2.running_instances.complete]
package aws.example.ec2;

// snippet-start:[ec2.java2.running_instances.import]
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.Filter;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Reservation;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.Ec2Exception;

import java.util.List;


// snippet-end:[ec2.java2.running_instances.import]

/**
 * Locates all running EC2 instances using a Filter
 */
public class FindInstance {
    public static void main(String[] args) {
        Ec2Client ec2 = Ec2Client.create();



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


        // Find the running instances
        DescribeInstancesResponse response = ec2.describeInstances(request);

        List<Reservation> reservationList = response.reservations();
        Reservation reservation = reservationList.get(0);

        List<Instance> instanceList = reservation.instances();
        Instance instance = instanceList.get(0);
        System.out.println(instance.instanceId());

        // snippet-end:[ec2.java2.running_instances.main]
    }
}
// snippet-end:[ec2.java2.running_instances.complete]
