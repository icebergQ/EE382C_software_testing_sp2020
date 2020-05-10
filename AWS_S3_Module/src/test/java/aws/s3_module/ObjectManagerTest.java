package aws.s3_module;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.util.StringInputStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AmazonS3ClientBuilder.class})
@PowerMockIgnore("javax.management.*")
public class ObjectManagerTest {

    String existentTestBucket = "existent-test-bucket";
    String existentTestBucket2 = "existent-test-bucket2";
    String nonexistentTestBucket = "non-existent-test-bucket";
    String existentLocalFile = "existent-local-file";
    String nonexistentLocalFile = "nonexistent-local-file";
    String existentTestKey = "existent-test-key-1";
    String nonexistentTestKey = "nonexistent-test-key";
    String testString = "This is test code from the mock method";

    @Mock
    AmazonS3 s3;
    @Mock
    ListObjectsV2Result listObjectsV2Result;

    @Before
    public void prepare() {
        s3 = mock(AmazonS3.class);

        PutObjectResult result = new PutObjectResult();
        when(s3.putObject(existentTestBucket, "dummy-key", new File(existentLocalFile))).thenReturn(result);
        when(s3.putObject(eq(nonexistentTestBucket), anyString(), any(File.class))).thenThrow(new AmazonServiceException("non-existent bucket"));
        when(s3.putObject(anyString(), anyString(), eq(new File(nonexistentLocalFile)))).thenThrow(new AmazonServiceException("non-existent local file"));

        S3ObjectSummary objSumm1 = new S3ObjectSummary();
        objSumm1.setBucketName(existentTestBucket);
        objSumm1.setKey(existentTestKey);

        List<S3ObjectSummary> objSummList = new ArrayList<>();
        objSummList.add(objSumm1);

        listObjectsV2Result = mock(ListObjectsV2Result.class);
        when(listObjectsV2Result.getObjectSummaries()).thenReturn(objSummList);

        when(s3.listObjectsV2(existentTestBucket)).thenReturn(listObjectsV2Result);
        when(s3.listObjectsV2(nonexistentTestBucket)).thenThrow(new AmazonServiceException("non-existent bucket"));

        // set up objects for downloadObjectTest
        S3Object s3Object = new S3Object();
        try {
            s3Object.setObjectContent(new StringInputStream(testString));
        } catch(java.io.UnsupportedEncodingException e) {
            fail();
            return;
        }
        when(s3.getObject(existentTestBucket, existentTestKey)).thenReturn(s3Object);
        when(s3.getObject(eq(nonexistentTestBucket), anyString())).thenThrow(new AmazonServiceException("non-existent bucket name: " + nonexistentTestBucket));
        when(s3.getObject(anyString(), eq(nonexistentTestKey) )).thenThrow(new AmazonServiceException("non-existent key: " + nonexistentTestKey));


        // set up objects for copyObjectTest
        CopyObjectResult copyObjectResult = new CopyObjectResult();
        when(s3.copyObject(existentTestBucket, existentTestKey, existentTestBucket2,existentTestKey)).thenReturn(copyObjectResult);
        when(s3.copyObject(eq(nonexistentTestBucket), anyString(),anyString(),anyString())).thenThrow(new AmazonServiceException("non-existent from bucket: " + nonexistentTestBucket));
        when(s3.copyObject(anyString(), anyString(),eq(nonexistentTestBucket),anyString())).thenThrow(new AmazonServiceException("non-existent to bucket: " + nonexistentTestBucket));
        when(s3.copyObject(existentTestBucket, nonexistentTestKey, existentTestBucket2, nonexistentTestKey)).thenThrow(new AmazonServiceException("non-existent key: " + nonexistentTestKey));
        // PowerMockito framework allows you to stub static method as well as instance methods
        // Here we are stubbing the entire AmazonS3ClientBuilder class
        PowerMockito.mock(AmazonS3ClientBuilder.class);

        stub(method(AmazonS3ClientBuilder.class, "build")).toReturn(s3);
    }

    @After
    public void cleanUp() {
        File f = new File(existentTestKey);
        if (f.exists()) {
            f.delete();
        }
    }

    @Test
    public void putObjectTest() {
        ObjectManager objectManager = new ObjectManager();
        objectManager.putObject(existentTestBucket, existentLocalFile);

        assertTrue(true);
    }

    @Test (expected = AmazonServiceException.class)
    public void putObjectTest2() {
        ObjectManager objectManager = new ObjectManager();
        objectManager.putObject(nonexistentTestBucket, existentLocalFile);

        fail();
    }

    @Test (expected = AmazonServiceException.class)
    public void putObjectTest3() {
        ObjectManager objectManager = new ObjectManager();
        objectManager.putObject(existentTestBucket, nonexistentLocalFile);

        fail();
    }

    @Test
    public void listObjectsTest() {
        ObjectManager objectManager = new ObjectManager();
        List<String>  lst = objectManager.listObjects(existentTestBucket);

        assertTrue(lst.size() == 1 && lst.get(0).equals(existentTestKey));
    }

    @Test (expected = AmazonServiceException.class)
    public void listObjectsTest2() {
        ObjectManager objectManager = new ObjectManager();
        List<String>  lst = objectManager.listObjects(nonexistentTestBucket);

        fail();
    }

    @Test
    public void downloadObjectTest() {
        ObjectManager objectManager = new ObjectManager();
        try {
            objectManager.downloadObject(existentTestBucket, existentTestKey);
        } catch (IOException e) {
            // shouldn't get this exception
            fail();
        }

        File file = new File(existentTestKey);

        String s = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            s = br.readLine();
        } catch (Exception e) {
            // shouldn't get this exception
            fail();
        }

        assertEquals(testString, s);
    }

    @Test (expected = AmazonServiceException.class)
    public void downloadObjectTest2() {
        ObjectManager objectManager = new ObjectManager();
        try {
            objectManager.downloadObject(nonexistentTestBucket, existentTestKey);
        } catch (IOException e) {
            // shouldn't get this exception
            fail();
        }
    }

    @Test (expected = AmazonServiceException.class)
    public void downloadObjectTest3() {
        ObjectManager objectManager = new ObjectManager();
        try {
            objectManager.downloadObject(existentTestBucket, nonexistentTestKey);
        } catch (IOException e) {
            // shouldn't get this exception
            fail();
        }
    }

    @Test
    public void copyObjectTest() {
        ObjectManager objectManager = new ObjectManager();
        objectManager.copyObject(existentTestBucket, existentTestKey, existentTestBucket2);

        verify(s3).copyObject(existentTestBucket, existentTestKey, existentTestBucket2, existentTestKey);
    }

    @Test (expected = AmazonServiceException.class)
    public void copyObjectTest2() {
        ObjectManager objectManager = new ObjectManager();
        objectManager.copyObject(nonexistentTestBucket, existentTestKey, existentTestBucket2);

        fail();
    }

    @Test (expected = AmazonServiceException.class)
    public void copyObjectTest3() {
        ObjectManager objectManager = new ObjectManager();
        objectManager.copyObject(existentTestBucket, nonexistentTestKey, existentTestBucket2);

        fail();
    }

    @Test (expected = AmazonServiceException.class)
    public void copyObjectTest4() {
        ObjectManager objectManager = new ObjectManager();
        objectManager.copyObject(existentTestBucket, existentTestKey, nonexistentTestBucket);

        fail();
    }
    

}
