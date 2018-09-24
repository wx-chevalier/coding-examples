package com.alibaba.middleware.race.sync;

/**
 * 外部赛示例代码需要的常量 Created by wanshao on 2017/5/25.
 */
public interface Constants {
    
    boolean ASSERT = false;
    boolean PERF_LOG = false;

    int SEGMENT_QUEUE_SIZE = 16;

    int PARSER_NUM = 6;
    int WORKER_NUM = 10; // Aka. BUCKET_NUM
    
    int READ_BUFFER_SIZE = 65536;
    
    int STRING_STORE_PAGE_SIZE = 2 * 1024 * 1024;
    
    int RESULT_BUFFER_SIZE = 10 * 1024 * 1024;
    int RESULT_QUEUE_SIZE = 32768;
    
    int SEGMENT_SIZE = 16 * 1024 * 1024;
    int SEGMENT_MARGIN = 65536 * 2;
    int SEGMENT_SIZE_WITH_MARGIN = SEGMENT_SIZE + SEGMENT_MARGIN;
    int SEGMENT_MAX_EVENTS_NUM = 400000;
    int SEGMENT_MAX_EVENTS_NUM_PER_BUCKET = 60000; // should >= SEGMENT_MAX_EVENTS_NUM / WORKER_NUM
    
    int BUCKET_TABLE_INITIAL_SIZE = 6000000 / WORKER_NUM;
    int BUCKET_BLOCKED_TABLE_INITIAL_SIZE = 25000;
    int BUCKET_KEYS_SET_INITIAL_SIZE = 7000000 / WORKER_NUM;
    int BUCKET_PROMISE_POOL_SIZE = 700000;
    
    int GLOBAL_DATA_BUFFER_LENGTH = 45875200; // 350 MB
    int GLOBAL_DATA_BUFFER_ALLOCATE_UNIT = 655360; // 5 MB
    
    String RESULT_FILE_NAME = "Result.rs";
    String TEAMCODE = "74785s2rt2";
    String LOG_LEVEL = "INFO";
    Integer SERVER_PORT = 5527;

    String TESTER_HOME = "/home/admin";

    // ------------ 本地测试可以使用自己的路径--------------//

//    String DATA_HOME = "/Users/yfu/Workspace/code.aliyun.com/fuyufjh/SyncTestData/10G";
//    String RESULT_HOME = "./user_result";
//    String MIDDLE_HOME = "./middle";
//
//    String SERVER_LOG_PATH = "./server-custom.log";
//    String CLIENT_LOG_PATH = "./client-custom.log";
    
    // ------------ 正式比赛指定的路径--------------//
    //// 工作主目录
    //// 赛题数据
    String DATA_HOME = "/home/admin/canal_data";
    String RESULT_HOME = "/home/admin/sync_results/74785s2rt2";
    String MIDDLE_HOME = "/home/admin/middle/74785s2rt2";

    String SERVER_LOG_PATH = "/home/admin/logs/74785s2rt2/server-custom.log";
    String CLIENT_LOG_PATH = "/home/admin/logs/74785s2rt2/client-custom.log";
}
