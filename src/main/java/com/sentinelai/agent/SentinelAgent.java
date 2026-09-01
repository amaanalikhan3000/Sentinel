package com.sentinelai.agent;

import com.google.adk.agents.LlmAgent;
import com.google.adk.tools.FunctionTool;
import com.sentinelai.agent.tools.*;
import com.google.adk.agents.RunConfig;
import com.google.adk.events.Event;
import com.google.adk.runner.InMemoryRunner;
import com.google.adk.sessions.Session;
import com.google.genai.types.Content;
import com.google.genai.types.Part;
import java.lang.reflect.Method;

/**
 * SentinelAI Java ADK agent with five investigation tools registered.
 * Authenticates via GOOGLE_API_KEY environment variable.
 * Tools connect to existing Spring Boot backend via REST API.
 *
 * Tool registration uses Google ADK's FunctionTool.create() which wraps
 * Java methods as ADK tools via reflection.
 */
public class SentinelAgent {

    private static final String DEFAULT_BACKEND_URL = "http://localhost:8080";

    public static void main(String[] args) {
        // Verify GOOGLE_API_KEY is set
        String apiKey = System.getenv("GOOGLE_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            System.err.println("ERROR: GOOGLE_API_KEY environment variable is not set.");
            System.err.println("Please set GOOGLE_API_KEY before running this agent.");
            System.exit(1);
        }

        // Get backend URL from environment or use default
        String backendUrl = System.getenv("BACKEND_URL");
        if (backendUrl == null || backendUrl.trim().isEmpty()) {
            backendUrl = DEFAULT_BACKEND_URL;
        }

        System.out.println("SentinelAI Agent - Starting...");
        System.out.println("Using Gemini model: gemini-2.0-flash-exp");
        System.out.println("Backend URL: " + backendUrl);

        try {
            // Create backend tool client
            BackendToolClient backendClient = new BackendToolClient(backendUrl);

            // Create five investigation tool wrappers
            ServiceHealthFunctionTool serviceHealthTool = new ServiceHealthFunctionTool(backendClient);
            PaymentMetricsFunctionTool paymentMetricsTool = new PaymentMetricsFunctionTool(backendClient);
            KafkaConsumerLagFunctionTool kafkaLagTool = new KafkaConsumerLagFunctionTool(backendClient);
            RecentErrorsFunctionTool recentErrorsTool = new RecentErrorsFunctionTool(backendClient);
            DependencyHealthFunctionTool dependencyHealthTool = new DependencyHealthFunctionTool(backendClient);

            System.out.println("\n=== Five Investigation Tools ===");
            System.out.println("1. " + serviceHealthTool.getName() + ": " + serviceHealthTool.getDescription());
            System.out.println("2. " + paymentMetricsTool.getName() + ": " + paymentMetricsTool.getDescription());
            System.out.println("3. " + kafkaLagTool.getName() + ": " + kafkaLagTool.getDescription());
            System.out.println("4. " + recentErrorsTool.getName() + ": " + recentErrorsTool.getDescription());
            System.out.println("5. " + dependencyHealthTool.getName() + ": " + dependencyHealthTool.getDescription());
            System.out.println("==========================================\n");

            // Register tools with ADK using FunctionTool.create()
            // This wraps each tool's execute() method as an ADK BaseTool via reflection
            System.out.println("Registering tools with Google ADK FunctionTool...");

            // Tool 1: getServiceHealth() - no parameters
            Method serviceHealthMethod = ServiceHealthFunctionTool.class.getMethod("execute");
            FunctionTool adkServiceHealthTool = FunctionTool.create(serviceHealthTool, serviceHealthMethod);
            System.out.println("Registered: " + adkServiceHealthTool.name() + " (declaration: " + adkServiceHealthTool.declaration().isPresent() + ")");

            // Tool 2: getPaymentMetrics() - no parameters
            Method paymentMetricsMethod = PaymentMetricsFunctionTool.class.getMethod("execute");
            FunctionTool adkPaymentMetricsTool = FunctionTool.create(paymentMetricsTool, paymentMetricsMethod);
            System.out.println("Registered: " + adkPaymentMetricsTool.name() + " (declaration: " + adkPaymentMetricsTool.declaration().isPresent() + ")");

            // Tool 3: getKafkaConsumerLag(String consumerGroup, String topic)
            Method kafkaLagMethod = KafkaConsumerLagFunctionTool.class.getMethod("execute", String.class, String.class);
            FunctionTool adkKafkaLagTool = FunctionTool.create(kafkaLagTool, kafkaLagMethod);
            System.out.println("Registered: " + adkKafkaLagTool.name() + " (declaration: " + adkKafkaLagTool.declaration().isPresent() + ")");

            // Tool 4: getRecentErrors(String service, long timeWindowSeconds)
            Method recentErrorsMethod = RecentErrorsFunctionTool.class.getMethod("execute", String.class, long.class);
            FunctionTool adkRecentErrorsTool = FunctionTool.create(recentErrorsTool, recentErrorsMethod);
            System.out.println("Registered: " + adkRecentErrorsTool.name() + " (declaration: " + adkRecentErrorsTool.declaration().isPresent() + ")");

            // Tool 5: getDependencyHealth() - no parameters
            Method dependencyHealthMethod = DependencyHealthFunctionTool.class.getMethod("execute");
            FunctionTool adkDependencyHealthTool = FunctionTool.create(dependencyHealthTool, dependencyHealthMethod);
            System.out.println("Registered: " + adkDependencyHealthTool.name() + " (declaration: " + adkDependencyHealthTool.declaration().isPresent() + ")");

            System.out.println("\nAll five tools registered with FunctionTool.create()\n");

            // Create LlmAgent with all five tools registered
            System.out.println("Creating LlmAgent...");
            LlmAgent agent = LlmAgent.builder()
                .name("sentinel_investigation_agent")
                .description("SentinelAI agent with five investigation tools")
                .model("gemini-2.0-flash-exp")
                    .instruction("""
    You are SentinelAI, an incident investigation agent
    for a simulated banking payment system.

    Investigate before concluding.

    Rules:
    1. Use tools to collect evidence.
    2. Do not invent operational values.
    3. Check multiple signals before naming a root cause.
    4. Separate observed evidence from conclusions.
    5. Report tool failures.
    6. Do not execute production changes.
    7. Return incident, severity, confidence, rootCause,
       evidence, customerImpact, and recommendation.
    """)
                .tools(adkServiceHealthTool, adkPaymentMetricsTool, adkKafkaLagTool, adkRecentErrorsTool, adkDependencyHealthTool)
                .build();

            System.out.println("\n=== Agent Created Successfully ===");
            System.out.println("Agent Name: " + agent.name());
            System.out.println("Model: gemini-2.0-flash-exp");
            System.out.println("Tools registered with agent: " + agent.tools().blockingGet().size());
            System.out.println("===================================\n");

            // Verify all tools are discoverable
            System.out.println("Verifying tool discovery...");
            var registeredTools = agent.tools().blockingGet();
            System.out.println("ADK agent reports " + registeredTools.size() + " tools:");
            for (var tool : registeredTools) {
                System.out.println("  - " + tool.name() + ": " + tool.description());
                System.out.println("    Declaration present: " + tool.declaration().isPresent());
            }

            System.out.println("\n=== Step 11.3 Complete ===");
            System.out.println("All five investigation tools are registered with the ADK agent.");
            System.out.println("The agent can discover and invoke these tools via function calling.");

        } catch (Exception e) {
            System.err.println("ERROR: Failed to initialize agent or register tools");
            System.err.println("Error details: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
