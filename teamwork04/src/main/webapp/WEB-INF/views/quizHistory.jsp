
<%@ page language="java"
    contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8" %>

<%@ page import="java.util.ArrayList" %>
<%@ page import="aiwa.entity.QuizResult" %>

<%
    ArrayList<QuizResult> history =
        (ArrayList<QuizResult>) request.getAttribute("history");

    // Summary calculations
    int totalAttempts = 0;
    int perfectScores = 0;
    double totalPercentage = 0;

    if (history != null) {

        totalAttempts = history.size();

        for (QuizResult result : history) {

            int score = result.getScore();
            int total = result.getTotalQuestions();

            if (total > 0) {

                double percentage =
                    score * 100.0 / total;

                totalPercentage += percentage;

                if (score == total) {
                    perfectScores++;
                }
            }
        }
    }

    double averageScore = totalAttempts > 0
        ? totalPercentage / totalAttempts
        : 0;
%>

<!DOCTYPE html>
<html lang="en">

<head>
    <title>Quiz History - StudyFlow</title>

    <%@ include file="/WEB-INF/views/components/head.jspf" %>

    <style>
        .qh-page {
            max-width: 1150px;
            margin: 0 auto;
            padding: 48px 20px 72px;
        }

        .qh-subtitle {
            color: #77758a;
            margin-bottom: 32px;
        }

        .qh-stats {
            display: grid;
            grid-template-columns:
                repeat(4, minmax(0, 1fr));
            gap: 18px;
            margin-bottom: 40px;
        }

        .qh-card {
            background: #ffffff;
            border: 1px solid #e9e5f4;
            border-radius: 18px;
            padding: 24px;
            box-shadow:
                0 4px 18px rgba(60, 40, 100, 0.04);
        }

        .qh-card-label {
            font-size: 14px;
            color: #77758a;
            margin-bottom: 14px;
        }

        .qh-card-value {
            font-size: 30px;
            font-weight: 700;
            color: #21183e;
        }

        .qh-section-title {
            margin-bottom: 22px;
            font-size: 23px;
            font-weight: 700;
        }

        .qh-results {
            display: grid;
            gap: 16px;
        }

        .qh-result-card {
            background: white;
            border: 1px solid #e9e5f4;
            border-radius: 18px;
            padding: 24px;
        }

        .qh-result-top {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 20px;
            margin-bottom: 18px;
        }

        .qh-result-title {
            font-size: 17px;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .qh-date {
            font-size: 13px;
            color: #77758a;
        }

        .qh-score {
            font-size: 22px;
            font-weight: 700;
            color: #7c4dff;
            white-space: nowrap;
        }

        .qh-progress {
            width: 100%;
            height: 10px;
            background: #eeeaf7;
            border-radius: 20px;
            overflow: hidden;
            margin-bottom: 14px;
        }

        .qh-progress-fill {
            height: 100%;
            background: #8b5cf6;
            border-radius: 20px;
        }

        .qh-result-bottom {
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 12px;
        }

        .qh-status {
            display: inline-block;
            padding: 6px 12px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }

        .qh-perfect {
            background: #eee7ff;
            color: #6d36d6;
        }

        .qh-passed {
            background: #e7f5ee;
            color: #23764c;
        }

        .qh-practice {
            background: #fff0e6;
            color: #a95b1b;
        }

        .qh-back {
            display: inline-block;
            margin-top: 30px;
            padding: 12px 20px;
            background: #8b5cf6;
            color: white;
            border-radius: 12px;
            text-decoration: none;
            font-weight: 600;
        }

        .qh-back:hover {
            background: #7443df;
            color: white;
        }

        @media (max-width: 900px) {
            .qh-stats {
                grid-template-columns:
                    repeat(2, minmax(0, 1fr));
            }
        }

        @media (max-width: 550px) {
            .qh-stats {
                grid-template-columns: 1fr;
            }

            .qh-result-top {
                flex-direction: column;
            }

            .qh-page {
                padding: 28px 16px 48px;
            }
        }
    </style>
</head>

<body>

    <jsp:include
        page="/WEB-INF/views/components/header.jsp" />

    <main class="qh-page">

        <h1>Quiz History</h1>

        <p class="qh-subtitle">
            Your learning journey,
            one quiz at a time.
        </p>

        <!-- Summary Cards -->
        <section class="qh-stats">

            <div class="qh-card">
                <div class="qh-card-label">
                    📝 Total Attempts
                </div>

                <div class="qh-card-value">
                    <%= totalAttempts %>
                </div>
            </div>

            <div class="qh-card">
                <div class="qh-card-label">
                    🎯 Average Score
                </div>

                <div class="qh-card-value">
                    <%= String.format(
                        "%.1f", averageScore) %>%
                </div>
            </div>

            <div class="qh-card">
                <div class="qh-card-label">
                    🏆 Perfect Scores
                </div>

                <div class="qh-card-value">
                    <%= perfectScores %>
                </div>
            </div>

            <div class="qh-card">
                <div class="qh-card-label">
                    🌱 Growing Plant
                </div>

                <div class="qh-card-value"
                     style="font-size: 17px;">
                    Coming Soon
                </div>
            </div>

        </section>

        <!-- Quiz Results -->
        <section>

            <h2 class="qh-section-title">
                Recent Quiz Results
            </h2>

            <% if (history == null
                    || history.isEmpty()) { %>

                <div class="qh-result-card">
                    No quiz history yet.
                    Complete a quiz to see
                    your progress here!
                </div>

            <% } else { %>

                <div class="qh-results">

                    <% for (QuizResult result : history) {

                        int score = result.getScore();
                        int total =
                            result.getTotalQuestions();

                        double percentage = total > 0
                            ? score * 100.0 / total
                            : 0;

                        String status;
                        String statusClass;

                        if (percentage == 100) {
                            status = "Perfect!";
                            statusClass = "qh-perfect";

                        } else if (percentage >= 70) {
                            status = "Passed";
                            statusClass = "qh-passed";

                        } else {
                            status = "Keep Practicing";
                            statusClass = "qh-practice";
                        }
                    %>

                    <div class="qh-result-card">

                        <div class="qh-result-top">

                            <div>
                                <div class="qh-result-title">
                                    <%= result.getQuizTitle() %>
                                </div>

                                <div class="qh-date">
                                    <%= result.getCompletedAt() %>
                                </div>
                            </div>

                            <div class="qh-score">
                                <%= String.format(
                                    "%.1f", percentage) %>%
                            </div>

                        </div>

                        <div class="qh-progress">

                            <div class="qh-progress-fill"
                                 style="width:
                                 <%= percentage %>%;">
                            </div>

                        </div>

                        <div class="qh-result-bottom">

                            <span>
                                <%= score %> /
                                <%= total %> correct
                            </span>

                            <span class="qh-status
                                <%= statusClass %>">
                                <%= status %>
                            </span>

                        </div>

                    </div>

                    <% } %>

                </div>

            <% } %>

        </section>

        <a href="<%= request.getContextPath() %>/DashboardController"
           class="qh-back">
            Back to Dashboard
        </a>

    </main>

    <jsp:include
        page="/WEB-INF/views/components/footer.jsp" />

</body>
</html>
