<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>View Scores - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/evaluatordashboard.css">
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <div class="page-header">
                    <a href="${pageContext.request.contextPath}/evaluator/dashboard" class="back-link">← Back to Dashboard</a>
                    <h1>My Evaluation Scores</h1>
                    <p>Tender: ${tender.referenceNumber} - ${tender.title}</p>
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

                <!-- Weightage Information -->
                <div class="card weightage-card">
                    <h3>Evaluation Criteria Weightage</h3>
                    <div class="weightage-grid">
                        <div class="weightage-item">
                            <span class="weightage-label">Price Score</span>
                            <span class="weightage-value">40%</span>
                            <span class="weightage-desc">Automatically calculated</span>
                        </div>
                        <div class="weightage-item">
                            <span class="weightage-label">Technical Compliance</span>
                            <span class="weightage-value">35%</span>
                            <span class="weightage-desc">Entered by evaluator</span>
                        </div>
                        <div class="weightage-item">
                            <span class="weightage-label">Delivery Timeline</span>
                            <span class="weightage-value">25%</span>
                            <span class="weightage-desc">Automatically calculated</span>
                        </div>
                    </div>
                </div>

                <!-- Scores Table -->
                <div class="card">
                    <h3>My Submitted Scores</h3>

                    <c:choose>
                        <c:when test="${not empty scores}">
                            <table class="scores-table">
                                <thead>
                                    <tr>
                                        <th>Supplier</th>
                                        <th>Bid Amount</th>
                                        <th>Price Score (40%)</th>
                                        <th>Technical Score (35%)</th>
                                        <th>Timeline Score (25%)</th>
                                        <th>Weighted Total</th>
                                        <th>Submitted On</th>
                                        <th>Status</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="score" items="${scores}">
                                        <tr>
                                            <td class="supplier-cell">
                                                <strong>${score.supplierName}</strong>
                                            </td>
                                            <td>${score.formattedBidAmount}</td>
                                            <td class="score-cell">
                                                ${score.formattedPriceScore}%
                                                <div class="score-weight">× 0.40 = ${score.priceScore * 0.4}%</div>
                                            </td>
                                            <td class="score-cell">
                                                ${score.formattedTechnicalScore}%
                                                <div class="score-weight">× 0.35 = ${score.technicalComplianceScore * 0.35}%</div>
                                            </td>
                                            <td class="score-cell">
                                                ${score.formattedTimelineScore}%
                                                <div class="score-weight">× 0.25 = ${score.timelineScore * 0.25}%</div>
                                            </td>
                                            <td class="total-cell">
                                                <strong>${score.formattedWeightedTotal}%</strong>
                                            </td>
                                            <td>${score.submittedAtFormatted}</td>
                                            <td>
                                                <span class="badge badge-success">Submitted</span>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                                <tfoot>
                                    <tr class="average-row">
                                        <td colspan="5" class="average-label"><strong>Your Average Score</strong></td>
                                        <td class="average-value"><strong>${averageScore}%</strong></td>
                                        <td colspan="2"></td>
                                    </tr>
                                </tfoot>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-icon">📊</div>
                                <h3>No Scores Submitted Yet</h3>
                                <p>You haven't submitted any evaluation scores for this tender yet.</p>
                                <c:if test="${tender.status == 'UNDER_EVALUATION'}">
                                    <a href="${pageContext.request.contextPath}/evaluator/start-evaluation?id=${tender.tenderId}" 
                                       class="btn btn-primary">Start Evaluation</a>
                                </c:if>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- All Scores Summary (if evaluation complete) -->
                <c:if test="${not empty consolidatedScores}">
                    <div class="card">
                        <h3>Consolidated Results (All Evaluators)</h3>
                        <table class="consolidated-table">
                            <thead>
                                <tr>
                                    <th>Rank</th>
                                    <th>Supplier</th>
                                    <th>Average Score</th>
                                    <th>Status</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="bid" items="${consolidatedScores}" varStatus="loop">
                                    <tr class="${loop.index == 0 ? 'winner-row' : ''}">
                                        <td>
                                            <c:choose>
                                                <c:when test="${loop.index == 0}">🥇 1st</c:when>
                                                <c:when test="${loop.index == 1}">🥈 2nd</c:when>
                                                <c:when test="${loop.index == 2}">🥉 3rd</c:when>
                                                <c:otherwise>${loop.index + 1}th</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${bid.supplierName}</td>
                                        <td class="score-cell"><strong>${bid.formattedFinalScore}%</strong></td>
                                        <td>
                                            <c:if test="${loop.index == 0}">
                                                <span class="badge badge-winner">Leading</span>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>

                <!-- Action Buttons -->
                <div class="action-buttons">
                    <c:if test="${tender.status == 'UNDER_EVALUATION'}">
                        <a href="${pageContext.request.contextPath}/evaluator/start-evaluation?id=${tender.tenderId}" 
                           class="btn btn-primary">Continue Evaluation</a>
                    </c:if>
                    <a href="${pageContext.request.contextPath}/evaluator/dashboard" 
                       class="btn btn-secondary">Back to Dashboard</a>
                    <button onclick="window.print()" class="btn btn-outline">Print Scores</button>
                </div>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <style>
            .scores-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 14px;
            }

            .scores-table th {
                background: #00529B;
                color: white;
                padding: 12px;
                text-align: left;
            }

            .scores-table td {
                padding: 10px 12px;
                border-bottom: 1px solid #e0e0e0;
            }

            .scores-table tr:hover {
                background: #f5f5f5;
            }

            .supplier-cell {
                font-weight: 500;
            }

            .score-cell {
                text-align: center;
            }

            .score-weight {
                font-size: 10px;
                color: #888;
                margin-top: 2px;
            }

            .total-cell {
                text-align: center;
                font-weight: bold;
                color: #00529B;
                font-size: 16px;
            }

            .average-row {
                background: #e8f4fd;
                font-weight: bold;
            }

            .average-label {
                text-align: right;
            }

            .average-value {
                text-align: center;
                font-size: 18px;
                color: #00529B;
            }

            .consolidated-table {
                width: 100%;
                border-collapse: collapse;
            }

            .consolidated-table th {
                background: #343a40;
                color: white;
                padding: 10px;
            }

            .consolidated-table td {
                padding: 8px 10px;
                border-bottom: 1px solid #e0e0e0;
            }

            .winner-row {
                background: #d4edda;
            }

            .badge {
                display: inline-block;
                padding: 4px 10px;
                border-radius: 20px;
                font-size: 11px;
                font-weight: bold;
            }

            .badge-success {
                background: #d4edda;
                color: #155724;
            }

            .badge-winner {
                background: #ffc107;
                color: #856404;
            }

            .weightage-card {
                background: #f0f7ff;
            }

            .weightage-grid {
                display: flex;
                gap: 20px;
                margin-top: 15px;
            }

            .weightage-item {
                flex: 1;
                text-align: center;
                padding: 10px;
                background: white;
                border-radius: 8px;
            }

            .weightage-label {
                display: block;
                font-size: 12px;
                color: #666;
            }

            .weightage-value {
                display: block;
                font-size: 24px;
                font-weight: bold;
                color: #00529B;
            }

            .weightage-desc {
                display: block;
                font-size: 10px;
                color: #999;
            }

            .action-buttons {
                display: flex;
                gap: 15px;
                margin-top: 25px;
                justify-content: center;
            }

            @media print {
                .sidebar, .navigation, .action-buttons, .alert, .back-link {
                    display: none;
                }
                .scores-table {
                    font-size: 10px;
                }
            }

            .empty-state {
                text-align: center;
                padding: 50px;
                color: #666;
            }

            .empty-icon {
                font-size: 48px;
                margin-bottom: 15px;
            }

            .card {
                background: white;
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 20px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }
        </style>

    </body>
</html>