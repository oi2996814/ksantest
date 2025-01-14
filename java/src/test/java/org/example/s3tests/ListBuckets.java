/*
* Copyright (c) 2021 PSPACE, inc. KSAN Development Team ksan@pspace.co.kr
* KSAN is a suite of free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version 
* 3 of the License.  See LICENSE for details
*
* 본 프로그램 및 관련 소스코드, 문서 등 모든 자료는 있는 그대로 제공이 됩니다.
* KSAN 프로젝트의 개발자 및 개발사는 이 프로그램을 사용한 결과에 따른 어떠한 책임도 지지 않습니다.
* KSAN 개발팀은 사전 공지, 허락, 동의 없이 KSAN 개발에 관련된 모든 결과물에 대한 LICENSE 방식을 변경 할 권리가 있습니다.
*/
package org.example.s3tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.model.HeadBucketRequest;

public class ListBuckets extends TestBase
{
	@Test
	@DisplayName("test_buckets_create_then_list")
    @Tag("Get")
    @Tag("KSAN")
    //@Tag("여러개의 버킷 생성해서 목록 조회 확인")
    public void test_buckets_create_then_list() throws Exception
    {
        var Client = GetClient();
        var BucketNames = new ArrayList<String>();
        for (int i = 0; i < 5; i++)
        {
            var BucketName = GetNewBucketName();
            Client.createBucket(BucketName);
            BucketNames.add(BucketName);
        }

        var Response = Client.listBuckets();
        var BucketList = GetBucketList(Response);

        for (var BucketName : BucketNames)
        {
            if(!BucketList.contains(BucketName))
                throw new Exception(String.format("S3 implementation's GET on Service did not return bucket we created: {0}", BucketName));
        }
    }

    @Test
	@DisplayName("test_list_buckets_invalid_auth")
    @Tag("ERROR")
    @Tag("KSAN")
    //@Tag("존재하지 않는 사용자가 버킷목록 조회시 에러 확인")
    public void test_list_buckets_invalid_auth()
    {
        var BadAuthClient = GetBadAuthClient(null, null);

        var e = assertThrows(AmazonServiceException.class, () -> BadAuthClient.listBuckets());
        
        var StatusCode = e.getStatusCode();
        var ErrorCode = e.getErrorCode();
        assertEquals(403, StatusCode);
        assertEquals(MainData.InvalidAccessKeyId, ErrorCode);
    }

    @Test
	@DisplayName("test_list_buckets_bad_auth")
    @Tag("ERROR")
    @Tag("KSAN")
    //@Tag("로그인정보를 잘못입력한 사용자가 버킷목록 조회시 에러 확인")
    public void test_list_buckets_bad_auth()
    {
        var MainAccessKey = Config.MainUser.AccessKey;
        var BadAuthClient = GetBadAuthClient(MainAccessKey, null);

        var e = assertThrows(AmazonServiceException.class, () -> BadAuthClient.listBuckets());
        var StatusCode = e.getStatusCode();
        var ErrorCode = e.getErrorCode();
        assertEquals(403, StatusCode);
        assertEquals(MainData.SignatureDoesNotMatch, ErrorCode);
    }
    
    @Test
    @DisplayName("test_head_bucket")
    @Tag("Metadata")
    @Tag("KSAN")
    //Tag("버킷의 메타데이터를 가져올 수 있는지 확인")
    public void test_head_bucket()
    {
    	var BucketName = GetNewBucket();
    	var Client = GetClient();
    	
    	var Response = Client.headBucket(new HeadBucketRequest(BucketName));
    	assertNotNull(Response);
    }
}
