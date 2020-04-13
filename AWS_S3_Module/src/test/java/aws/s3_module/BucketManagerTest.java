package aws.s3_module;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AmazonS3ClientBuilder.class})
@PowerMockIgnore("javax.management.*")
public class BucketManagerTest {

    String existentBucketName = "dummy-test-bucket-1";
    String nonExistentBucketName = "dummy-test-bucket-2";

    @Mock AmazonS3Client s3;

    @Before
    public void prepare() {

        S3ObjectSummary objSumm1 = new S3ObjectSummary();
        objSumm1.setBucketName("dummy-test-object-1");
        S3ObjectSummary objSumm2 = new S3ObjectSummary();
        objSumm1.setBucketName("dummy-test-object-2");
        List<S3ObjectSummary> objSummList = new ArrayList<>();
        objSummList.add(objSumm1);
        objSummList.add(objSumm2);

        ObjectListing ol = mock(ObjectListing.class);
        when(ol.getObjectSummaries()).thenReturn(objSummList);

        // a list of things to do before I die
        Bucket b1 = new Bucket(existentBucketName);
        List<Bucket> bucketList = new ArrayList<Bucket>();
        bucketList.add(b1);

        // create a mock instance of AmazonS3Client. This mock will not implement any behavior except
        // that when the getObject method is invoked, it will return a test string instead of the
        // contents of the file
        //AmazonS3Client s3 = mock(AmazonS3Client.class);
        s3 = mock(AmazonS3Client.class);
        when(s3.listObjects(existentBucketName)).thenReturn(ol);
        when(s3.listObjects(nonExistentBucketName)).thenThrow(new com.amazonaws.services.s3.model.AmazonS3Exception("The specified bucket does not exist"));
        when(s3.listVersions(any(ListVersionsRequest.class))).thenReturn(new VersionListing());
        when(s3.listBuckets()).thenReturn(bucketList);

        when(s3.createBucket(existentBucketName)).thenThrow(new AmazonServiceException("bucket already exists: " + existentBucketName));
        when(s3.createBucket(nonExistentBucketName)).thenReturn(b1);

        when(s3.doesBucketExistV2(existentBucketName)).thenReturn(true);
        when(s3.doesBucketExistV2(nonExistentBucketName)).thenReturn(false);

        // PowerMockito framework allows you to stub static method as well as instance methods
        // Here we are stubbing the entire AmazonS3ClientBuilder class
        PowerMockito.mock(AmazonS3ClientBuilder.class);

        stub(method(AmazonS3ClientBuilder.class, "build")).toReturn(s3);
    }

    @Test
    public void DeleteBucketTest() {
        BucketManager bucketManager = new BucketManager();
        bucketManager.deleteBucket(existentBucketName);

        verify(s3, times(1)).listObjects(existentBucketName);
        assertTrue(true);
    }

    @Test(expected = AmazonS3Exception.class)
    public void DeleteBucket_NonExistent_Test() {
        BucketManager bucketManager = new BucketManager();
        bucketManager.deleteBucket(nonExistentBucketName);

        assertTrue(false);
    }

    @Test
    public void CreateBucketTest() {
        BucketManager bucketManager = new BucketManager();
        bucketManager.createBucket(nonExistentBucketName);

        InOrder inOrder = inOrder(s3);

        inOrder.verify(s3).doesBucketExistV2(nonExistentBucketName);
        inOrder.verify(s3).createBucket(nonExistentBucketName);
    }

    @Test
    public void CreateBucketTest2() {
        BucketManager bucketManager = new BucketManager();
        Bucket b = bucketManager.createBucket(existentBucketName);

        assertTrue(b.getName().equals(existentBucketName));
    }

    @Test
    public void GetBucketTest() {
        BucketManager bucketManager = new BucketManager();
        Bucket bucket = bucketManager.getBucket(existentBucketName);
        assertTrue(bucket.getName() == existentBucketName);
    }

    @Test
    public void GetBucketTest_NonExistent() {
        BucketManager bucketManager = new BucketManager();
        Bucket bucket = bucketManager.getBucket(nonExistentBucketName);
        assertTrue(bucket == null);
    }
}
