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
using Amazon.S3;
using Amazon.S3.Model;
using System.Collections.Generic;
using System.Net;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace s3tests2
{
    [TestClass]
    public class Grants : TestBase
    {
        [TestMethod("test_bucket_acl_default")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Bucket")]
        [TestProperty(MainData.Explanation, "[bucket_acl : default] " +
                                     "권한을 설정하지 않고 생성한 버킷의 default acl정보가 올바른지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_default()
        {
            var BucketName = GetNewBucket();

            var Client = GetClient();
            var Response = Client.GetBucketACL(BucketName);

            var DisplayName = Config.MainUser.DisplayName;
            var UserId = Config.MainUser.UserId;

            if (!Config.S3.IsAWS) Assert.AreEqual(DisplayName, Response.AccessControlList.Owner.DisplayName);
            Assert.AreEqual(UserId, Response.AccessControlList.Owner.Id);

            var GetGrants = Response.AccessControlList.Grants;
            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                }
            },
            GetGrants);
        }

        [TestMethod("test_bucket_acl_canned_during_create")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Bucket")]
        [TestProperty(MainData.Explanation, "[bucket_acl : public-read] " +
                                     "권한을 public-read로 생성한 버킷의 acl정보가 올바른지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_canned_during_create()
        {
            var BucketName = GetNewBucketName();

            var Client = GetClient();
            Client.PutBucket(BucketName, ACL: S3CannedACL.PublicRead);
            var Response = Client.GetBucketACL(BucketName);

            var DisplayName = Config.MainUser.DisplayName;
            var UserId = Config.MainUser.UserId;

            var GetGrants = Response.AccessControlList.Grants;
            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = null,
                        DisplayName = null,
                        URI = "http://acs.amazonaws.com/groups/global/AllUsers",
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);
        }

        [TestMethod("test_bucket_acl_canned")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Bucket")]
        [TestProperty(MainData.Explanation, "[bucket_acl : public-read => bucket_acl : private] " +
                                     "권한을 public-read로 생성한 버킷을 private로 변경할경우 올바르게 적용되는지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_canned()
        {
            var BucketName = GetNewBucketName();

            var Client = GetClient();
            Client.PutBucket(BucketName, ACL: S3CannedACL.PublicRead);
            var Response = Client.GetBucketACL(BucketName);

            var DisplayName = Config.MainUser.DisplayName;
            var UserId = Config.MainUser.UserId;

            var GetGrants = Response.AccessControlList.Grants;
            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = null,
                        DisplayName = null,
                        URI = "http://acs.amazonaws.com/groups/global/AllUsers",
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);

            Client.PutBucketACL(BucketName, ACL: S3CannedACL.Private);
            Response = Client.GetBucketACL(BucketName);
            GetGrants = Response.AccessControlList.Grants;

            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                }
            },
            GetGrants);
        }

        [TestMethod("test_bucket_acl_canned_publicreadwrite")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Bucket")]
        [TestProperty(MainData.Explanation, "[bucket_acl : public-read-write] " +
                                     "권한을 public-read-write로 생성한 버킷의 acl정보가 올바른지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_canned_publicreadwrite()
        {
            var BucketName = GetNewBucketName();

            var Client = GetClient();
            Client.PutBucket(BucketName, ACL: S3CannedACL.PublicReadWrite);
            var Response = Client.GetBucketACL(BucketName);

            var DisplayName = Config.MainUser.DisplayName;
            var UserId = Config.MainUser.UserId;

            var GetGrants = Response.AccessControlList.Grants;
            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = null,
                        DisplayName = null,
                        URI = "http://acs.amazonaws.com/groups/global/AllUsers",
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.WRITE,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = null,
                        DisplayName = null,
                        URI = "http://acs.amazonaws.com/groups/global/AllUsers",
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);
        }

        [TestMethod("test_bucket_acl_canned_authenticatedread")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Bucket")]
        [TestProperty(MainData.Explanation, "[bucket_acl : authenticated-read] " +
                                     "권한을 authenticated-read로 생성한 버킷의 acl정보가 올바른지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_canned_authenticatedread()
        {
            var BucketName = GetNewBucketName();

            var Client = GetClient();
            Client.PutBucket(BucketName, ACL: S3CannedACL.AuthenticatedRead);
            var Response = Client.GetBucketACL(BucketName);

            var DisplayName = Config.MainUser.DisplayName;
            var UserId = Config.MainUser.UserId;

            var GetGrants = Response.AccessControlList.Grants;
            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = null,
                        DisplayName = null,
                        URI = "http://acs.amazonaws.com/groups/global/AuthenticatedUsers",
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);
        }

        [TestMethod("test_object_acl_default")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Object")]
        [TestProperty(MainData.Explanation, "[object_acl : default] " +
                                     "권한을 설정하지 않고 생성한 오브젝트의 default acl정보가 올바른지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_default()
        {
            var BucketName = GetNewBucket();
            var Client = GetClient();

            var Key = "foo";
            Client.PutObject(BucketName, Key, Body: "bar");
            var Response = Client.GetObjectACL(BucketName, Key);


            var DisplayName = Config.MainUser.DisplayName;
            var UserId = Config.MainUser.UserId;

            var GetGrants = Response.AccessControlList.Grants;
            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                }
            },
            GetGrants);
        }

        [TestMethod("test_object_acl_canned_during_create")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Object")]
        [TestProperty(MainData.Explanation, "[object_acl : public-read] " +
                                     "권한을 public-read로 생성한 오브젝트의 acl정보가 올바른지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_canned_during_create()
        {
            var BucketName = GetNewBucket();
            var Client = GetClient();

            var Key = "foo";
            Client.PutObject(BucketName, Key, Body: "bar", ACL: S3CannedACL.PublicRead);
            var Response = Client.GetObjectACL(BucketName, Key);


            var DisplayName = Config.MainUser.DisplayName;
            var UserId = Config.MainUser.UserId;

            var GetGrants = Response.AccessControlList.Grants;
            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = null,
                        DisplayName = null,
                        URI = "http://acs.amazonaws.com/groups/global/AllUsers",
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);
        }

        [TestMethod("test_object_acl_canned")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Object")]
        [TestProperty(MainData.Explanation, "[object_acl : public-read => object_acl : private] " +
                                     "권한을 public-read로 생성한 오브젝트를 private로 변경할경우 올바르게 적용되는지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_canned()
        {
            var BucketName = GetNewBucket();
            var Client = GetClient();

            var Key = "foo";
            Client.PutObject(BucketName, Key, Body: "bar", ACL: S3CannedACL.PublicRead);
            var Response = Client.GetObjectACL(BucketName, Key);


            var DisplayName = Config.MainUser.DisplayName;
            var UserId = Config.MainUser.UserId;

            var GetGrants = Response.AccessControlList.Grants;
            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = null,
                        DisplayName = null,
                        URI = "http://acs.amazonaws.com/groups/global/AllUsers",
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);

            Client.PutObjectACL(BucketName, Key, ACL: S3CannedACL.Private);
            Response = Client.GetObjectACL(BucketName, Key);

            GetGrants = Response.AccessControlList.Grants;
            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);
        }


        [TestMethod("test_object_acl_canned_publicreadwrite")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Object")]
        [TestProperty(MainData.Explanation, "[object_acl : public-read-write] " +
                                     "권한을 public-read-write로 생성한 오브젝트의 acl정보가 올바른지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_canned_publicreadwrite()
        {
            var BucketName = GetNewBucket();
            var Client = GetClient();

            var Key = "foo";
            Client.PutObject(BucketName, Key, Body: "bar", ACL: S3CannedACL.PublicReadWrite);
            var Response = Client.GetObjectACL(BucketName, Key);


            var DisplayName = Config.MainUser.DisplayName;
            var UserId = Config.MainUser.UserId;

            var GetGrants = Response.AccessControlList.Grants;

            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = null,
                        DisplayName = null,
                        URI = "http://acs.amazonaws.com/groups/global/AllUsers",
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.WRITE,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = null,
                        DisplayName = null,
                        URI = "http://acs.amazonaws.com/groups/global/AllUsers",
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);
        }

        [TestMethod("test_object_acl_canned_authenticatedread")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Object")]
        [TestProperty(MainData.Explanation, "[object_acl : public-read-write] " +
                                     "권한을 public-read-write로 생성한 오브젝트의 acl정보가 올바른지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_canned_authenticatedread()
        {
            var BucketName = GetNewBucket();
            var Client = GetClient();

            var Key = "foo";
            Client.PutObject(BucketName, Key, Body: "bar", ACL: S3CannedACL.AuthenticatedRead);
            var Response = Client.GetObjectACL(BucketName, Key);


            var DisplayName = Config.MainUser.DisplayName;
            var UserId = Config.MainUser.UserId;

            var GetGrants = Response.AccessControlList.Grants;

            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = UserId,
                        DisplayName = DisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = null,
                        DisplayName = null,
                        URI = "http://acs.amazonaws.com/groups/global/AuthenticatedUsers",
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);
        }


        [TestMethod("test_object_acl_canned_bucketownerread")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Object")]
        [TestProperty(MainData.Explanation, "[bucket_acl: public-read-write]" +
                                     "[object_acl: public-read-write => object_acl : bucket-owner-read]" +
                                     "메인 유저가 권한을 public-read-write로 생성한 버켓에서 서브유저가 업로드한 오브젝트를" +
                                     "서브 유저가 권한을 bucket-owner-read로 변경하였을때 올바르게 적용되는지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_canned_bucketownerread()
        {
            var BucketName = GetNewBucketName();
            var MainClient = GetClient();
            var AltClient = GetAltClient();
            var Key = "foo";

            MainClient.PutBucket(BucketName, ACL: S3CannedACL.PublicReadWrite);
            AltClient.PutObject(BucketName, Key, Body: "bar");

            var BucketACLResponse = MainClient.GetBucketACL(BucketName);
            var BucketOwnerId = BucketACLResponse.AccessControlList.Owner.Id;
            var BucketOwnerDisplayName = BucketACLResponse.AccessControlList.Owner.DisplayName;

            AltClient.PutObject(BucketName, Key, ACL: S3CannedACL.BucketOwnerRead);
            var Response = AltClient.GetObjectACL(BucketName, Key);

            var AltDisplayName = Config.AltUser.DisplayName;
            var AltUserId = Config.AltUser.UserId;

            var GetGrants = Response.AccessControlList.Grants;

            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = BucketOwnerId,
                        DisplayName = BucketOwnerDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);
        }

        [TestMethod("test_object_acl_canned_bucketownerfullcontrol")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Object")]
        [TestProperty(MainData.Explanation, "[bucket_acl: public-read-write]" +
                                     "[object_acl: public-read-write => object_acl : bucket-owner-full-control]" +
                                     "메인 유저가 권한을 public-read-write로 생성한 버켓에서 서브유저가 업로드한 오브젝트를" +
                                     "서브 유저가 권한을 bucket-owner-full-control로 변경하였을때 올바르게 적용되는지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_canned_bucketownerfullcontrol()
        {
            var BucketName = GetNewBucketName();
            var MainClient = GetClient();
            var AltClient = GetAltClient();
            var Key = "foo";

            MainClient.PutBucket(BucketName, ACL: S3CannedACL.PublicReadWrite);
            AltClient.PutObject(BucketName, Key, Body: "bar");

            var BucketACLResponse = MainClient.GetBucketACL(BucketName);
            var BucketOwnerId = BucketACLResponse.AccessControlList.Owner.Id;
            var BucketOwnerDisplayName = BucketACLResponse.AccessControlList.Owner.DisplayName;

            AltClient.PutObject(BucketName, Key, ACL: S3CannedACL.BucketOwnerFullControl);
            var Response = AltClient.GetObjectACL(BucketName, Key);

            var AltDisplayName = Config.AltUser.DisplayName;
            var AltUserId = Config.AltUser.UserId;

            var GetGrants = Response.AccessControlList.Grants;

            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = BucketOwnerId,
                        DisplayName = BucketOwnerDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);
        }

        [TestMethod("test_object_acl_full_control_verify_owner")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Object")]
        [TestProperty(MainData.Explanation, "[bucket_acl: public-read-write]" +
            "메인 유저가 권한을 public-read-write로 생성한 버켓에서 메인유저가 생성한 오브젝트를 권한을" +
            "서브유저에게 FULL_CONTROL, 소유주를 메인유저로 설정한뒤 서브 유저가 권한을 READ_ACP, 소유주를 메인유저로 설정하였을때" +
            "오브젝트의 소유자가 유지되는지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_full_control_verify_owner()
        {
            var BucketName = GetNewBucketName();
            var MainClient = GetClient();
            var AltClient = GetAltClient();
            var Key = "foo";

            MainClient.PutBucket(BucketName, ACL: S3CannedACL.PublicReadWrite);
            MainClient.PutObject(BucketName, Key, Body: "bar");

            var MainUserId = Config.MainUser.UserId;
            var MainDisplayName = Config.MainUser.DisplayName;

            var AltUserId = Config.AltUser.UserId;
            var AltDisplayName = Config.AltUser.DisplayName;

            var Grant = new S3AccessControlList()
            {
                Owner = new Owner() { DisplayName = MainDisplayName, Id = MainUserId },
                Grants = new List<S3Grant>()
                {
                    new S3Grant()
                    {
                        Permission = S3Permission.FULL_CONTROL,
                        Grantee = new S3Grantee()
                        {
                            CanonicalUser = AltUserId,
                            DisplayName = AltDisplayName,
                            URI = null,
                            EmailAddress = null,
                        }
                    },
                }
            };

            MainClient.PutObjectACL(BucketName, Key, AccessControlPolicy: Grant);

            Grant = new S3AccessControlList()
            {
                Owner = new Owner() { DisplayName = MainDisplayName, Id = MainUserId },
                Grants = new List<S3Grant>()
                {
                    new S3Grant()
                    {
                        Permission = S3Permission.READ_ACP,
                        Grantee = new S3Grantee()
                        {
                            CanonicalUser = AltUserId,
                            DisplayName = AltDisplayName,
                            URI = null,
                            EmailAddress = null,
                        }
                    },
                }
            };

            AltClient.PutObjectACL(BucketName, Key, AccessControlPolicy: Grant);

            var Response = AltClient.GetObjectACL(BucketName, Key);
            Assert.AreEqual(MainUserId, Response.AccessControlList.Owner.Id);
        }


        [TestMethod("test_object_acl_full_control_verify_attributes")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "ETag")]
        [TestProperty(MainData.Explanation, "[bucket_acl: public-read-write]" +
                                     "권한정보를 추가한 오브젝트의 eTag값이 변경되지 않는지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_full_control_verify_attributes()
        {
            var BucketName = GetNewBucketName();
            var MainClient = GetClient();
            var Key = "foo";

            MainClient.PutBucket(BucketName, ACL: S3CannedACL.PublicReadWrite);

            var Headers = new List<KeyValuePair<string, string>>() { new KeyValuePair<string, string>("x-amz-meta-foo", "bar") };
            MainClient.PutObject(BucketName, Key, Body: "bar", HeaderList: Headers);

            var Response = MainClient.GetObject(BucketName, Key);
            var ContentType = Response.Headers.ContentType;
            var ETag = Response.ETag;

            var AltUserId = Config.AltUser.UserId;

            var Grant = new S3Grant()
            {
                Permission = S3Permission.FULL_CONTROL,
                Grantee = new S3Grantee()
                {
                    CanonicalUser = AltUserId,
                    //DisplayName = AltDisplayName,
                    //URI = null,
                    //EmailAddress = null,
                }
            };

            var Grants = AddObjectUserGrant(BucketName, Key, Grant);

            MainClient.PutObjectACL(BucketName, Key, AccessControlPolicy: Grants);

            Response = MainClient.GetObject(BucketName, Key);
            Assert.AreEqual(ContentType, Response.Headers.ContentType);
            Assert.AreEqual(ETag, Response.ETag);
        }

        [TestMethod("test_bucket_acl_canned_private_to_private")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Permission")]
        [TestProperty(MainData.Explanation, "[bucket_acl:private] " +
                                     "기본생성한 버킷에 priavte 설정이 가능한지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_canned_private_to_private()
        {
            var BucketName = GetNewBucket();
            var Client = GetClient();

            var Response = Client.PutBucketACL(BucketName, ACL: S3CannedACL.Private);
            Assert.AreEqual(HttpStatusCode.OK, Response.HttpStatusCode);
        }


        [TestMethod("test_object_acl")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Permission")]
        [TestProperty(MainData.Explanation, "오브젝트에 설정한 acl정보가 올바르게 적용되었는지 확인 : FULL_CONTROL")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl()
        {
            CheckObjectACL(S3Permission.FULL_CONTROL);
        }

        [TestMethod("test_object_acl_write")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Permission")]
        [TestProperty(MainData.Explanation, "오브젝트에 설정한 acl정보가 올바르게 적용되었는지 확인 : WRITE")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_write()
        {
            CheckObjectACL(S3Permission.WRITE);
        }

        [TestMethod("test_object_acl_writeacp")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Permission")]
        [TestProperty(MainData.Explanation, "오브젝트에 설정한 acl정보가 올바르게 적용되었는지 확인 : WRITE_ACP")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_writeacp()
        {
            CheckObjectACL(S3Permission.WRITE_ACP);
        }

        [TestMethod("test_object_acl_read")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Permission")]
        [TestProperty(MainData.Explanation, "오브젝트에 설정한 acl정보가 올바르게 적용되었는지 확인 : READ")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_read()
        {
            CheckObjectACL(S3Permission.READ);
        }

        [TestMethod("test_object_acl_readacp")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Permission")]
        [TestProperty(MainData.Explanation, "오브젝트에 설정한 acl정보가 올바르게 적용되었는지 확인 : READ_ACP")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_acl_readacp()
        {
            CheckObjectACL(S3Permission.READ_ACP);
        }

        [TestMethod("test_bucket_acl_grant_userid_fullcontrol")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Permission")]
        [TestProperty(MainData.Explanation, "메인 유저가 버킷에 설정한 acl정보대로 서브유저가 해당 버킷에 접근 가능한지 확인 : FULL_CONTROL")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_grant_userid_fullcontrol()
        {
            var BucketName = BucketACLGrantUserid(S3Permission.FULL_CONTROL);

            CheckBucketACLGrantCanRead(BucketName);
            CheckBucketACLGrantCanReadACP(BucketName);
            CheckBucketACLGrantCanWrite(BucketName);
            CheckBucketACLGrantCanWriteACP(BucketName);

            var Client = GetClient();

            var BucketACLResponse = Client.GetBucketACL(BucketName);
            var OwnerId = BucketACLResponse.AccessControlList.Owner.Id;
            var OwnerDisplayName = BucketACLResponse.AccessControlList.Owner.DisplayName;

            var MainUserId = Config.MainUser.UserId;
            var MainDisplayName = Config.MainUser.DisplayName;

            Assert.AreEqual(MainUserId, OwnerId);
            if (!Config.S3.IsAWS) Assert.AreEqual(MainDisplayName, OwnerDisplayName);
        }

        [TestMethod("test_bucket_acl_grant_userid_read")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Permission")]
        [TestProperty(MainData.Explanation, "메인 유저가 버킷에 설정한 acl정보대로 서브유저가 해당 버킷에 접근 가능한지 확인 : READ")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_grant_userid_read()
        {
            var BucketName = BucketACLGrantUserid(S3Permission.READ);

            CheckBucketACLGrantCanRead(BucketName);
            CheckBucketACLGrantCantReadACP(BucketName);
            CheckBucketACLGrantCantWrite(BucketName);
            CheckBucketACLGrantCantWriteACP(BucketName);
        }

        [TestMethod("test_bucket_acl_grant_userid_readacp")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Permission")]
        [TestProperty(MainData.Explanation, "메인 유저가 버킷에 설정한 acl정보대로 서브유저가 해당 버킷에 접근 가능한지 확인 : READ_ACP")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_grant_userid_readacp()
        {
            var BucketName = BucketACLGrantUserid(S3Permission.READ_ACP);

            CheckBucketACLGrantCantRead(BucketName);
            CheckBucketACLGrantCanReadACP(BucketName);
            CheckBucketACLGrantCantWrite(BucketName);
            CheckBucketACLGrantCantWriteACP(BucketName);
        }

        [TestMethod("test_bucket_acl_grant_userid_write")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Permission")]
        [TestProperty(MainData.Explanation, "메인 유저가 버킷에 설정한 acl정보대로 서브유저가 해당 버킷에 접근 가능한지 확인 : WRITE")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_grant_userid_write()
        {
            var BucketName = BucketACLGrantUserid(S3Permission.WRITE);

            CheckBucketACLGrantCantRead(BucketName);
            CheckBucketACLGrantCantReadACP(BucketName);
            CheckBucketACLGrantCanWrite(BucketName);
            CheckBucketACLGrantCantWriteACP(BucketName);
        }

        [TestMethod("test_bucket_acl_grant_userid_writeacp")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Permission")]
        [TestProperty(MainData.Explanation, "메인 유저가 버킷에 설정한 acl정보대로 서브유저가 해당 버킷에 접근 가능한지 확인 : WRITE_ACP")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_grant_userid_writeacp()
        {
            var BucketName = BucketACLGrantUserid(S3Permission.WRITE_ACP);

            CheckBucketACLGrantCantRead(BucketName);
            CheckBucketACLGrantCantReadACP(BucketName);
            CheckBucketACLGrantCantWrite(BucketName);
            CheckBucketACLGrantCanWriteACP(BucketName);
        }

        [TestMethod("test_bucket_acl_grant_nonexist_user")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "ERROR")]
        [TestProperty(MainData.Explanation, "버킷에 존재하지 않는 유저를 추가하려고 하면 에러 발생 확인")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_bucket_acl_grant_nonexist_user()
        {
            var BucketName = GetNewBucket();
            var Client = GetClient();

            var BadUserId = "_foo";

            var Grant = new S3Grant() { Permission = S3Permission.FULL_CONTROL, Grantee = new S3Grantee() { CanonicalUser = BadUserId } };

            var Grants = AddBucketUserGrant(BucketName, Grant);

            var e = Assert.ThrowsException<AmazonS3Exception>(() => Client.PutBucketACL(BucketName, AccessControlPolicy: Grants));
            var StatusCode = e.StatusCode;
            var ErrorCode = e.ErrorCode;
            Assert.AreEqual(HttpStatusCode.BadRequest, StatusCode);
            Assert.AreEqual(MainData.InvalidArgument, ErrorCode);
        }

        [TestMethod("test_bucket_acl_no_grants")]
        [Ignore("c#에서는 Grants가 비어있는 정보로 전송 할 수 없음")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "ERROR")]
        [TestProperty(MainData.Explanation, "버킷에 권한정보를 모두 제거했을때 오브젝트를 업데이트 하면 실패 확인")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_bucket_acl_no_grants()
        {
            var BucketName = GetNewBucket();
            var Client = GetClient();
            var Key = "foo";

            Client.PutObject(BucketName, Key, Body: "bar");
            var Response = Client.GetBucketACL(BucketName);
            var OldGrants = Response.AccessControlList.Grants;
            var Policy = new S3AccessControlList()
            {
                Owner = Response.AccessControlList.Owner,
                Grants = new List<S3Grant>()
            };

            Client.PutBucketACL(BucketName, AccessControlPolicy: Policy);

            Client.GetObject(BucketName, Key);

            Assert.ThrowsException<AmazonS3Exception>(() => Client.PutObject(BucketName, Key, Body: "A"));

            var Client2 = GetClient();
            Client2.GetBucketACL(BucketName);
            Client2.PutBucketACL(BucketName, ACL: S3CannedACL.Private);

            Policy.Grants = OldGrants;
            Client2.PutBucketACL(BucketName, AccessControlPolicy: Policy);

        }

        [TestMethod("test_object_header_acl_grants")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Header")]
        [TestProperty(MainData.Explanation, "오브젝트를 생성하면서 권한정보를 여러개보낼때 모두 올바르게 적용되었는지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_object_header_acl_grants()
        {
            var BucketName = GetNewBucket();
            var Client = GetClient();
            var Key = "foo_key";

            var AltUserId = Config.AltUser.UserId;
            var AltDisplayName = Config.AltUser.DisplayName;

            var Grants = GetGrantList();

            Client.PutObject(BucketName, Key, Body: "bar", Grants: Grants);
            var Response = Client.GetObjectACL(BucketName, Key);

            var GetGrants = Response.AccessControlList.Grants;
            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ_ACP,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.WRITE,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.WRITE_ACP,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);
        }

        [TestMethod("test_bucket_header_acl_grants")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Header")]
        [TestProperty(MainData.Explanation, "버킷 생성하면서 권한정보를 여러개 보낼때 모두 올바르게 적용되었는지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_header_acl_grants()
        {
            var BucketName = GetNewBucketName();
            var Client = GetClient();

            var AltUserId = Config.AltUser.UserId;
            var AltDisplayName = Config.AltUser.DisplayName;

            var Headers = GetACLHeader();

            Client.PutBucket(BucketName, HeaderList: Headers);
            var Response = Client.GetBucketACL(BucketName);

            var GetGrants = Response.AccessControlList.Grants;
            CheckGrants(new List<S3Grant>()
            {
                new S3Grant()
                {
                    Permission = S3Permission.FULL_CONTROL,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.READ_ACP,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.WRITE,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
                new S3Grant()
                {
                    Permission = S3Permission.WRITE_ACP,
                    Grantee = new S3Grantee()
                    {
                        CanonicalUser = AltUserId,
                        DisplayName = AltDisplayName,
                        URI = null,
                        EmailAddress = null,
                    }
                },
            },
            GetGrants);
        }

        [TestMethod("test_bucket_acl_revoke_all")]
        [Ignore("c#에서는 Grants가 비어있는 정보로 전송 할 수 없음")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Delete")]
        [TestProperty(MainData.Explanation, "버킷의 소유자정보를 포함한 모든 acl정보를 삭제할 경우 올바르게 적용되는지 확인")]
        [TestProperty(MainData.Result, MainData.ResultSuccess)]
        public void test_bucket_acl_revoke_all()
        {
            var BucketName = GetNewBucket();
            var Client = GetClient();

            Client.PutObject(BucketName, Key: "foo", Body: "bar");
            var Response = Client.GetBucketACL(BucketName);
            var OldGrants = Response.AccessControlList.Grants;

            var Policy = new S3AccessControlList() { Owner = Response.AccessControlList.Owner, Grants = new List<S3Grant>() };

            Client.PutBucketACL(BucketName, AccessControlPolicy: Policy);
            Response = Client.GetBucketACL(BucketName);

            Assert.AreEqual(0, Response.AccessControlList.Grants.Count);

            Policy.Grants = OldGrants;
            Client.PutBucketACL(BucketName, AccessControlPolicy: Policy);
        }

        [TestMethod("test_access_bucket_private_object_private")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:private, object_acl:private]" +
            "메인유저가 pirvate권한으로 생성한 버킷과 오브젝트를 서브유저가 오브젝트 목록을 보거나 다운로드 할 수 없음을 확인")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_private_object_private()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.Private, S3CannedACL.Private);

            var AltClient = GetAltClient();

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.GetObject(BucketName, Key1));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.GetObject(BucketName, Key2));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.ListObjects(BucketName));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.PutObject(BucketName, Key1, Body: "barcontent"));

            var AltClient2 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.PutObject(BucketName, Key2, Body: "baroverwrite"));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.PutObject(BucketName, NewKey, Body: "newcontent"));
        }

        [TestMethod("test_access_bucket_private_objectv2_private")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:private, object_acl:private]" +
            "메인유저가 pirvate권한으로 생성한 버킷과 오브젝트를 서브유저가 오브젝트 목록을 보거나 다운로드 할 수 없음을 확인(ListObjects_v2)")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_private_objectv2_private()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.Private, S3CannedACL.Private);

            var AltClient = GetAltClient();

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.GetObject(BucketName, Key1));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.GetObject(BucketName, Key2));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.ListObjectsV2(BucketName));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.PutObject(BucketName, Key1, Body: "barcontent"));

            var AltClient2 = GetAltClient();

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.PutObject(BucketName, Key2, Body: "baroverwrite"));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.PutObject(BucketName, NewKey, Body: "newcontent"));
        }

        [TestMethod("test_access_bucket_private_object_publicread")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:private, object_acl:private, public-read] " +
                                     "메인유저가 pirvate권한으로 생성한 버킷과 오브젝트는 서브유저가 목록을 보거나 다운로드할 수 없지만 " +
                                     "public-read로 설정한 오브젝트는 다운로드 할 수 있음을 확인")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_private_object_publicread()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.Private, S3CannedACL.PublicRead);
            var AltClient = GetAltClient();
            var Response = AltClient.GetObject(BucketName, Key1);

            var Body = GetBody(Response);
            Assert.AreEqual("foocontent", Body);

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.PutObject(BucketName, Key1, Body: "foooverwrite"));

            var AltClient2 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.GetObject(BucketName, Key2));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.PutObject(BucketName, Key2, Body: "baroverwrite"));

            var AltClient3 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient3.ListObjects(BucketName));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient3.PutObject(BucketName, NewKey, Body: "newcontent"));
        }

        [TestMethod("test_access_bucket_private_objectv2_publicread")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:private, object_acl:private, public-read] " +
                                     "메인유저가 pirvate권한으로 생성한 버킷과 오브젝트는 서브유저가 목록을 보거나 다운로드할 수 없지만 " +
                                     "public-read로 설정한 오브젝트는 다운로드 할 수 있음을 확인(ListObjectsV2)")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_private_objectv2_publicread()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.Private, S3CannedACL.PublicRead);
            var AltClient = GetAltClient();
            var Response = AltClient.GetObject(BucketName, Key1);

            var Body = GetBody(Response);
            Assert.AreEqual("foocontent", Body);

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.PutObject(BucketName, Key1, Body: "foooverwrite"));

            var AltClient2 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.GetObject(BucketName, Key2));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.PutObject(BucketName, Key2, Body: "baroverwrite"));

            var AltClient3 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient3.ListObjectsV2(BucketName));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient3.PutObject(BucketName, NewKey, Body: "newcontent"));
        }

        [TestMethod("test_access_bucket_private_object_publicreadwrite")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:private, object_acl:private, public-read-write]" +
            "메인유저가 pirvate권한으로 생성한 버킷과 오브젝트는 서브유저가 목록을 보거나 다운로드할 수 없지만" +
            "public-read-write로 설정한 오브젝트는 다운로드만 할 수 있음을 확인 " +
            "(버킷의 권한이 private이기 때문에 오브젝트의 권한이 public-read-write로 설정되어있어도 업로드불가)")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_private_object_publicreadwrite()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.Private, S3CannedACL.PublicReadWrite);
            var AltClient = GetAltClient();
            var Response = AltClient.GetObject(BucketName, Key1);

            var Body = GetBody(Response);
            Assert.AreEqual("foocontent", Body);

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.PutObject(BucketName, Key1, Body: "foooverwrite"));

            var AltClient2 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.GetObject(BucketName, Key2));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.PutObject(BucketName, Key2, Body: "baroverwrite"));

            var AltClient3 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient3.ListObjects(BucketName));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient3.PutObject(BucketName, NewKey, Body: "newcontent"));
        }

        [TestMethod("test_access_bucket_private_objectv2_publicreadwrite")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:private, object_acl:private, public-read-write]" +
            "메인유저가 pirvate권한으로 생성한 버킷과 오브젝트는 서브유저가 목록을 보거나 다운로드할 수 없지만" +
            "public-read-write로 설정한 오브젝트는 다운로드만 할 수 있음을 확인 (ListObjectsV2)" +
            "(버킷의 권한이 private이기 때문에 오브젝트의 권한이 public-read-write로 설정되어있어도 업로드불가)")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_private_objectv2_publicreadwrite()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.Private, S3CannedACL.PublicReadWrite);
            var AltClient = GetAltClient();
            var Response = AltClient.GetObject(BucketName, Key1);

            var Body = GetBody(Response);
            Assert.AreEqual("foocontent", Body);

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.PutObject(BucketName, Key1, Body: "foooverwrite"));

            var AltClient2 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.GetObject(BucketName, Key2));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.PutObject(BucketName, Key2, Body: "baroverwrite"));

            var AltClient3 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient3.ListObjectsV2(BucketName));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient3.PutObject(BucketName, NewKey, Body: "newcontent"));
        }

        [TestMethod("test_access_bucket_publicread_object_private")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:public-read, object_acl:private] " +
                                     "메인유저가 public-read권한으로 생성한 버킷에서 private권한으로 생성한 오브젝트에 대해 " +
                                     "서브유저는 오브젝트 목록만 볼 수 있음을 확인")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_publicread_object_private()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.PublicRead, S3CannedACL.Private);
            var AltClient = GetAltClient();

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.GetObject(BucketName, Key1));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.PutObject(BucketName, Key1, Body: "foooverwrite"));

            var AltClient2 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.GetObject(BucketName, Key2));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.PutObject(BucketName, Key2, Body: "baroverwrite"));

            var AltClient3 = GetAltClient();
            var ObjList = GetKeys(AltClient3.ListObjects(BucketName));
            CollectionAssert.AreEqual(new List<string>() { Key2, Key1 }, ObjList);
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient3.PutObject(BucketName, NewKey, Body: "newcontent"));
        }

        [TestMethod("test_access_bucket_publicread_object_publicread")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:public-read, object_acl:public-read, private] " +
                                     "메인유저가 public-read권한으로 생성한 버킷에서 private권한으로 생성한 오브젝트에 대해 " +
                                     "서브유저는 오브젝트 목록만 볼 수 있음을 확인")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_publicread_object_publicread()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.PublicRead, S3CannedACL.PublicRead);
            var AltClient = GetAltClient();

            var Response = AltClient.GetObject(BucketName, Key1);
            var Body = GetBody(Response);
            Assert.AreEqual("foocontent", Body);

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.PutObject(BucketName, Key1, Body: "foooverwrite"));

            var AltClient2 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.GetObject(BucketName, Key2));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.PutObject(BucketName, Key2, Body: "baroverwrite"));

            var AltClient3 = GetAltClient();
            var ObjList = GetKeys(AltClient3.ListObjects(BucketName));
            CollectionAssert.AreEqual(new List<string>() { Key2, Key1 }, ObjList);
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient3.PutObject(BucketName, NewKey, Body: "newcontent"));
        }

        [TestMethod("test_access_bucket_publicread_object_publicreadwrite")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:public-read, object_acl:public-read-wirte, private]" +
            "메인유저가 public-read권한으로 생성한 버킷에서 public-read-write권한으로 생성한 오브젝트에 대해" +
            "서브유저는 오브젝트 목록을 보거나 다운로드 할 수 있음을 확인" +
            "(버킷의 권한이 public-read이기 때문에 오브젝트의 권한이 public-read-write로 설정되어있어도 수정불가)")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_publicread_object_publicreadwrite()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.PublicRead, S3CannedACL.PublicReadWrite);
            var AltClient = GetAltClient();

            var Response = AltClient.GetObject(BucketName, Key1);
            var Body = GetBody(Response);
            Assert.AreEqual("foocontent", Body);

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.PutObject(BucketName, Key1, Body: "foooverwrite"));

            var AltClient2 = GetAltClient();
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.GetObject(BucketName, Key2));
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient2.PutObject(BucketName, Key2, Body: "baroverwrite"));

            var AltClient3 = GetAltClient();
            var ObjList = GetKeys(AltClient3.ListObjects(BucketName));
            CollectionAssert.AreEqual(new List<string>() { Key2, Key1 }, ObjList);
            Assert.ThrowsException<AmazonS3Exception>(() => AltClient3.PutObject(BucketName, NewKey, Body: "newcontent"));
        }

        [TestMethod("test_access_bucket_publicreadwrite_object_private")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:public-read-write, object_acl:private] " +
            "메인유저가 public-read-write권한으로 생성한 버킷에서 private권한으로 생성한 오브젝트에 대해 " +
            "서브유저는 오브젝트 목록을 읽거나 업로드는 가능하지만 다운로드 할 수 없음을 확인")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_publicreadwrite_object_private()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.PublicReadWrite, S3CannedACL.Private);
            var AltClient = GetAltClient();


            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.GetObject(BucketName, Key1));
            AltClient.PutObject(BucketName, Key1, Body: "barcontent");

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.GetObject(BucketName, Key2));
            AltClient.PutObject(BucketName, Key2, Body: "baroverwrite");

            var ObjList = GetKeys(AltClient.ListObjects(BucketName));
            CollectionAssert.AreEqual(new List<string>() { Key2, Key1 }, ObjList);
            AltClient.PutObject(BucketName, NewKey, Body: "newcontent");
        }

        [TestMethod("test_access_bucket_publicreadwrite_object_publicread")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:public-read-write, object_acl:public-read, private] " +
                                     "메인유저가 public-read-write권한으로 생성한 버킷에서 public-read권한으로 생성한 오브젝트에 대해 " +
                                     "서브유저는 오브젝트 목록을 읽거나 업로드, 다운로드 모두 가능함을 확인")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_publicreadwrite_object_publicread()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.PublicReadWrite, S3CannedACL.PublicRead);
            var AltClient = GetAltClient();

            var Response = AltClient.GetObject(BucketName, Key1);
            var Body = GetBody(Response);
            Assert.AreEqual("foocontent", Body);
            AltClient.PutObject(BucketName, Key1, Body: "barcontent");

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.GetObject(BucketName, Key2));
            AltClient.PutObject(BucketName, Key2, Body: "baroverwrite");

            var ObjList = GetKeys(AltClient.ListObjects(BucketName));
            CollectionAssert.AreEqual(new List<string>() { Key2, Key1 }, ObjList);
            AltClient.PutObject(BucketName, NewKey, Body: "newcontent");
        }

        [TestMethod("test_access_bucket_publicreadwrite_object_publicreadwrite")]
        [TestProperty(MainData.Major, "Grants")]
        [TestProperty(MainData.Minor, "Access")]
        [TestProperty(MainData.Explanation, "[bucket_acl:public-read-write, object_acl:public-read-write, private] " +
                                     "메인유저가 public-read-write권한으로 생성한 버킷에서 public-read-write권한으로 생성한 오브젝트에 대해 " +
                                     "서브유저는 오브젝트 목록을 읽거나 업로드, 다운로드 모두 가능함을 확인")]
        [TestProperty(MainData.Result, MainData.ResultFailure)]
        public void test_access_bucket_publicreadwrite_object_publicreadwrite()
        {
            var BucketName = SetupBucketAndObjectsACL(out string Key1, out string Key2, out string NewKey, S3CannedACL.PublicReadWrite, S3CannedACL.PublicReadWrite);
            var AltClient = GetAltClient();

            var Response = AltClient.GetObject(BucketName, Key1);
            var Body = GetBody(Response);
            Assert.AreEqual("foocontent", Body);
            AltClient.PutObject(BucketName, Key1, Body: "foooverwrite");

            Assert.ThrowsException<AmazonS3Exception>(() => AltClient.GetObject(BucketName, Key2));
            AltClient.PutObject(BucketName, Key2, Body: "baroverwrite");

            var ObjList = GetKeys(AltClient.ListObjects(BucketName));
            CollectionAssert.AreEqual(new List<string>() { Key2, Key1 }, ObjList);
            AltClient.PutObject(BucketName, NewKey, Body: "newcontent");
        }
    }

}
