package com.esstudy01;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;

/**
 * @program: elasticsearch
 * @ClassName: ES_Index_Search
 * @author: skl
 * @create: 2024-11-28 15:18
 */
public class ES_Doc_Insert {
    public static void main(String[] args) throws Exception {
        //创建ES客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );

        //插入数据
        IndexRequest indexRequest = new IndexRequest();
        indexRequest.index("user").id("1001");
        User user = new User();
        user.setName("zhangsan");
        user.setAge(30);
        user.setSex("男");
        //向ES插入数据，必须转换为json格式
        ObjectMapper mapper = new ObjectMapper();
        String userJson = mapper.writeValueAsString(user);
        indexRequest.source(userJson, XContentType.JSON);


        IndexResponse response = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
        System.out.println(response.getResult());

        //关闭ES客户端
        restHighLevelClient.close();
    }
}
