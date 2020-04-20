android_lint.report_file = "app/build/reports/lint-results.xml"
android_lint.lint

jacoco.gradle_task = "jacocoTestReport"
jacoco.report_file = "vibes/build/reports/jacoco/jacocoTestDebugUnitTestReport/jacocoTestDebugUnitTestReport.xml"
jacoco.report

# Make it more obvious that a PR is a work in progress and shouldn't be merged yet
warn("PR is classed as Work in Progress") if github.pr_title.include? "[WIP]"

# Warn when there is a big PR
warn("Big PR") if git.lines_of_code > 500

findbugs.report_file = "vibes/build/reports/findbugs/findbugs.xml"
findbugs.report
