//package com.sentinelai.backend.checks;
//
//import com.sentinelai.backend.Service.RecentErrorCollector;
//import com.sentinelai.backend.Service.RecentErrorsTool;
//import com.sentinelai.backend.dto.RecentErrorsResponse;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class RecentErrorsTestController {
//
//    private final RecentErrorsTool recentErrorsTool;
//
//    public RecentErrorsTestController(RecentErrorsTool recentErrorsTool) {
//        this.recentErrorsTool = recentErrorsTool;
//    }
//
//    @GetMapping("/api/tools/recent-errors")
//    public RecentErrorsResponse getRecentErrors(
//            @RequestParam String service,
//            @RequestParam long timeWindowSeconds) {
//
//        return recentErrorsTool.getRecentErrors(
//                service,
//                timeWindowSeconds
//        );
//    }
//
//
//
//    @GetMapping("/api/tools/recent-errors/test")
//    public String addTestError(RecentErrorCollector errorCollector) {
//        errorCollector.record(
//                "payment-service",
//                "Test payment processing error"
//        );
//        return "Test error recorded";
//    }
//}