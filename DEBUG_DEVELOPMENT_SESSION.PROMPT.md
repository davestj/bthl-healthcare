BTHL-HealthCare Debug Session Context

Git Workflow Process:
1. git status --porcelain
2. grep -r "Git Commit:" src/main/java/com/bthl/healthcare/
3. git add .
4. git commit -m "session commit message"
5. git push origin develop

After updating CARRY_OVER.md and CHANGELOG.md:
git add CARRY_OVER.md CHANGELOG.md
git commit -m "docs: update session tracking"
git push origin develop

Debug Commands:
cd /Users/dstjohn/dev/02_davestj.com/bthl-hc
mvn clean compile
mvn spring-boot:run

Success: Spring Boot on port 8330, login at http://localhost:8330/login
