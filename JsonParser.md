
```Java
  /**
   * {
   *   "$schema": "/mediawiki/recentchange/1.0.0",
   *   "meta": {
   *     "uri": "https://www.wikidata.org/wiki/Q13187",
   *     "request_id": "b295d2b4-2948-4072-b26b-dd303367b1d1",
   *     "id": "87fe17e8-6e9d-406c-b77e-27123bcdceff",
   *     "dt": "2023-10-29T10:26:13Z",
   *     "domain": "www.wikidata.org",
   *     "stream": "mediawiki.recentchange",
   *     "topic": "codfw.mediawiki.recentchange",
   *     "partition": 0,
   *     "offset": 724054332
   *   }
   * }
   * @param json string
   * @return extracted value of "87fe17e8-6e9d-406c-b77e-27123bcdceff"
   */
  private static String parseRecordId(String json) {
    return JsonParser.parseString(json)
                     .getAsJsonObject()
                     .get("meta")
                     .getAsJsonObject()
                     .get("id")
                     .getAsString();
  }
```
