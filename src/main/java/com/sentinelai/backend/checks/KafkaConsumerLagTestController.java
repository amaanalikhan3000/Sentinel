//package com.sentinelai.backend.checks;
//
//
//import com.sentinelai.backend.Service.KafkaConsumerLagTool;
//import com.sentinelai.backend.dto.KafkaConsumerLagResponse;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class KafkaConsumerLagTestController {
//
//    private final KafkaConsumerLagTool kafkaConsumerLagTool;
//
//    public KafkaConsumerLagTestController(
//            KafkaConsumerLagTool kafkaConsumerLagTool) {
//        this.kafkaConsumerLagTool = kafkaConsumerLagTool;
//    }
//
//    @GetMapping("/api/tools/kafka-lag")
//    public KafkaConsumerLagResponse getKafkaLag(
//            @RequestParam String consumerGroup,
//            @RequestParam String topic) {
//
//        return kafkaConsumerLagTool.getKafkaConsumerLag(
//                consumerGroup,
//                topic
//        );
//    }
//}