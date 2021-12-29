package org.example.s3tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class ListObjects {

    org.example.test.ListObjects Test = new org.example.test.ListObjects();

    @AfterEach
	public void Clear() {
		Test.Clear();
	}

	@Test
	@DisplayName("test_bucket_list_many")
	@Tag("Check")
	@Tag("KSAN")
	//@Tag("버킷의 오브젝트 목록을 올바르게 가져오는지 확인")
	public void test_bucket_list_many()
    {
        Test.test_bucket_list_many();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_basic")
	@Tag("Delimiter")
	@Tag("KSAN")
	//@Tag("오브젝트 목록을 가져올때 폴더 구분자[/]로 필터링 되는지 확인")
	public void test_bucket_list_delimiter_basic()
    {
        Test.test_bucket_list_delimiter_basic();
	}

	@Test
	@DisplayName("test_bucket_list_encoding_basic")
	@Tag("Encoding")
	@Tag("KSAN")
	//@Tag("오브젝트 목록을 가져올때 인코딩이 올바르게 동작하는지 확인")
	public void test_bucket_list_encoding_basic()
    {
        Test.test_bucket_list_encoding_basic();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_prefix")
	@Tag("DelimiterandPrefix")
	@Tag("KSAN")
	//@Tag("조건에 맞는 오브젝트 목록을 가져올 수 있는지 확인")
	public void test_bucket_list_delimiter_prefix()
    {
        Test.test_bucket_list_delimiter_prefix();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_prefix_ends_with_delimiter")
	@Tag("DelimiterandPrefix")
	@Tag("KSAN")
	//@Tag("비어있는 폴더의 오브젝트 목록을 가져올 수 있는지 확인")
	public void test_bucket_list_delimiter_prefix_ends_with_delimiter()
    {
        Test.test_bucket_list_delimiter_prefix_ends_with_delimiter();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_alt")
	@Tag("Delimiter")
	@Tag("KSAN")
	//@Tag("오브젝트 목록을 가져올때 문자 구분자[a]로 필터링 되는지 확인")
	public void test_bucket_list_delimiter_alt()
    {
        Test.test_bucket_list_delimiter_alt();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_prefix_underscore")
	@Tag("DelimiterandPrefix")
	@Tag("KSAN")
	//@Tag("[폴더명 앞에 _가 포함되어 있는 환경] 조건에 맞는 오브젝트 목록을 가져올 수 있는지 확인")
	public void test_bucket_list_delimiter_prefix_underscore()
    {
        Test.test_bucket_list_delimiter_prefix_underscore();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_percentage")
	@Tag("Delimiter")
	@Tag("KSAN")
	//@Tag("오브젝트 목록을 가져올때 특수문자 구분자[%]로 필터링 되는지 확인")
	public void test_bucket_list_delimiter_percentage()
    {
        Test.test_bucket_list_delimiter_percentage();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_whitespace")
	@Tag("Delimiter")
	@Tag("KSAN")
	//@Tag("오브젝트 목록을 가져올때 공백문자 구분자[ ]로 필터링 되는지 확인")
	public void test_bucket_list_delimiter_whitespace()
    {
        Test.test_bucket_list_delimiter_whitespace();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_dot")
	@Tag("Delimiter")
	@Tag("KSAN")
	//@Tag("오브젝트 목록을 가져올때 구분자[.]로 필터링 되는지 확인")
	public void test_bucket_list_delimiter_dot()
    {
        Test.test_bucket_list_delimiter_dot();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_unreadable")
	@Tag("Delimiter")
	@Tag("KSAN")
	//@Tag("오브젝트 목록을 가져올때 읽을수 없는 구분자[\n]로 필터링 되는지 확인")
	public void test_bucket_list_delimiter_unreadable()
    {
        Test.test_bucket_list_delimiter_unreadable();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_empty")
	@Tag("Delimiter")
	@Tag("KSAN")
	//@Tag("오브젝트 목록을 가져올때 구분자가 빈문자일때 필터링 되는지 확인")
	public void test_bucket_list_delimiter_empty()
    {
        Test.test_bucket_list_delimiter_empty();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_none")
	@Tag("Delimiter")
	@Tag("KSAN")
	//@Tag("오브젝트 목록을 가져올때 구분자를 입력하지 않아도 문제없는지 확인")
	public void test_bucket_list_delimiter_none()
    {
        Test.test_bucket_list_delimiter_none();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_not_exist")
	@Tag("Delimiter")
	@Tag("KSAN")
	//@Tag("[폴더가 존재하지 않는 환경] 오브젝트 목록을 가져올때 폴더 구분자[/]로 필터링 되는지 확인")
	public void test_bucket_list_delimiter_not_exist()
    {
        Test.test_bucket_list_delimiter_not_exist();
	}

	@Test
	@DisplayName("test_bucket_list_delimiter_not_skip_special")
	@Tag("Delimiter")
	@Tag("KSAN")
	//@Tag("오브젝트 목록을 가져올때 특수문자가 생략되는지 확인")
	public void test_bucket_list_delimiter_not_skip_special()
    {
        Test.test_bucket_list_delimiter_not_skip_special();
	}

	@Test
	@DisplayName("test_bucket_list_prefix_basic")
	@Tag("Prefix")
	@Tag("KSAN")
	//@Tag("[접두어에 '/'가 포함] 오브젝트 목록을 가져올때 선택한 폴더 목록만 가져오는지 확인")
	public void test_bucket_list_prefix_basic()
    {
        Test.test_bucket_list_prefix_basic();
	}

	@Test
	@DisplayName("test_bucket_list_prefix_alt")
	@Tag("Prefix")
	@Tag("KSAN")
	//@Tag("접두어가 [/]가 아닌 경우 구분기호와 접두사 논리를 수행할 수 있는지 확인")
	public void test_bucket_list_prefix_alt()
    {
        Test.test_bucket_list_prefix_alt();
	}

	@Test
	@DisplayName("test_bucket_list_prefix_empty")
	@Tag("Prefix")
	@Tag("KSAN")
	//@Tag("접두어를 빈문자로 입력할 경우 모든 오브젝트 목록을 받아오는지 확인")
	public void test_bucket_list_prefix_empty()
    {
        Test.test_bucket_list_prefix_empty();
	}

	@Test
	@DisplayName("test_bucket_list_prefix_none")
	@Tag("Prefix")
	@Tag("KSAN")
	//@Tag("접두어를 입력하지 않을 경우 모든 오브젝트 목록을 받아오는지 확인")
	public void test_bucket_list_prefix_none()
    {
        Test.test_bucket_list_prefix_none();
	}

	@Test
	@DisplayName("test_bucket_list_prefix_not_exist")
	@Tag("Prefix")
	@Tag("KSAN")
	//@Tag("[접두어와 일치하는 오브젝트가 없는 경우] 접두어를 입력할 경우 빈 오브젝트 목록을 받아오는지 확인")
	public void test_bucket_list_prefix_not_exist()
    {
        Test.test_bucket_list_prefix_not_exist();
	}

	@Test
	@DisplayName("test_bucket_list_prefix_unreadable")
	@Tag("Prefix")
	@Tag("KSAN")
	//@Tag("읽을수 없는 접두어를 입력할 경우 빈 오브젝트 목록을 받아오는지 확인")
	public void test_bucket_list_prefix_unreadable()
    {
        Test.test_bucket_list_prefix_unreadable();
	}

	@Test
	@DisplayName("test_bucket_list_prefix_delimiter_basic")
	@Tag("PrefixAndDelimiter")
	@Tag("KSAN")
	//@Tag("접두어와 구분자를 입력할 경우 오브젝트 목록을 올바르게 받아오는지 확인")
	public void test_bucket_list_prefix_delimiter_basic()
    {
        Test.test_bucket_list_prefix_delimiter_basic();
	}

	@Test
	@DisplayName("test_bucket_list_prefix_delimiter_alt")
	@Tag("PrefixAndDelimiter")
	@Tag("KSAN")
	//@Tag("[구분자가 '/' 아닐 경우] 접두어와 구분자를 입력할 경우 오브젝트 목록을 올바르게 받아오는지 확인")
	public void test_bucket_list_prefix_delimiter_alt()
    {
        Test.test_bucket_list_prefix_delimiter_alt();
	}

	@Test
	@DisplayName("test_bucket_list_prefix_delimiter_prefix_not_exist")
	@Tag("PrefixAndDelimiter")
	@Tag("KSAN")
	//@Tag("[입력한 접두어와 일치하는 오브젝트가 없을 경우] 접두어와 구분자를 입력할 경우 오브젝트 목록이 비어있는지 확인")
	public void test_bucket_list_prefix_delimiter_prefix_not_exist()
    {
        Test.test_bucket_list_prefix_delimiter_prefix_not_exist();
	}

	@Test
	@DisplayName("test_bucket_list_prefix_delimiter_delimiter_not_exist")
	@Tag("PrefixAndDelimiter")
	@Tag("KSAN")
	//@Tag("[구분자가 '/'가 아닐 경우] 접두어와 구분자를 입력할 경우 오브젝트 목록을 올바르게 받아오는지 확인")
	public void test_bucket_list_prefix_delimiter_delimiter_not_exist()
    {
        Test.test_bucket_list_prefix_delimiter_delimiter_not_exist();
	}

	@Test
	@DisplayName("test_bucket_list_prefix_delimiter_prefix_delimiter_not_exist")
	@Tag("PrefixAndDelimiter")
	@Tag("KSAN")
	//@Tag("[구분자가 '/'가 아니며, 접두어와 일치하는 오브젝트가 존재하지 않는 경우] 접두어와 구분자를 입력할 경우 오브젝트 목록이 비어있는지 확인")
	public void test_bucket_list_prefix_delimiter_prefix_delimiter_not_exist()
    {
        Test.test_bucket_list_prefix_delimiter_prefix_delimiter_not_exist();
	}

	@Test
	@DisplayName("test_bucket_list_maxkeys_one")
	@Tag("MaxKeys")
	@Tag("KSAN")
	//@Tag("오브젝트 목록의 최대갯수를 1로 지정하고 불러올때 올바르게 가져오는지 확인")
	public void test_bucket_list_maxkeys_one()
    {
        Test.test_bucket_list_maxkeys_one();
	}

	@Test
	@DisplayName("test_bucket_list_maxkeys_zero")
	@Tag("MaxKeys")
	@Tag("KSAN")
	//@Tag("오브젝트 목록의 최대갯수를 0으로 지정하고 불러올때 목록이 비어있는지 확인")
	public void test_bucket_list_maxkeys_zero()
    {
        Test.test_bucket_list_maxkeys_zero();
	}

	@Test
	@DisplayName("test_bucket_list_maxkeys_none")
	@Tag("MaxKeys")
	@Tag("KSAN")
	//@Tag("[default = 1000] 오브젝트 목록의 최대갯수를 지정하지않고 불러올때 올바르게 가져오는지 확인")
	public void test_bucket_list_maxkeys_none()
    {
        Test.test_bucket_list_maxkeys_none();
	}

	@Test
	@DisplayName("test_bucket_list_maxkeys_invalid")
	@Disabled("JAVA에서는 BeforeRequestEvent 사용이 불가능하여 테스트 하지 못함")
	@Tag("MaxKeys")
	@Tag("KSAN")
	//@Tag("[함수가 호출되기 전에 URL에 유효하지 않은 최대목록갯수를 추가할 경우] 오브젝트 목록 조회 실패 확인")
	public void test_bucket_list_maxkeys_invalid()
    {
        Test.test_bucket_list_maxkeys_invalid();
	}

	@Test
	@DisplayName("test_bucket_list_marker_none")
	@Tag("Marker")
	@Tag("KSAN")
	//@Tag("오브젝트 목록을 가져올때 모든 목록을 가져왓을 경우 마커가 비어있는지 확인")
	public void test_bucket_list_marker_none()
    {
        Test.test_bucket_list_marker_none();
	}

	@Test
	@DisplayName("test_bucket_list_marker_empty")
	@Tag("Marker")
	@Tag("KSAN")
	//@Tag("빈 마커를 입력하고 오브젝트 목록을 불러올때 올바르게 가져오는지 확인")
	public void test_bucket_list_marker_empty()
    {
        Test.test_bucket_list_marker_empty();
	}

	@Test
	@DisplayName("test_bucket_list_marker_unreadable")
	@Tag("Marker")
	//@Tag("마커에 읽을수 없는 값[\n]을 설정한 경우 오브젝트 목록을 올바르게 가져오는지 확인")
	public void test_bucket_list_marker_unreadable()
    {
        Test.test_bucket_list_marker_unreadable();
	}

	@Test
	@DisplayName("test_bucket_list_marker_not_in_list")
	@Tag("Marker")
	@Tag("KSAN")
	//@Tag("[마커와 일치하는 오브젝트가 존재하지 않지만 해당 마커보다 정렬순서가 낮은 오브젝트는 존재하는 환경] 마커를 설정하고 오브젝트 목록을 불러올때 재대로 가져오는지 확인")
	public void test_bucket_list_marker_not_in_list()
    {
        Test.test_bucket_list_marker_not_in_list();
	}

	@Test
	@DisplayName("test_bucket_list_marker_after_list")
	@Tag("Marker")
	@Tag("KSAN")
	//@Tag("[마커와 일치하는 오브젝트도 정렬순서가 같은 오브젝트도 존재하지 않는 환경] 마커를 설정하고 오브젝트 목록을 불러올때 재대로 가져오는지 확인")
	public void test_bucket_list_marker_after_list()
    {
        Test.test_bucket_list_marker_after_list();
	}

	@Test
	@DisplayName("test_bucket_list_return_data")
	@Tag("Metadata")
	@Tag("KSAN")
	//@Tag("ListObjcets으로 가져온 Metadata와  HeadObject, GetObjectAcl로 가져온 Metadata 일치 확인")
	public void test_bucket_list_return_data()
    {
        Test.test_bucket_list_return_data();
	}

	@Test
	@DisplayName("test_bucket_list_objects_anonymous")
	@Tag("ACL")
	@Tag("KSAN")
	//@Tag("권한없는 사용자가 공용읽기설정된 버킷의 오브젝트 목록을 읽을수 있는지 확인")
	public void test_bucket_list_objects_anonymous()
    {
        Test.test_bucket_list_objects_anonymous();
	}

	@Test
	@DisplayName("test_bucket_list_objects_anonymous_fail")
	@Tag("ACL")
	@Tag("KSAN")
	//@Tag("권한없는 사용자가 버킷의 오브젝트 목록을 읽지 못하는지 확인")
	public void test_bucket_list_objects_anonymous_fail()
    {
        Test.test_bucket_list_objects_anonymous_fail();
	}

	@Test
	@DisplayName("test_bucket_notexist")
	@Tag("ERROR")
	@Tag("KSAN")
	//@Tag("존재하지 않는 버킷 내 오브젝트들을 가져오려 했을 경우 실패 확인")
	public void test_bucket_notexist()
    {
        Test.test_bucket_notexist();
	}
}