package com.esstudy01;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.index.engine.Engine;

/**
 * @program: elasticsearch
 * @ClassName: ES_Index_Search
 * @author: skl
 * @create: 2024-11-28 15:18
 */
public class ES_Index_Search {
    public static void main(String[] args) throws Exception {
        //创建ES客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
        //查询索引
        GetIndexRequest request = new GetIndexRequest("user");
        GetIndexResponse response = restHighLevelClient.indices().get(request, RequestOptions.DEFAULT);
        //响应状态
        System.out.println(response.getAliases());
        System.out.println(response.getMappings());
        System.out.println(response.getSettings());

        //关闭ES客户端
        restHighLevelClient.close();
    }
}
