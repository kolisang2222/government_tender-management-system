<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Evaluation Panel - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/evaluatordashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/evaluation-panel.css">
        <link rel="stylesheet" href="procgov-dashboard.css">
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <!-- Page Header -->
                <div class="page-header">
                    <div class="header-left">
                        <a href="${pageContext.request.contextPath}/evaluator/evaluations" class="back-link">
                            Back to My Evaluations
                        </a>
                        <h1>Bid Evaluation Panel</h1>
                        <p>Tender: ${tender.referenceNumber} - ${tender.title}</p>
                    </div>
                    <div class="header-right">
                        <span class="status-badge status-${tender.status.name().toLowerCase()}">
                            ${tender.status.displayName}
                        </span>
                    </div>
                </div>

                <!-- Success/Error Messages -->
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="alert alert-success">
                        <span>OK</span>
                        <span>${sessionScope.successMessage}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>

                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-error">
                        <span>!</span>
                        <span>${sessionScope.errorMessage}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <!-- Progress Status -->
                <div class="progress-status-card">
                    <div class="progress-info">
                        <span class="progress-label">Your Progress:</span>
                        <span class="progress-value">${scoredCount} / ${bidCount} bids scored</span>
                    </div>
                    <div class="progress-bar-container">
                        <div class="progress-bar" style="width: ${progressPercent}%"></div>
                    </div>
                    <c:if test="${!hasSubmittedAllScores && !isOfficer}">
                        <div class="visibility-notice">
                            <span>!</span>
                            <span>You must score ALL bids before viewing other evaluators' scores.</span>
                        </div>
                    </c:if>
                </div>

                <!-- Bids Evaluation Cards -->
                <div class="bids-container">
                    <c:choose>
                        <c:when test="${not empty evaluationData}">
                            <c:forEach var="bidData" items="${evaluationData}">
                                <div class="bid-evaluation-card ${bidData.hasScored ? 'scored' : 'pending'}">

                                    <!-- Bid Header -->
                                    <div class="bid-header">
                                        <div class="bid-info">
                                            <h3>${bidData.supplierName}</h3>
                                            <div class="bid-meta">
                                                <span class="bid-amount">Bid: ${bidData.bidAmount}</span>
                                                <span class="timeline">Timeline: ${bidData.timelineDays} days</span>
                                            </div>
                                        </div>
                                        <c:if test="${bidData.hasScored}">
                                            <span class="scored-badge">Scored</span>
                                        </c:if>
                                    </div>

                                    <!-- Technical Statement -->
                                    <div class="technical-statement">
                                        <h4>Technical Compliance Statement</h4>
                                        <p>${bidData.technicalStatement}</p>
                                    </div>

                                    <!-- Auto-Calculated Scores (Always Visible) -->
                                    <div class="auto-scores">
                                        <h4>Auto-Calculated Scores</h4>
                                        <div class="scores-grid">
                                            <div class="score-item">
                                                <span class="score-label">Price Score (40%)</span>
                                                <span class="score-value">${bidData.autoPriceScore}</span>
                                            </div>
                                            <div class="score-item">
                                                <span class="score-label">Timeline Score (25%)</span>
                                                <span class="score-value">${bidData.autoTimelineScore}</span>
                                            </div>
                                        </div>
                                    </div>

                                    <!-- Scoring Form (Only if not scored yet) -->
                                    <c:if test="${!bidData.hasScored && !isOfficer}">
                                        <div class="scoring-form">
                                            <h4>Enter Technical Score (35%)</h4>
                                            <form action="${pageContext.request.contextPath}/evaluator/submit-score" method="POST" class="score-form">
                                                <input type="hidden" name="tenderId" value="${tender.tenderId}">
                                                <input type="hidden" name="bidId" value="${bidData.bidId}">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                                                <div class="form-group">
                                                    <label for="technicalScore_${bidData.bidId}">Technical Compliance Score (0-100)</label>
                                                    <input type="number" 
                                                           id="technicalScore_${bidData.bidId}" 
                                                           name="technicalScore" 
                                                           min="0" 
                                                           max="100" 
                                                           step="0.01"
                                                           placeholder="Enter score between 0-100"
                                                           required>
                                                </div>

                                                <button type="submit" class="btn btn-primary">Submit Score</button>
                                            </form>
                                        </div>
                                    </c:if>

                                    <!-- My Score (If already scored) -->
                                    <c:if test="${bidData.hasScored}">
                                        <div class="my-score">
                                            <h4>Your Score</h4>
                                            <div class="scores-grid">
                                                <div class="score-item">
                                                    <span class="score-label">Technical (35%)</span>
                                                    <span class="score-value">${bidData.myTechnicalScore}</span>
                                                </div>
                                                <div class="score-item">
                                                    <span class="score-label">Price (40%)</span>
                                                    <span class="score-value">${bidData.myPriceScore}</span>
                                                </div>
                                                <div class="score-item">
                                                    <span class="score-label">Timeline (25%)</span>
                                                    <span class="score-value">${bidData.myTimelineScore}</span>
                                                </div>
                                                <div class="score-item highlight">
                                                    <span class="score-label">Weighted Total</span>
                                                    <span class="score-value">${bidData.myWeightedTotal}</span>
                                                </div>
                                            </div>
                                        </div>
                                    </c:if>

                                    <!-- Other Evaluators' Scores (Only visible after submitting all) -->
                                    <c:if test="${bidData.showOtherScores || isOfficer}">
                                        <div class="other-scores">
                                            <h4>All Evaluators' Scores</h4>
                                            <table class="scores-table">
                                                <thead>
                                                    <tr>
                                                        <th>Evaluator</th>
                                                        <th>Technical</th>
                                                        <th>Price</th>
                                                        <th>Timeline</th>
                                                        <th>Total</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="evalScore" items="${bidData.otherScores}">
                                                        <tr class="${evalScore.isCurrentUser ? 'current-user' : ''}">
                                                            <td>${evalScore.evaluatorName} ${evalScore.isCurrentUser ? '(You)' : ''}</td>
                                                            <td>${evalScore.technicalScore}</td>
                                                            <td>${evalScore.priceScore}</td>
                                                            <td>${evalScore.timelineScore}</td>
                                                            <td><strong>${evalScore.weightedTotal}</strong></td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                            <div class="average-score">
                                                <strong>Average Final Score: ${bidData.averageScore}</strong>
                                            </div>
                                        </div>
                                    </c:if>

                                    <c:if test="${!bidData.showOtherScores && !isOfficer && bidData.hasScored}">
                                        <div class="hidden-scores-message">
                                            <p>Submit scores for ALL bids to view other evaluators' scores.</p>
                                        </div>
                                    </c:if>

                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <p>No bids available for evaluation.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            // Close alerts after 5 seconds
            setTimeout(function () {
                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(function (alert) {
                    alert.style.transition = 'opacity 0.5s';
                    alert.style.opacity = '0';
                    setTimeout(function () {
                        alert.remove();
                    }, 500);
                });
            }, 5000);

            // Validate score input
            document.querySelectorAll('.score-form').forEach(form => {
                form.addEventListener('submit', function (e) {
                    const scoreInput = this.querySelector('input[name="technicalScore"]');
                    const score = parseFloat(scoreInput.value);

                    if (isNaN(score) || score < 0 || score > 100) {
                        e.preventDefault();
                        alert('Please enter a valid score between 0 and 100.');
                    }
                });
            });
        </script>

    </body>
</html>