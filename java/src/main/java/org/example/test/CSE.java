package org.example.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Random;

import org.example.s3tests.AES256;
import org.example.s3tests.MainData;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.s3.model.CompleteMultipartUploadRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PartETag;
import com.amazonaws.services.s3.model.UploadPartRequest;

public class CSE extends TestBase
{
	@org.junit.jupiter.api.BeforeAll
	static public void BeforeAll()
	{
		System.out.println("CSE Start");
	}

	@org.junit.jupiter.api.AfterAll
	static public void AfterAll()
	{
		System.out.println("CSE End");
	}

	@Test
	@DisplayName("test_cse_encrypted_transfer_1b")
	@Tag("PutGet")
	//@Tag("[AES256] 1Byte 오브젝트를 암호화 하여 업로드한뒤, 다운로드하여 복호화 했을 경우 일치하는지 확인")
	public void test_cse_encrypted_transfer_1b() {
		TestEncryptionCSEWrite(1);
	}

	@Test
	@DisplayName("test_cse_encrypted_transfer_1kb")
	@Tag("PutGet")
	//@Tag("[AES256] 1KB 오브젝트를 암호화 하여 업로드한뒤, 다운로드하여 복호화 했을 경우 일치하는지 확인")
	public void test_cse_encrypted_transfer_1kb() {
		TestEncryptionCSEWrite(1024);
	}

	@Test
	@DisplayName("test_cse_encrypted_transfer_1MB")
	@Tag("PutGet")
	//@Tag("[AES256] 1MB 오브젝트를 암호화 하여 업로드한뒤, 다운로드하여 복호화 했을 경우 일치하는지 확인")
	public void test_cse_encrypted_transfer_1MB() {
		TestEncryptionCSEWrite(1024 * 1024);
	}

	@Test
	@DisplayName("test_cse_encrypted_transfer_13b")
	@Tag("PutGet")
	//@Tag("[AES256] 13Byte 오브젝트를 암호화 하여 업로드한뒤, 다운로드하여 복호화 했을 경우 일치하는지 확인")
	public void test_cse_encrypted_transfer_13b() {
		TestEncryptionCSEWrite(13);
	}

