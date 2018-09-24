package com.alibaba.middleware.topkn;

/**
 * Created by wanshao on 2017/7/11 0011.
 */
public interface Constants {
    
    int BUCKET_SIZE = 5971968;  // 128*36*36*36
//    int BUCKET_SIZE = 268435456;  // 128*128*128*128 = 256M

    int WORKER_SEGMENT_SIZE = 32 * 1024 * 1024;
    
    int RESULT_BUFFER_SIZE = 5120000;
    
    int MAX_TEXT_LENGTH = 128;
    
    
    int NUM_CORE = 24;
    
    
    int OP_BUILD_INDEX = 1;
    int OP_QUERY_RANGE = 2;
    
    
    // For online tests
    String DATA_DIR = "/home/admin/final24/topkn-datafiles/";
    String MIDDLE_DIR = "/home/admin/final24/middle/86604nnnd5/";
    String RESULT_DIR = "/home/admin/final24/topkn-resultfiles/86604nnnd5/";
    

    String MASTER_LOG_PATH = "/home/admin/final24/logs/86604nnnd5/server-custom.log";
    String WORKER_LOG_PATH = "/home/admin/final24/logs/86604nnnd5/client-custom.log";

    // For local tests
//    String DATA_DIR = "/Users/yfu/Workspace/code.aliyun.com/fuyufjh/TopKNTestData/client_" + System.getenv("CLIENT_NO") + "/";
//    String MIDDLE_DIR = "./middle/";
//    String RESULT_DIR = "./result/";

}
