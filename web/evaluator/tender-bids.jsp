<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Score Bids - ${tender.referenceNumber}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
    <style>
        .data-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }
        .data-table th, .data-table td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }
        .data-table th {
            background-color: #00529B;
            color: white;
        }
        .btn-sm {
            padding: 5px 10px;
            font-size: 12px;
            border-radius: 3px;
            text-decoration: none;
            display: inline-block;
        }
        .btn-primary {
            background-color: #00529B;
            color: white;
        }
        .btn-info {
            background-color: #17a2b8;
            color: white;
        }
        .badge-success {
            background-color: #28a745;
            color: white;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 11px;
        }
        .badge-warning {
            background-color: #ffc107;
            color: #333;
            padding: 3px 8px;
            border-radius: 3px;
            font-size: 11px;
        }
        .progress-bar-container {
            background-color: #e0e0e0;
            border-radius: 10px;
            height: 25px;
            margin: 10px 0;
            overflow: hidden;
        }
        .progress-bar {
            background-color: #00529B;
            height: 100%;
            color: white;
            text-align: center;
            line-height: 25px;
            border-radius: 10px;
        }
        .info-box {
            background-color: #e8f4f8;
            border-left: 4px solid #00529B;
            padding: 15px;
            margin: 20px 0;
        }
        .back-link {
            text-decoration: none;
            color: #00529B;
            margin-bottom: 15px;
            display: inline-block;
        }
    </style>
</head>
<body>

    <jsp:include page="/shared/navigation.jsp" />

    <div class="dashboard-layout">
        
        <jsp:include page="/shared/sidebar.jsp" />

        <main class="dashboard-main">
            
            <div class="page-header">
                <a href="${pageContext.request.contextPath}/evaluator/score-bids" class="back-link">← Back to Available Tenders</a>
                <h1>Score Bids</h1>
                <p>Tender: <strong>${tender.referenceNumber}</strong> - ${tender.title}</p>
            </div>

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

            <!-- Progress Section -->
            <div class="info-box">
                <h3>Your Scoring Progress</h3>
                <div class="progress-bar-container">
                    <div class="progress-bar" style="width: ${progressPercent}%;">
                        ${progressPercent}% Complete (${scoredBids} of ${totalBids} bids scored)
                    </div>
                </div>
            </div>

            <!-- Weightage Info -->
            <div class="info-box">
                <h4>Scoring Criteria Weightage</h4>
                <ul>
                    <li><strong>Price Score:</strong> 40% - Calculated automatically: (Lowest Bid / This Bid) × 100</li>
                    <li><strong>Technical Compliance:</strong> 35% - You enter a score from 0-100</li>
                    <li><strong>Delivery Timeline:</strong> 25% - Calculated automatically: (Shortest Timeline / This Timeline) × 100</li>
                </ul>
                <p><strong>Lowest Bid for this tender:</strong> M ${lowestBid}</p>
                <p><strong>Shortest Timeline for this tender:</strong> ${shortestTimeline} days</p>
            </div>

            <c:choose>
                <c:when test="${not empty bids}">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Supplier</th>
                                <th>Bid Amount</th>
                                <th>Timeline</th>
                                <th>Auto Price Score (40%)</th>
                                <th>Auto Timeline Score (25%)</th>
                                <th>Status</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="bid" items="${bids}">
                                <tr>
                                    <td>${bid.supplierName}</td
                                    <td>${bid.formattedBidAmount}</td
                                    <td>${bid.proposedTimelineDays} days</td
                                    <td>${bid.finalScore}%</td
                                    <td>${bid.rank}%</td
                                    <td>
                                        <c:choose>
                                            <c:when test="${bid.hasCurrentUserScored}">
                                                <span class="badge-success">✓ Scored</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge-warning">Pending</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td
                                    <td>
                                        <c:choose>
                                            <c:when test="${bid.hasCurrentUserScored}">
                                                <a href="${pageContext.request.contextPath}/evaluator/score-bids?action=score&tenderId=${tender.tenderId}&bidId=${bid.bidId}" 
                                                   class="btn-sm btn-info">View Score</a>
                                            </c:when>
                                            <c:otherwise>
                                                <a href="${pageContext.request.contextPath}/evaluator/score-bids?action=score&tenderId=${tender.tenderId}&bidId=${bid.bidId}" 
                                                   class="btn-sm btn-primary">Score Now</a>
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