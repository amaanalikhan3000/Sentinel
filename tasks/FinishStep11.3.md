# Finish Step 11.3 — Register the Five Tools with Java ADK

The five SentinelAI investigation tool wrappers have been created and verified against the existing Spring Boot REST APIs.

Current status:

- Wrapper classes exist.
- Each wrapper obtains real backend data.
- Backend remains unchanged.
- Maven build succeeds.
- Remaining blocker: the wrappers are not yet registered/exposed to the Google Java ADK agent.

Task:

1. Inspect the currently installed Google ADK version and its Java tool/function API.
2. Use the actual supported Java ADK registration mechanism for Function Tools.
3. Register all five existing SentinelAI tools with the existing LlmAgent.
4. Do not guess an API such as execute(), send(), or another unsupported method.
5. Do not change the existing Spring Boot architecture.
6. Do not redesign the five tools.
7. Do not add new tools.
8. Do not add unnecessary dependencies.
9. Keep the tools read-only.
10. Do not implement SentinelAI agent instructions, RCA, investigation persistence, or UI yet.

Five tools:

- getServiceHealth()
- getPaymentMetrics(startTime, endTime)
- getKafkaConsumerLag(consumerGroup, topic)
- getRecentErrors(service, timeWindow)
- getDependencyHealth()

Acceptance criteria:

- All five tools are actually registered with the LlmAgent.
- The project compiles successfully.
- The ADK agent can discover the five tools.
- No backend functionality is changed.
- No secrets are exposed.

If the installed ADK version does not support the expected registration API, stop and report the exact API limitation instead of inventing a workaround.

After completion, report:

- ADK tool registration mechanism used
- Files changed
- Build result
- Confirmation that all five tools are registered
- Any remaining blocker

Stop after Step 11.3 is technically complete.
