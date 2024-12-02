package com.esstudy01;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;

/**
 * @program: elasticsearch
 * @ClassName: ES_Index_Search
 * @author: skl
 * @create: 2024-11-28 15:18
 */
public class ES_Index_Delete {
    public static void main(String[] args) throws Exception {
        //创建ES客户端
        RestHighLevelClient restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(new HttpHost("localhost", 9200, "http"))
        );
        //查询索引
        DeleteIndexRequest request = new DeleteIndexRequest("user");
        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        //响应状态
        System.out.println(response.isAcknowledged());

        //关闭ES客户端
        restHighLevelClient.close();
    }
}
