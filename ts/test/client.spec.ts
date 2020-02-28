'use strict';

import client from "../src/client";
import * as $tea from "@alicloud/tea-typescript";
import 'mocha';
import assert from 'assert';


describe('client', function () {
  const testXml = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?>\n' +
    '<root>\n' +
    '  <Owner>\n' +
    '    <ID>1325847523475998</ID>\n' +
    '    <DisplayName>1325847523475998</DisplayName>\n' +
    '  </Owner>\n' +
    '  <AccessControlList>\n' +
    '    <Grant>public-read</Grant>\n' +
    '  </AccessControlList>\n' +
    '</root>';
  const errorXml = '<Error>\
    <Code>AccessForbidden</Code>\
    <Message>CORSResponse: CORS is not enabled for this bucket.</Message>\
    <RequestId>5DECB1F6F3150D373335D8D2</RequestId>\
    <HostId>sdk-oss-test.oss-cn-hangzhou.aliyuncs.com</HostId>\
  </Error>';

  it('parseXml should ok', async function () {
    class GetBucketAclResponseAccessControlPolicyAccessControlList extends $tea.Model {
      grant: string;
      static names(): { [key: string]: string } {
        return {
          grant: 'Grant',
        };
      }

      static types(): { [key: string]: any } {
        return {
          grant: 'string',
        };
      }

      constructor(map: { [key: string]: any }) {
        super(map);
      }

    }

    class GetBucketAclResponseAccessControlPolicyOwner extends $tea.Model {
      iD: string;
      displayName: string;
      static names(): { [key: string]: string } {
        return {
          iD: 'ID',
          displayName: 'DisplayName',
        };
      }

      static types(): { [key: string]: any } {
        return {
          iD: 'string',
          displayName: 'string',
        };
      }

      constructor(map: { [key: string]: any }) {
        super(map);
      }

    }

    class GetBucketAclResponseAccessControlPolicy extends $tea.Model {
      owner: GetBucketAclResponseAccessControlPolicyOwner;
      accessControlList: GetBucketAclResponseAccessControlPolicyAccessControlList;
      static names(): { [key: string]: string } {
        return {
          owner: 'Owner',
          accessControlList: 'AccessControlList',
        };
      }

      static types(): { [key: string]: any } {
        return {
          owner: GetBucketAclResponseAccessControlPolicyOwner,
          accessControlList: GetBucketAclResponseAccessControlPolicyAccessControlList,
        };
      }

      constructor(map: { [key: string]: any }) {
        super(map);
      }

    }

    class GetBucketAclResponse extends $tea.Model {
      accessControlPolicy: GetBucketAclResponseAccessControlPolicy;
      static names(): { [key: string]: string } {
        return {
          accessControlPolicy: 'root',
        };
      }

      static types(): { [key: string]: any } {
        return {
          accessControlPolicy: GetBucketAclResponseAccessControlPolicy,
        };
      }

      constructor(map: { [key: string]: any }) {
        super(map);
      }
    }

    const data = {
      root: {
        Owner: { ID: '1325847523475998', DisplayName: '1325847523475998' },
        AccessControlList: { Grant: 'public-read' },
      },
    };
    assert.deepStrictEqual(client.parseXml(testXml, GetBucketAclResponse), data);
    assert.ok(client.parseXml(errorXml, GetBucketAclResponse));
    try {
      client.parseXml('ddsfadf', GetBucketAclResponse)
    } catch (err) {
      assert.ok(err);
      return;
    }
    assert.ok(false);
  });

  it('_toXML should ok', function () {
    const data = {
      root: {
        Owner: { ID: '1325847523475998', DisplayName: '1325847523475998' },
        AccessControlList: { Grant: 'public-read' },
      },
    };
    assert.strictEqual(client.toXML(data), testXml);
  });

  it('_xmlCast should ok', async function () {
    const data: { [key: string]: any } = {
      boolean: false,
      boolStr: 'true',
      number: 1,
      NaNNumber: null,
      NaN: undefined,
      string: 'string',
      array: ['string1', 'string2'],
      notArray: 'string',
      emptyArray: undefined,
      classArray: [{
        string: 'string',
      }, {
        string: 'string'
      }],
      classMap: '',
      map: {
        string: 'string',
      }
    };

    class TestSubModel extends $tea.Model {
      string: string;
      static names(): { [key: string]: string } {
        return {
          string: 'string',
        };
      }

      static types(): { [key: string]: any } {
        return {
          string: 'string',
        };
      }

      constructor(map: { [key: string]: any }) {
        super(map);
      }
    }

    class TestModel extends $tea.Model {
      boolean: boolean;
      boolStr: boolean;
      string: string;
      number: number;
      NaNNumber: number;
      array: string[];
      emptyArray: string[];
      notArray: string[];
      map: { [key: string]: any };
      classArray: TestSubModel[];
      classMap: TestSubModel;
      static names(): { [key: string]: string } {
        return {
          boolean: 'boolean',
          boolStr: 'boolStr',
          string: 'string',
          number: 'number',
          NaNNumber: 'NaNNumber',
          array: 'array',
          emptyArray: 'emptyArray',
          notArray: 'notArray',
          map: 'map',
          classArray: 'classArray',
          classMap: 'classMap',
        };
      }

      static types(): { [key: string]: any } {
        return {
          boolean: 'boolean',
          boolStr: 'boolean',
          string: 'string',
          number: 'number',
          NaNNumber: 'number',
          array: { type: 'array', itemType: 'string' },
          emptyArray: { type: 'array', itemType: 'string' },
          notArray: { type: 'array', itemType: 'string' },
          map: 'map',
          classArray: { type: 'array', itemType: TestSubModel },
          classMap: TestSubModel,
        };
      }

      constructor(map: { [key: string]: any }) {
        super(map);
      }
    }

    assert.deepStrictEqual(client._xmlCast(data, TestModel), {
      "boolean": false,
      "boolStr": true,
      "number": 1,
      "NaNNumber": NaN,
      "string": 'string',
      "array": ['string1', 'string2'],
      "classArray": [{
        "string": 'string',
      }, {
        "string": 'string'
      }],
      "notArray": ['string'],
      "emptyArray": [],
      "classMap": {
        "string": ''
      },
      "map": {
        "string": 'string',
      }
    });
  });


});