	@Test
	@DisplayName("test_cse_encryption_method_head")
	@Tag("Metadata")
	//@Tag("[AES256] 암호화하고 메타데이터에 키값을 추가하여 업로드한 오브젝트가 올바르게 반영되었는지 확인 ")
	public void test_cse_encryption_method_head() {
		var BucketName = GetNewBucket();
		var Client = GetClient();
		var Key = "testobj";
		var Size = 1000;
		var Data = RandomTextToLong(Size);

		// AES
		var AESKey = RandomTextToLong(32);
		try {
			var EncodingData = AES256.encryptAES256(Data, AESKey);
			var MetadataList = new ObjectMetadata();
			MetadataList.addUserMetadata("x-amz-meta-key", AESKey);
			MetadataList.setContentType("text/plain");
			MetadataList.setContentLength(EncodingData.length());

			Client.putObject(BucketName, Key, CreateBody(EncodingData), MetadataList);

			var ResMetaData = Client.getObjectMetadata(BucketName, Key);
			assertEquals(MetadataList.getUserMetadata(), ResMetaData.getUserMetadata());
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("test_cse_encryption_non_decryption")
	@Tag("ERROR")
	//@Tag("[AES256] 암호화 하여 업로드한 오브젝트를 다운로드하여 비교할경우 불일치")
	public void test_cse_encryption_non_decryption() {
		var BucketName = GetNewBucket();
		var Client = GetClient();
		var Key = "testobj";
		var Size = 1000;
		var Data = RandomTextToLong(Size);

		// AES
		var AESKey = RandomTextToLong(32);

		try {
			var EncodingData = AES256.encryptAES256(Data, AESKey);
			var MetadataList = new ObjectMetadata();
			MetadataList.addUserMetadata("x-amz-meta-key", AESKey);
			MetadataList.setContentType("text/plain");
			MetadataList.setContentLength(EncodingData.length());

			Client.putObject(BucketName, Key, CreateBody(EncodingData), MetadataList);

			var Response = Client.getObject(BucketName, Key);
			var Body = GetBody(Response.getObjectContent());
			assertNotEquals(Data, Body);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("test_cse_non_encryption_decryption")
	@Tag("ERROR")
	//@Tag("[AES256] 암호화 없이 업로드한 오브젝트를 다운로드하여 복호화할 경우 실패 확인")
	public void test_cse_non_encryption_decryption() {
		var BucketName = GetNewBucket();
		var Client = GetClient();
		var Key = "testobj";
		var Size = 1000;
		var Data = RandomTextToLong(Size);

		// AES
		var AESKey = RandomTextToLong(32);

		var MetadataList = new ObjectMetadata();
		MetadataList.addUserMetadata("x-amz-meta-key", AESKey);
		MetadataList.setContentType("text/plain");
		MetadataList.setContentLength(Size);
		Client.putObject(BucketName, Key, CreateBody(Data), MetadataList);

		var Response = Client.getObject(BucketName, Key);
		var EncodingBody = GetBody(Response.getObjectContent());
		assertThrows(Exception.class, () -> AES256.decryptAES256(EncodingBody, AESKey));
	}

	@Test
	@DisplayName("test_cse_encryption_range_read")
	@Tag("RangeRead")
	//@Tag("[AES256] 암호화 하여 업로드한 오브젝트에 대해 범위를 지정하여 읽기 성공")
	public void test_cse_encryption_range_read() {
		var BucketName = GetNewBucket();
		var Client = GetClient();
		var Key = "testobj";

		// AES
		var AESKey = RandomTextToLong(32);

		try {
			var Data = RandomTextToLong(1024 * 1024);
			var EncodingData = AES256.encryptAES256(Data, AESKey);
			var MetadataList = new ObjectMetadata();
			MetadataList.addUserMetadata("x-amz-meta-key", AESKey);
			MetadataList.setContentType("text/plain");
			MetadataList.setContentLength(EncodingData.length());
			Client.putObject(BucketName, Key, CreateBody(EncodingData), MetadataList);

			var r = new Random();
			var StartPoint = r.nextInt(1024 * 1024 - 1001);
			var Response = Client
					.getObject(new GetObjectRequest(BucketName, Key).withRange(StartPoint, StartPoint + 1000 - 1));
			var EncodingBody = GetBody(Response.getObjectContent());
			assertEquals(EncodingData.substring(StartPoint, StartPoint + 1000), EncodingBody);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	@Test
	@DisplayName("test_cse_encryption_multipart_upload")
	@Tag("Multipart")
	//@Tag("[AES256] 암호화된 오브젝트 멀티파트 업로드 / 다운로드 성공 확인")
	public void test_cse_encryption_multipart_upload() {
		var BucketName = GetNewBucket();
		var Client = GetClient();
		var Key = "multipart_enc";
		var Size = 30 * MainData.MB;
		var ContentType = "text/plain";
		var Data = RandomTextToLong(Size);

		// AES
		var AESKey = RandomTextToLong(32);

		try {
			var EncodingData = AES256.encryptAES256(Data, AESKey);
			var MetadataList = new ObjectMetadata();
			MetadataList.addUserMetadata("x-amz-meta-key", AESKey);
			MetadataList.setContentType(ContentType);

			var InitMultiPartResponse = Client
					.initiateMultipartUpload(new InitiateMultipartUploadRequest(BucketName, Key, MetadataList));
			var UploadID = InitMultiPartResponse.getUploadId();

			var Parts = CutStringData(EncodingData, 5 * MainData.MB);
			var PartETag = new ArrayList<PartETag>();
			int PartNumber = 1;
			for (var Part : Parts) {
				var PartResPonse = Client.uploadPart(new UploadPartRequest().withBucketName(BucketName).withKey(Key)
						.withUploadId(UploadID).withPartNumber(PartNumber++).withInputStream(CreateBody(Part))
						.withPartSize(Part.length()));
				PartETag.add(PartResPonse.getPartETag());
			}

			Client.completeMultipartUpload(new CompleteMultipartUploadRequest(BucketName, Key, UploadID, PartETag));

			var HeadResponse = Client.listObjectsV2(BucketName);
			var ObjectCount = HeadResponse.getKeyCount();
			assertEquals(1, ObjectCount);
			assertEquals(EncodingData.length(), GetBytesUsed(HeadResponse));

			var GetResponse = Client.getObject(BucketName, Key);
			assertEquals(MetadataList.getUserMetadata(), GetResponse.getObjectMetadata().getUserMetadata());
			assertEquals(ContentType, GetResponse.getObjectMetadata().getContentType());

			var EncodingBody = GetBody(GetResponse.getObjectContent());
			var Body = AES256.decryptAES256(EncodingBody, AESKey);
			assertEquals(Size, Body.length());
			assertEquals(Data, Body);

			CheckContentUsingRange(BucketName, Key, EncodingData, 1000000);
			CheckContentUsingRange(BucketName, Key, EncodingData, 10000000);
			CheckContentUsingRandomRange(BucketName, Key, EncodingData, Size, 100);
	} catch (Exception e) {
			fail(e.getMessage());
		}
	}
    
    @Test
	@DisplayName("test_cse_get_object_many")
    @Tag("Get")
    //@Tag("CSE설정한 오브젝트를 여러번 반복하여 다운로드 성공 확인")
    public void test_cse_get_object_many()
    {
        var BucketName = GetNewBucket();
        var Client = GetClient();
        var Key = "foo";
		// AES
		var AESKey = RandomTextToLong(32);
        var Data = RandomTextToLong(15 * 1024 * 1024);
		
		try {
			var EncodingData = AES256.encryptAES256(Data, AESKey);
			var MetadataList = new ObjectMetadata();
			MetadataList.addUserMetadata("AESkey", AESKey);
			MetadataList.setContentType("text/plain");
			MetadataList.setContentLength(EncodingData.length());

			Client.putObject(BucketName, Key, CreateBody(EncodingData), MetadataList);

			var Response = Client.getObject(BucketName, Key);
			var EncodingBody = GetBody(Response.getObjectContent());
			var Body = AES256.decryptAES256(EncodingBody, AESKey);
			assertEquals(Data, Body);
			CheckContent(BucketName, Key, EncodingData, 50);

		} catch (Exception e) {
			fail(e.getMessage());
		}
    }
    
    @Test
	@DisplayName("test_cse_range_object_many")
    @Tag("Get")
    //@Tag("CSE설정한 오브젝트를 여러번 반복하여 Range 다운로드 성공 확인")
    public void test_cse_range_object_many()
    {
        var BucketName = GetNewBucket();
        var Client = GetClient();
        var Key = "foo";

		// AES
		var AESKey = RandomTextToLong(32);
        var FileSize = 15 * 1024 * 1024;
        var Data = RandomTextToLong(FileSize);
		
		try {
			var EncodingData = AES256.encryptAES256(Data, AESKey);
			var MetadataList = new ObjectMetadata();
			MetadataList.addUserMetadata("AESkey", AESKey);
			MetadataList.setContentType("text/plain");
			MetadataList.setContentLength(EncodingData.length());

			Client.putObject(BucketName, Key, CreateBody(EncodingData), MetadataList);

			var Response = Client.getObject(BucketName, Key);
			var EncodingBody = GetBody(Response.getObjectContent());
			var Body = AES256.decryptAES256(EncodingBody, AESKey);
			assertEquals(Data, Body);

			CheckContentUsingRandomRange(BucketName, Key, EncodingData, EncodingData.length(), 50);

		} catch (Exception e) {
			fail(e.getMessage());
		}
    }
}