package aws.example.s3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.*;
import static org.powermock.api.support.membermodification.MemberMatcher.method;
import static org.powermock.api.support.membermodification.MemberModifier.stub;

import java.io.*;
import java.util.Arrays;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.StringInputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.amazonaws.services.s3.AmazonS3ClientBuilder;

//@Mock


@RunWith(PowerMockRunner.class)
@PrepareForTest({ AmazonS3ClientBuilder.class})
@PowerMockIgnore("javax.management.*")
public class GetObjectTest {


//    @InjectMocks
//    GetObject getObjectTestObject;
//
//    @Before
//    public void init() {
//        MockitoAnnotations.initMocks(this);
//    }

    @Test
    public void testS3Mock() throws FileNotFoundException {

        String testString = "This is test code from the mock method";
        // create a test S3Object containing a stream of a test message
        S3Object so = new S3Object();
        try {
            so.setObjectContent(new StringInputStream(testString));
        } catch(java.io.UnsupportedEncodingException e) {
            assertTrue(false);
            return;
        }

        // create a mock instance of AmazonS3Client. This mock will not implement any behavior except
        // that when the getObject method is invoked, it will return a test string instead of the
        // contents of the file
        AmazonS3Client s3 = mock(AmazonS3Client.class);
        when(s3.getObject("bucket", "syllabus.txt")).thenReturn(so);

        // PowerMockito framework allows you to stub static method as well as instance methods
        // Here we are stubbing the entire AmazonS3ClientBuilder class
        PowerMockito.mock(AmazonS3ClientBuilder.class);
        stub(method(AmazonS3ClientBuilder.class, "build")).toReturn(s3);

        GetObject go = new GetObject();
        go.getIt("bucket", "syllabus.txt");

        File file = new File("syllabus.txt");

        String s;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));

            s = br.readLine();
        } catch (Exception e) {
            assertTrue(false);
            return;
        }

        assertEquals(testString, s);
    }

//    @Test(expected = AmazonServiceException.class)
//    public void testS3MockFileNotFound() throws FileNotFoundException {
//
//        String testString = "This is test code from the mock method";
//        // create a test S3Object containing a stream of a test message
//        S3Object so = new S3Object();
//        try {
//            so.setObjectContent(new StringInputStream(testString));
//        } catch(java.io.UnsupportedEncodingException e) {
//            assertTrue(false);
//            return;
//        }
//
//        // create a mock instance of AmazonS3Client. This mock will not implement any behavior except
//        // that when the getObject method is invoked, it will return a test string instead of the
//        // contents of the file
//        AmazonS3Client s3 = mock(AmazonS3Client.class);
//        when(s3.getObject("bucket", "wrong_file_name")).thenThrow(AmazonServiceException.class);
//
//        // PowerMockito framework allows you to stub static method as well as instance methods
//        // Here we are stubbing the entire AmazonS3ClientBuilder class
//        PowerMockito.mock(AmazonS3ClientBuilder.class);
//        stub(method(AmazonS3ClientBuilder.class, "build")).toReturn(s3);
//
//        GetObject go = new GetObject();
//        go.getIt("bucket", "wrong_file_name");
//
//
//        assertTrue(false);
//    }
}
