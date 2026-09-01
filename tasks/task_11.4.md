Implement Step 11.4 of SentinelAI: create the minimal standalone Java ADK agent.

First inspect the existing repository and project structure. Do not modify the existing Spring Boot backend, Kafka, MySQL, Grafana, incident APIs, or investigation tools.

Create the agent under:
agent/

Requirements:

- Use Java ADK for Gemini.
- Use the current Google Java ADK dependency documented by the official Java ADK quickstart; do not use the outdated 0.5.0 dependency shown in the Google Cloud console.
- Keep this agent isolated from the existing backend for now.
- Configure Gemini authentication through the GOOGLE_API_KEY environment variable.
- Never hard-code or commit the API key.
- Add .env only if appropriate, and ensure it is gitignored.
- Create the smallest possible agent that can make one Gemini request and return a response.
- Follow the official Java ADK quickstart structure.
- Do not add unnecessary dependencies or frameworks.
- Do not connect the five SentinelAI investigation tools yet.
- Do not modify architecture outside agent/.

After implementation:

1. Run the minimum compile/test command.
2. Report files created/changed.
3. Report dependencies added.
4. Report the exact command needed to run the basic agent.
5. Do not display or log the API key.
6. Stop after the basic Gemini agent works.
