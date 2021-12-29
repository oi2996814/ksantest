package org.example.s3tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class Post {

    org.example.test.Post Test = new org.example.test.Post();

    @AfterEach
	public void Clear() {
		Test.Clear();
	}

    @Test
    @DisplayName("test_post_object_anonymous_request")
    @Tag("Upload")
    // @Tag("post 방식으로 권한없는 사용자가 파일 업로드할 경우 성공 확인")
    public void test_post_object_anonymous_request()
    {
        Test.test_post_object_anonymous_request();
    }

    @Test
    @DisplayName("test_post_object_authenticated_request")
    @Tag("Upload")
    // @Tag("post 방식으로 로그인 정보를 포함한 파일 업로드할 경우 성공 확인")
    public void test_post_object_authenticated_request()
    {
        Test.test_post_object_authenticated_request();
    }

    @Test
    @DisplayName("test_post_object_authenticated_no_content_type")
    @Tag("Upload")
    // @Tag("content-type 헤더 정보 없이 post 방식으로 로그인정보를 포함한 파일 업로드시 올바르게 업로드 되는지 확인")
    public void test_post_object_authenticated_no_content_type()
    {
        Test.test_post_object_authenticated_no_content_type();
    }

    @Test
    @DisplayName("test_post_object_authenticated_request_bad_access_key")
    @Tag("ERROR")
    // @Tag("[PostKey 값이 틀린 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_authenticated_request_bad_access_key()
    {
        Test.test_post_object_authenticated_request_bad_access_key();
    }

    @Test
    @DisplayName("test_post_object_set_success_code")
    @Tag("StatusCode")
    // @Tag("[성공시 반환상태값을 201로 설정] post 방식으로 권한없는 사용자가 파일 업로드시 에러체크가 올바른지 확인")
    public void test_post_object_set_success_code()
    {
        Test.test_post_object_set_success_code();
    }

    @Test
    @DisplayName("test_post_object_set_invalid_success_code")
    @Tag("StatusCode")
    // @Tag("[성공시 반환상태값을 에러코드인 404로 설정] post 방식으로 권한없는 사용자가 파일 업로드시 에러체크가 올바른지 확인")
    public void test_post_object_set_invalid_success_code()
    {
        Test.test_post_object_set_invalid_success_code();
    }

    @Test
    @DisplayName("test_post_object_upload_larger_than_chunk")
    @Tag("Upload")
    // @Tag("post 방식으로 로그인정보를 포함한 대용량 파일 업로드시 올바르게 업로드 되는지 확인")
    public void test_post_object_upload_larger_than_chunk()
    {
        Test.test_post_object_upload_larger_than_chunk();
    }

    @Test
    @DisplayName("test_post_object_set_key_from_filename")
    @Tag("Upload")
    // @Tag("[오브젝트 이름을 로그인정보에 포함되어 있는 key값으로 대체할 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 올바르게 업로드 되는지 확인")
    public void test_post_object_set_key_from_filename()
    {
        Test.test_post_object_set_key_from_filename();
    }

    @Test
    @DisplayName("test_post_object_ignored_header")
    @Tag("Upload")
    // @Tag("post 방식으로 로그인, 헤더 정보를 포함한 파일 업로드시 올바르게 업로드 되는지 확인")
    public void test_post_object_ignored_header()
    {
        Test.test_post_object_ignored_header();
    }

    @Test
    @DisplayName("test_post_object_case_insensitive_condition_fields")
    @Tag("Upload")
    // @Tag("[헤더정보에 대소문자를 섞어서 사용할 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 올바르게 업로드 되는지 확인")
    public void test_post_object_case_insensitive_condition_fields()
    {
        Test.test_post_object_case_insensitive_condition_fields();
    }

    @Test
    @DisplayName("test_post_object_escaped_field_values")
    @Tag("Upload")
    // @Tag("[오브젝트 이름에 '\'를 사용할 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 올바르게 업로드 되는지 확인")
    public void test_post_object_escaped_field_values()
    {
        Test.test_post_object_escaped_field_values();
    }

    @Test
    @DisplayName("test_post_object_success_redirect_action")
    @Tag("Upload")
    // @Tag("[redirect url설정하여 체크] post 방식으로 로그인정보를 포함한 파일 업로드시 올바르게 업로드 되는지 확인")
    public void test_post_object_success_redirect_action()
    {
        Test.test_post_object_success_redirect_action();
    }

    @Test
    @DisplayName("test_post_object_invalid_signature")
    @Tag("ERROR")
    // @Tag("[SecretKey Hash 값이 틀린경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_invalid_signature()
    {
        Test.test_post_object_invalid_signature();
    }

    @Test
    @DisplayName("test_post_object_invalid_access_key")
    @Tag("ERROR")
    // @Tag("[PostKey 값이 틀린경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_invalid_access_key()
    {
        Test.test_post_object_invalid_access_key();
    }

    @Test
    @DisplayName("test_post_object_invalid_date_format")
    @Tag("ERROR")
    // @Tag("[로그인 정보의 날짜포맷이 다를경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_invalid_date_format()
    {
        Test.test_post_object_invalid_date_format();
    }

    @Test
    @DisplayName("test_post_object_no_key_specified")
    @Tag("ERROR")
    // @Tag("[오브젝트 이름을 입력하지 않을 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_no_key_specified()
    {
        Test.test_post_object_no_key_specified();
    }

    @Test
    @DisplayName("test_post_object_missing_signature")
    @Tag("ERROR")
    // @Tag("[signature 정보를 누락하고 업로드할 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_missing_signature()
    {
        Test.test_post_object_missing_signature();
    }

    @Test
    @DisplayName("test_post_object_missing_policy_condition")
    @Tag("ERROR")
    // @Tag("[policy에 버킷 이름을 누락하고 업로드할 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_missing_policy_condition()
    {
        Test.test_post_object_missing_policy_condition();
    }

    @Test
    @DisplayName("test_post_object_user_specified_header")
    @Tag("Metadata")
    // @Tag("[사용자가 추가 메타데이터를 입력한 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 올바르게 업로드 되는지 확인")
    public void test_post_object_user_specified_header()
    {
        Test.test_post_object_user_specified_header();
    }

    @Test
    @DisplayName("test_post_object_request_missing_policy_specified_field")
    @Tag("ERROR")
    // @Tag("[사용자가 추가 메타데이터를 policy에 설정하였으나 오브젝트에 해당 정보가 누락된 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_request_missing_policy_specified_field()
    {
        Test.test_post_object_request_missing_policy_specified_field();
    }

    @Test
    @DisplayName("test_post_object_condition_is_case_sensitive")
    @Tag("ERROR")
    // @Tag("[policy의 condition을 대문자(CONDITIONS)로 입력할 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_condition_is_case_sensitive()
    {
        Test.test_post_object_condition_is_case_sensitive();
    }

    @Test
    @DisplayName("test_post_object_expires_is_case_sensitive")
    @Tag("ERROR")
    // @Tag("[policy의 expiration을 대문자(EXPIRATION)로 입력할 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_expires_is_case_sensitive()
    {
        Test.test_post_object_expires_is_case_sensitive();
    }

    @Test
    @DisplayName("test_post_object_expired_policy")
    @Tag("ERROR")
    // @Tag("[policy의 expiration을 만료된 값으로 입력할 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_expired_policy()
    {
        Test.test_post_object_expired_policy();
    }

    @Test
    @DisplayName("test_post_object_invalid_request_field_value")
    @Tag("ERROR")
    // @Tag("[사용자가 추가 메타데이터를 policy에 설정하였으나 설정정보가 올바르지 않을 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_invalid_request_field_value()
    {
        Test.test_post_object_invalid_request_field_value();
    }

    @Test
    @DisplayName("test_post_object_missing_expires_condition")
    @Tag("ERROR")
    // @Tag("[policy의 expiration값을 누락했을 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_missing_expires_condition()
    {
        Test.test_post_object_missing_expires_condition();
    }

    @Test
    @DisplayName("test_post_object_missing_conditions_list")
    @Tag("ERROR")
    // @Tag("[policy의 conditions값을 누락했을 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_missing_conditions_list()
    {
        Test.test_post_object_missing_conditions_list();
    }

    @Test
    @DisplayName("test_post_object_upload_size_limit_exceeded")
    @Tag("ERROR")
    // @Tag("[policy에 설정한 용량보다 큰 오브젝트를 업로드 할 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_upload_size_limit_exceeded()
    {
        Test.test_post_object_upload_size_limit_exceeded();
    }

    @Test
    @DisplayName("test_post_object_missing_content_length_argument")
    @Tag("ERROR")
    // @Tag("[policy에 용량정보 설정을 누락할 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_missing_content_length_argument()
    {
        Test.test_post_object_missing_content_length_argument();
    }

    @Test
    @DisplayName("test_post_object_invalid_content_length_argument")
    @Tag("ERROR")
    // @Tag("[policy에 용량정보 설정값이 틀렸을 경우(용량값을 음수로 입력) post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_invalid_content_length_argument()
    {
        Test.test_post_object_invalid_content_length_argument();
    }

    @Test
    @DisplayName("test_post_object_upload_size_below_minimum")
    @Tag("ERROR")
    // @Tag("[policy에 설정한 용량보다 작은 오브젝트를 업로드 할 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_upload_size_below_minimum()
    {
        Test.test_post_object_upload_size_below_minimum();
    }

    @Test
    @DisplayName("test_post_object_empty_conditions")
    @Tag("ERROR")
    // @Tag("[policy의 conditions값이 비어있을 경우] post 방식으로 로그인정보를 포함한 파일 업로드시 실패하는지 확인")
    public void test_post_object_empty_conditions()
    {
        Test.test_post_object_empty_conditions();
    }

    @Test
    @DisplayName("test_presignedurl_put_get")
    @Tag("PresignedURL")
    // @Tag("PresignedURL로 오브젝트 업로드, 다운로드 성공 확인")
    public void test_presignedurl_put_get()
    {
        Test.test_presignedurl_put_get();
    }

    @Test
    @DisplayName("test_presignedurl_put_get_v4")
    @Tag("PresignedURL")
    // @Tag("[SignatureVersion4]PresignedURL로 오브젝트 업로드, 다운로드 성공 확인")
    public void test_presignedurl_put_get_v4()
    {
        Test.test_presignedurl_put_get_v4();
    }

}
