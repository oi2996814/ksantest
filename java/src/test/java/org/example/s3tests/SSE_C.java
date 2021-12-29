package org.example.s3tests;

import com.amazonaws.SDKGlobalConfiguration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class SSE_C {

    org.example.test.SSE_C Test = new org.example.test.SSE_C();

    @BeforeEach
    public void trustAllHosts() {
        System.setProperty(SDKGlobalConfiguration.DISABLE_CERT_CHECKING_SYSTEM_PROPERTY, "true");
    }

    @AfterEach
	public void Clear() {
		Test.Clear();
	}
    
    @Test
    @DisplayName("test_encrypted_transfer_1b")
    @Tag("PutGet")
    // @Tag("1Byte 오브젝트를 SSE-C 설정하여 업/다운로드가 올바르게 동작하는지 확인")
    public void test_encrypted_transfer_1b()
    {
        Test.test_encrypted_transfer_1b();
    }

    @Test
    @DisplayName("test_encrypted_transfer_1kb")
    @Tag("PutGet")
    // @Tag("1KB 오브젝트를 SSE-C 설정하여 업/다운로드가 올바르게 동작하는지 확인")
    public void test_encrypted_transfer_1kb()
    {
        Test.test_encrypted_transfer_1kb();
    }

    @Test
    @DisplayName("test_encrypted_transfer_1MB")
    @Tag("PutGet")
    // @Tag("1MB 오브젝트를 SSE-C 설정하여 업/다운로드가 올바르게 동작하는지 확인")
    public void test_encrypted_transfer_1MB()
    {
        Test.test_encrypted_transfer_1MB();
    }

    @Test
    @DisplayName("test_encrypted_transfer_13b")
    @Tag("PutGet")
    // @Tag("13Byte 오브젝트를 SSE-C 설정하여 업/다운로드가 올바르게 동작하는지 확인")
    public void test_encrypted_transfer_13b()
    {
        Test.test_encrypted_transfer_13b();
    }

    @Test
    @DisplayName("test_encryption_sse_c_method_head")
    @Tag("Metadata")
    // @Tag("SSE-C 설정하여 업로드한 오브젝트를 SSE-C 설정하여 헤더정보읽기가 가능한지 확인")
    public void test_encryption_sse_c_method_head()
    {
        Test.test_encryption_sse_c_method_head();
    }

    @Test
    @DisplayName("test_encryption_sse_c_present")
    @Tag("ERROR")
    // @Tag("SSE-C 설정하여 업로드한 오브젝트를 SSE-C 설정없이 다운로드 실패 확인")
    public void test_encryption_sse_c_present()
    {
        Test.test_encryption_sse_c_present();
    }

    @Test
    @DisplayName("test_encryption_sse_c_other_key")
    @Tag("ERROR")
    // @Tag("SSE-C 설정하여 업로드한 오브젝트와 다른 SSE-C 설정으로 다운로드 실패 확인")
    public void test_encryption_sse_c_other_key()
    {
        Test.test_encryption_sse_c_other_key();
    }

    @Test
    @DisplayName("test_encryption_sse_c_invalid_md5")
    @Tag("ERROR")
    // @Tag("SSE-C 설정값중 key-md5값이 올바르지 않을 경우 업로드 실패 확인")
    public void test_encryption_sse_c_invalid_md5()
    {
        Test.test_encryption_sse_c_invalid_md5();
    }

    @Test
    @DisplayName("test_encryption_sse_c_no_md5")
    @Tag("ERROR")
    // @Tag("SSE-C 설정값중 key-md5값을 누락했을 경우 업로드 성공 확인")
    public void test_encryption_sse_c_no_md5()
    {
        Test.test_encryption_sse_c_no_md5();
    }

    @Test
    @DisplayName("test_encryption_sse_c_no_key")
    @Tag("ERROR")
    // @Tag("SSE-C 설정값중 key값을 누락했을 경우 업로드 실패 확인")
    public void test_encryption_sse_c_no_key()
    {
        Test.test_encryption_sse_c_no_key();
    }
    @Test
    @DisplayName("test_encryption_key_no_sse_c")
    @Disabled("JAVA 에서는 algorithm값을 누락해도 기본값이 지정되어 있어 에러가 발생하지 않음")
    @Tag("ERROR")
    // @Tag("SSE-C 설정값중 algorithm값을 누락했을 경우 업로드 실패 확인")
    public void test_encryption_key_no_sse_c()
    {
        Test.test_encryption_key_no_sse_c();
    }

    @Test
    @DisplayName("test_encryption_sse_c_multipart_upload")
    @Tag("Multipart")
    // @Tag("멀티파트업로드를 SSE-C 설정하여 업로드 가능 확인")
    public void test_encryption_sse_c_multipart_upload()
    {
        Test.test_encryption_sse_c_multipart_upload();
    }

    @Test
    @DisplayName("test_encryption_sse_c_multipart_bad_download")
    @Tag("Multipart")
    // @Tag("SSE-C 설정하여 멀티파트 업로드한 오브젝트와 다른 SSE-C 설정으로 다운로드 실패 확인")
    public void test_encryption_sse_c_multipart_bad_download()
    {
        Test.test_encryption_sse_c_multipart_bad_download();
    }

    @Test
    @DisplayName("test_encryption_sse_c_post_object_authenticated_request")
    @Tag("Post")
    // @Tag("Post 방식으로 SSE-C 설정하여 오브젝트 업로드가 올바르게 동작하는지 확인")
    public void test_encryption_sse_c_post_object_authenticated_request()
    {
        Test.test_encryption_sse_c_post_object_authenticated_request();
    }

    @Test
    @DisplayName("test_encryption_sse_c_get_object_many")
    @Tag("Get")
    // @Tag("SSE-C설정한 오브젝트를 여러번 반복하여 다운로드 성공 확인")
    public void test_encryption_sse_c_get_object_many()
    {
        Test.test_encryption_sse_c_get_object_many();
    }

    @Test
    @DisplayName("test_encryption_sse_c_range_object_many")
    @Tag("Get")
    // @Tag("SSE-C설정한 오브젝트를 여러번 반복하여 Range 다운로드 성공 확인")
    public void test_encryption_sse_c_range_object_many()
    {
        Test.test_encryption_sse_c_range_object_many();
    }
}
