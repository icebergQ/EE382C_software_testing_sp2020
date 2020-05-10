package aws.s3_module;

import com.amazonaws.AmazonServiceException;
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
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AmazonS3ClientBuilder.class})
@PowerMockIgnore("javax.management.*")

public class S3Test {

    @Mock
    AmazonS3 s3;

    @Test
    public void putObjectTest() {
        s3 = mock(AmazonS3.class);
        PutObjectResult result = new PutObjectResult();
        when(s3.putObject("bucketname", "filename", new File("filename"))).thenReturn(result);
        stub(method(AmazonS3ClientBuilder.class, "build")).toReturn(s3);

        ObjectManager objectManager = new ObjectManager();
        objectManager.putObject("bucketname", "filename");
    }
}
