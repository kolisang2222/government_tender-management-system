<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Evaluation Results - ProcureGov</title>
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
                <h1>Evaluation Results</h1>
                <p>Tender: <strong>${tender.referenceNumber}</strong> - ${tender.title}</p>
            </div>

            <!-- Completion Status -->
            <c:choose>
                <c:when test="${allEvaluationsComplete}">
                    <div class="alert alert-success">
                        ✅ All evaluators have completed scoring. The tender has been moved to EVALUATED status.
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-info">
                        ⏳ Evaluation in progress: ${completedEvaluators} of ${totalEvaluators} evaluators have completed scoring.
                    </div>
                </c:otherwise>
            </c:choose>

            <!-- Ranked Bids Table -->
            <div class="card">
                <h3>Ranked Bids (by Final Score)</h3>
                
                <c:choose>
                    <c:when test="${not empty rankedBids}">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Rank</th>
                                    <th>Supplier</th>
                                    <th>Bid Amount</th>
                                    <th>Proposed Timeline</th>
                                    <th>Final Score</th>
                                    <c:if test="${tender.status == 'EVALUATED'}">
                                        <th>Action</th>
                                    </c:if>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="bid" items="${rankedBids}">
                                    <tr class="${bid.rank == 1 ? 'winner-row' : ''}">
                                        <td>
                                            <c:choose>
                                                <c:when test="${bid.rank == 1}">🥇 1st</c:when>
                                                <c:when test="${bid.rank == 2}">🥈 2nd</c:when>
                                                <c:when test="${bid.rank == 3}">🥉 3rd</c:when>
                                                <c:otherwise>${bid.rank}th</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${bid.supplierName}</td>
                                        <td>${bid.formattedBidAmount}</td>
                                        <td>${bid.proposedTimelineDays} days</td>
                                        <td>
                                            <strong>${bid.formattedFinalScore}%</strong>
                                        </td>
                                        <c:if test="${tender.status == 'EVALUATED'}">
                                            <td>
                                                <a href="${pageContext.request.contextPath}/officer/award-tender?id=${tender.tenderId}&bidId=${bid.bidId}"
                                                   class="btn btn-sm btn-success">Select for Award</a>
                                            </td>
                                        </c:if>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <p>No evaluation scores available yet.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Evaluation Summary -->
            <div class="card">
                <h3>Evaluation Summary</h3>
                <div class="stats-grid">
                    <div class="stat-box">
                        <span class="stat-label">Total Bids Evaluated:</span>
                        <span class="stat-value">${rankedBids.size()}</span>
                    </div>
                    <div class="stat-box">
                        <span class="stat-label">Number of Evaluators:</span>
                        <span class="stat-value">${totalEvaluators}</span>
                    </div>
                    <div class="stat-box">
                        <span class="stat-label">Evaluators Completed:</span>
                        <span class="stat-value">${completedEvaluators}</span>
                    </div>
                    <div class="stat-box">
                        <span class="stat-label">Tender Status:</span>
                        <span class="stat-value">${tender.status.displayName}</span>
                    </div>
                </div>
            </div>

            <div>
                <a href="${pageContext.request.contextPath}/officer/start-evaluation?id=${tender.tenderId}" 
                   class="btn btn-secondary">Back to Evaluation Dashboard</a>
            </div>

        </main>
    </div>

    <jsp:include page="/shared/footer.jsp" />

</body>
</html>