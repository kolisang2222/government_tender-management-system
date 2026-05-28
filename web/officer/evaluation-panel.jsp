<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Score Bid - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <div class="page-header">
                    <a href="${pageContext.request.contextPath}/officer/start-evaluation?id=${tender.tenderId}" class="back-link">← Back to Evaluation Dashboard</a>
                    <h1>Score Bid</h1>
                    <p>Tender: <strong>${tender.referenceNumber}</strong> - ${tender.title}</p>
                </div>

                <c:if test="${not empty sessionScope.errorMessage}">
                    <div class="alert alert-error">
                        <span>❌</span>
                        <span>${sessionScope.errorMessage}</span>
                        <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                    </div>
                    <c:remove var="errorMessage" scope="session" />
                </c:if>

                <c:if test="${hasScored}">
                    <div class="alert alert-warning">
                        ⚠️ You have already scored this bid. Your scores are shown below.
                    </div>
                </c:if>

                <!-- Bid Information -->
                <div class="card">
                    <h3>Bid Information</h3>
                    <div class="info-grid">
                        <div class="info-item">
                            <label>Supplier:</label>
                            <span>${bid.supplierName}</span>
                        </div>
                        <div class="info-item">
                            <label>Bid Amount:</label>
                            <span>${bid.formattedBidAmount}</span>
                        </div>
                        <div class="info-item">
                            <label>Proposed Timeline:</label>
                            <span>${bid.proposedTimelineDays} days</span>
                        </div>
                        <div class="info-item">
                            <label>Technical Statement:</label>
                            <span>${bid.technicalComplianceStatement}</span>
                        </div>
                    </div>
                </div>

                <!-- Score Calculation Info -->
                <div class="card">
                    <h3>Automatic Score Calculation</h3>
                    <div class="info-grid">
                        <div class="info-item">
                            <label>Lowest Bid Amount:</label>
                            <span>M ${lowestBid}</span>
                        </div>
                        <div class="info-item">
                            <label>Your Price Score:</label>
                            <span>${priceScore}% × 40% = ${priceScore * 0.4}%</span>
                        </div>
                        <div class="info-item">
                            <label>Shortest Timeline:</label>
                            <span>${shortestTimeline} days</span>
                        </div>
                        <div class="info-item">
                            <label>Your Timeline Score:</label>
                            <span>${timelineScore}% × 25% = ${timelineScore * 0.25}%</span>
                        </div>
                    </div>
                </div>

                <!-- Score Form -->
                <div class="card">
                    <h3>Enter Technical Score</h3>

                    <form action="${pageContext.request.contextPath}/officer/start-evaluation" method="POST">
                        <input type="hidden" name="action" value="submitScores">
                        <input type="hidden" name="tenderId" value="${tender.tenderId}">
                        <input type="hidden" name="bidId" value="${bid.bidId}">

                        <div class="form-group">
                            <label for="technicalScore">Technical Compliance Score (0-100):</label>
                            <input type="number" id="technicalScore" name="technicalScore" 
                                   min="0" max="100" step="1" required
                                   value="${existingScore.technicalComplianceScore}"
                                   ${hasScored ? 'readonly disabled' : ''}>
                            <small>Rate the technical compliance of this bid from 0 to 100</small>
                        </div>

                        <c:if test="${hasScored}">
                            <div class="form-group">
                                <label>Your Weighted Total Score:</label>
                                <div class="score-display">
                                    <strong>${existingScore.weightedTotal}%</strong>
                                </div>
                            </div>
                        </c:if>

                        <div class="form-actions">
                            <c:if test="${not hasScored}">
                                <button type="submit" class="btn btn-primary">Submit Scores</button>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/officer/start-evaluation?id=${tender.tenderId}" 
                               class="btn btn-secondary">Cancel</a>
                        </div>
                    </form>
                </div>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

    </body>
</html>