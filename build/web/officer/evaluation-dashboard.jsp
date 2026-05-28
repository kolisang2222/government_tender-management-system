<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bid Evaluation - ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
</head>
<body>

    <jsp:include page="/shared/navigation.jsp" />

    <div class="dashboard-layout">

        <jsp:include page="/shared/sidebar.jsp" />

        <main class="dashboard-main">

            <div class="page-header">
                <a href="${pageContext.request.contextPath}/officer/tenders" class="back-link">← Back to Tenders</a>
                <h1>Bid Evaluation</h1>
                <p>Tender: <strong>${tender.referenceNumber}</strong> - ${tender.title}</p>
            </div>

            <!-- Success/Error Messages -->
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success">
                    <span>✅</span>
                    <span>${sessionScope.successMessage}</span>
                    <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                </div>
                <c:remove var="successMessage" scope="session" />
            </c:if>

            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="alert alert-error">
                    <span>❌</span>
                    <span>${sessionScope.errorMessage}</span>
                    <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                </div>
                <c:remove var="errorMessage" scope="session" />
            </c:if>

            <!-- Results Link if Evaluation Complete -->
            <c:if test="${evaluationComplete}">
                <div class="alert alert-info">
                    🎉 <strong>Evaluation Complete!</strong> All evaluators have submitted their scores.
                    <a href="${pageContext.request.contextPath}/officer/start-evaluation?action=results&id=${tender.tenderId}">
                        Click here to view final results and ranked bids →
                    </a>
                </div>
            </c:if>

            <!-- View Results Button -->
            <div>
                <a href="${pageContext.request.contextPath}/officer/start-evaluation?action=results&id=${tender.tenderId}" 
                   class="btn btn-secondary">📊 View Evaluation Results</a>
            </div>

            <!-- Bids Table -->
            <h3>Submitted Bids</h3>

            <c:choose>
                <c:when test="${not empty bids}">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Supplier</th>
                                <th>Bid Amount</th>
                                <th>Proposed Timeline</th>
                                <th>Submitted Date</th>
                                <th>Your Score Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="bid" items="${bids}">
                                <tr>
                                    <td>${bid.supplierName}</td
                                    <td>${bid.formattedBidAmount}</td
                                    <td>${bid.proposedTimelineDays} days</td
                                    <td>${bid.formattedSubmittedAt}</td
                                    <td>
                                        <c:choose>
                                            <c:when test="${bid.hasCurrentUserScored}">
                                                <span class="badge badge-success">✓ Scored</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge badge-warning">⏳ Pending</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td
                                    <td>
                                        <c:choose>
                                            <c:when test="${bid.hasCurrentUserScored}">
                                                <a href="${pageContext.request.contextPath}/officer/start-evaluation?action=view&id=${tender.tenderId}&bidId=${bid.bidId}" 
                                                   class="btn btn-sm btn-info">View Score</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/officer/start-evaluation?action=view&id=${tender.tenderId}&bidId=${bid.bidId}" 
                                                   class="btn btn-sm btn-primary">Score Bid</a>
                                            </c:otherwise>
                                        </c:choose>
                                    </td
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <div class="empty-state">
                        <p>No bids have been submitted for this tender.</p>
                    </div>
                </c:otherwise>
            </c:choose>

        </main>
    </div>

    <jsp:include page="/shared/footer.jsp" />

</body>
</html>