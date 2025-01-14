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

import java.util.Date;

public class ObjectData {
    public String BucketName;
    public String Key;
    public String DisplayName;
    public String ID;
    public String ETag;
    public Date LastModified;
    public long ContentLength;
    public String VersionId;
 
    public ObjectData()
    {
        Init();
    }

    public void Init()
    {
        BucketName = null;
        Key = null;
        DisplayName = null;
        ID = null;
        ETag = null;
        LastModified = null;
        ContentLength = -1;
        VersionId = null;
    }

    
    public ObjectData withBucketName(String BucketName) {
        setBucketName(BucketName);
        return this;
    }
    public ObjectData withKey(String Key) {
        setKey(Key);
        return this;
    }
    public ObjectData withDisplayName(String DisplayName) {
        setDisplayName(DisplayName);
        return this;
    }
    public ObjectData withID(String ID) {
        setID(ID);
        return this;
    }
    public ObjectData withETag(String ETag) {
        setETag(ETag);
        return this;
    }
    public ObjectData withLastModified(Date LastModified) {
        setLastModified(LastModified);
        return this;
    }
    public ObjectData withContentLength(long ContentLength) {
        setContentLength(ContentLength);
        return this;
    }
    public ObjectData withVersionId(String VersionId) {
        setVersionId(VersionId);
        return this;
    }
    

    public void setBucketName(String BucketName) { this.BucketName = BucketName; }
    public String getBucketName() { return BucketName; }    
    public void setKey(String Key) { this.Key = Key; }
    public String getKey() { return Key; }
    public void setDisplayName(String DisplayName) { this.DisplayName = DisplayName; }
    public String getDisplayName() { return DisplayName; }
    public void setID(String ID) { this.ID = ID; }
    public String getID() { return ID; }
    public void setETag(String ETag) { this.ETag = ETag; }
    public String getETag() { return ETag; }
    public void setLastModified(Date LastModified) { this.LastModified = LastModified; }
    public Date getLastModified() { return LastModified; }
    public void setContentLength(long ContentLength) { this.ContentLength = ContentLength; }
    public long getContentLength() { return ContentLength; }
    public void setVersionId(String VersionId) { this.VersionId = VersionId; }
    public String getVersionId() { return VersionId; }
}