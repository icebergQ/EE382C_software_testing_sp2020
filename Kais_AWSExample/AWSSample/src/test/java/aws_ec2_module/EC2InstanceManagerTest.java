package aws_ec2_module;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.booleanThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;


public class EC2InstanceManagerTest {

    @Mock Ec2Client ec2;

    @Before
    public void prepare() {

        RunInstancesResponse res = mock(RunInstancesResponse.class);
        RunInstancesRequest req = mock(RunInstancesRequest.class);
        // String instanceId = response.instances().get(0).instanceId();
        List<Instance> listInstances = mock(List.class);

        Instance ins = mock(Instance.class);
        listInstances.add(ins);
        CreateTagsRequest tagRequest = mock(CreateTagsRequest.class);


        ec2 = mock(Ec2Client.class);


        when(ec2.runInstances(any(RunInstancesRequest.class))).thenReturn(res);
        when(res.instances()).thenReturn(listInstances);
        when(listInstances.get(0)).thenReturn(ins);
        when(ins.instanceId()).thenReturn("instance ID");



        TerminateInstancesRequest termReq = mock(TerminateInstancesRequest.class);
        TerminateInstancesResponse termRes = mock(TerminateInstancesResponse.class);
        List<InstanceStateChange> listInsChanges = mock(List.class);
        InstanceStateChange insChange = mock(InstanceStateChange.class);
        listInsChanges.add(insChange);

        when(ec2.terminateInstances(any(TerminateInstancesRequest.class))).thenReturn(termRes);
        when(termRes.terminatingInstances()).thenReturn(listInsChanges);
        when(listInsChanges.get(0)).thenReturn(insChange);
        when(insChange.instanceId()).thenReturn("instance ID");




    }
    @Test
    public void createInstanceTest0(){
        EC2InstanceManager insManager = new EC2InstanceManager();

        String instanceID = insManager.createEC2Instance(ec2, "name", "0f7919c33c90f5b58");
        assertEquals(true, instanceID.equals("instance ID"));
    }
    @Test
    public void createInstanceTest1(){
        EC2InstanceManager insManager = new EC2InstanceManager();

        String instanceID = insManager.createEC2Instance(ec2, "", "0f7919c33c90f5b58");
        assertEquals(true, instanceID.equals(""));

    }
    @Test
    public void createInstanceTest2(){
        EC2InstanceManager insManager = new EC2InstanceManager();

        String instanceID = insManager.createEC2Instance(ec2, "name", "");
        assertEquals(true, instanceID.equals(""));

    }



    @Test(expected = NullPointerException.class)
    public void createInstanceTest3(){
        EC2InstanceManager insManager = new EC2InstanceManager();

        String instanceID = insManager.createEC2Instance(null, "name", "0f7919c33c90f5b58");

    }



    @Test
    public void terminateEC2Test0() {
        EC2InstanceManager insManager = new EC2InstanceManager();

        String insId = insManager.terminateEC2(ec2, "instance ID");
        assertEquals("instance ID",insId);

    }

    @Test
    public void terminateEC2Test1() {
        EC2InstanceManager insManager = new EC2InstanceManager();

        String insId = insManager.terminateEC2(ec2, "");
        assertEquals("",insId);

    }
    @Test(expected = NullPointerException.class)
    public void terminateEC2Test2() {
        EC2InstanceManager insManager = new EC2InstanceManager();

        String insId = insManager.terminateEC2(null, "instance ID");
        //assertEquals("",insId);

    }
    @Test
    public void stopEC2Test0() {
        EC2InstanceManager insManager = new EC2InstanceManager();

        insManager.stopInstance(ec2, "instance ID");


    }

    @Test
    public void startEC2Test0() {
        EC2InstanceManager insManager = new EC2InstanceManager();

        insManager.startInstance(ec2, "instance ID");


    }

    @Test(expected = NullPointerException.class)
    public void findRunningEC2Test0() {
        EC2InstanceManager insManager = new EC2InstanceManager();
        List<String> returnList = insManager.findRunningEC2Instances(ec2);


    }

    @Test
    public void findRunningEC2Test1() {
        EC2InstanceManager insManager = new EC2InstanceManager();

        //find running instances


        DescribeInstancesResponse findRes = mock(DescribeInstancesResponse.class);
        Reservation reserv = mock(Reservation.class);
        Instance inst = mock(Instance.class);

        List<Instance> instList= new LinkedList<>();
        //List instList = mock(List.class);
        instList.add(inst);
        List<Reservation> reservList= new LinkedList<>();
        //List reservList = mock(List.class);
        reservList.add(reserv);



        when(ec2.describeInstances(any(DescribeInstancesRequest.class))).thenReturn(findRes);
        when(findRes.reservations()).thenReturn(reservList);
        when(reserv.instances()).thenReturn(instList);
        //when(instList.get(0)).thenReturn(inst);
        //when(reservList.get(0)).thenReturn(reserv);
        when(inst.instanceId()).thenReturn("FAKE_ID");

        List<String> returnList = insManager.findRunningEC2Instances(ec2);
        assertEquals(instList.size(),returnList.size());

    }

    @Test
    public void findRunningEC2Test2() {
        EC2InstanceManager insManager = new EC2InstanceManager();

        //find running instances


        DescribeInstancesResponse findRes = mock(DescribeInstancesResponse.class);
        Reservation reserv = mock(Reservation.class);
        Instance inst = mock(Instance.class);
        Instance inst2 = mock(Instance.class);
        List<Instance> instList= new LinkedList<>();
        //List instList = mock(List.class);
        instList.add(inst);
        instList.add(inst2);
        List<Reservation> reservList= new LinkedList<>();
        //List reservList = mock(List.class);
        reservList.add(reserv);



        when(ec2.describeInstances(any(DescribeInstancesRequest.class))).thenReturn(findRes);
        when(findRes.reservations()).thenReturn(reservList);
        when(reserv.instances()).thenReturn(instList);
        //when(instList.get(0)).thenReturn(inst);
        //when(reservList.get(0)).thenReturn(reserv);
        when(inst.instanceId()).thenReturn("FAKE_ID");

        List<String> returnList = insManager.findRunningEC2Instances(ec2);
        assertEquals(instList.size(),returnList.size());
    }
}