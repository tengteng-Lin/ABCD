#  需求

- query

  - 目的：获取结果列表
  - 参数：`{xxxxxxxxxxxxx}`
  - 返回：`{"session_id":"","query_result":[{"local_id":"","name":"","author":"","org_title":""}]}`
  - 方法：GET

- getMetadata

  - 目的：获取元数据信息
  - 参数：`{“session_id":"",”local_id":""}`
  - 返回：`{"notes":"","author_email":"","maintainer":"","maintainer_email":"","org_description":"","metadata_created":"","metadata_modified":""}`
  - 方法：GET

- getSchemaStatistics

  - 目的：获取schema相关的统计数据
  - 参数：`{"session_id":"","local_id":""} `         **可以后台存储某个session的local_id信息**
  - 返回：`{"count"：{"triple count"："","incomingLinkCount":"","outcomingLinkCount":"","classCount":""},"topTenClasses":[{"name":""}],"topTenProperties":[{"name"}]}`
  - 方法：GET

- getDataStatistics

  - 目的：获取data水平的统计数据
  - 参数：

  - 返回：

  - 方法：GET

- getSummary0

  - 目的：
  - 参数：
  - 返回：
  - 方法：GET

- getSummary1

  - 目的：
  - 参数：
  - 返回：
  - 方法：GET

- getSnippet0

  - 目的：
  - 参数：
  - 返回：
  - 方法：GET

- getSnippet1

  - 目的;
  - 参数：
  - 返回：
  - 方法：GET

- postQuestionaire

  - 目的：
  - 参数：
  - 返回：
  - 方法：POST

- 

  

  

  

  

