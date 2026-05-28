<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tender Details - ProcureGov</title>
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
                    <h1>Tender Details</h1>
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

                <c:if test="${not empty tender}">

                    <!-- Tender Status Banner -->
                    <div class="status-banner status-${tender.status.name().toLowerCase()}">
                        <div class="status-icon">
                            <c:choose>
                                <c:when test="${tender.status == 'OPEN'}">📢</c:when>
                                <c:when test="${tender.status == 'CLOSED'}">🔒</c:when>
                                <c:when test="${tender.status == 'UNDER_EVALUATION'}">📊</c:when>
                                <c:when test="${tender.status == 'EVALUATED'}">✅</c:when>
                                <c:when test="${tender.status == 'AWARDED'}">🏆</c:when>
                                <c:otherwise>📄</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="status-info">
                            <span class="status-label">Current Status:</span>
                            <span class="status-value">${tender.status.displayName}</span>
                        </div>
                        <c:if test="${tender.status == 'UNDER_EVALUATION' and not evaluationComplete}">
                            <div class="status-actions">
                                <a href="${pageContext.request.contextPath}/evaluator/start-evaluation?id=${tender.tenderId}" 
                                   class="btn btn-sm btn-primary">Start Scoring</a>
                            </div>
                        </c:if>
                        <c:if test="${tender.status == 'UNDER_EVALUATION' and evaluationComplete}">
                            <div class="status-actions">
                                <span class="badge badge-success">✓ All Scores Submitted</span>
                            </div>
                        </c:if>
                    </div>

                    <!-- Tender Information -->
                    <div class="card">
                        <h2>${tender.title}</h2>

                        <div class="info-grid">
                            <div class="info-item">
                                <label>Reference Number</label>
                                <span class="value">${tender.referenceNumber}</span>
                            </div>

                            <div class="info-item">
                                <label>Category</label>
                                <span class="value">${tender.category.displayName}</span>
                            </div>

                            <div class="info-item">
                                <label>Estimated Value</label>
                                <span class="value">${tender.formattedEstimatedValue}</span>
                            </div>

                            <div class="info-item">
                                <label>Closing Date</label>
                                <span class="value">${tender.formattedClosingDateTime}</span>
                            </div>

                            <div class="info-item">
                                <label>Published By</label>
                                <span class="value">${tender.createdByName}</span>
                            </div>

                            <div class="info-item">
                                <label>Published Date</label>
                                <span class="value">${tender.formattedCreatedAt}</span>
                            </div>
                        </div>

                        <div class="info-item full-width">
                            <label>Description</label>
                            <div class="description-box">${tender.description}</div>
                        </div>

                        <div class="info-item">
                            <label>Tender Notice</label>
                            <c:if test="${tender.hasTenderNotice}">
                                <a href="${pageContext.request.contextPath}/officer/download-tender?id=${tender.tenderId}" 
                                   class="btn-download">📄 Download Tender Notice</a>
                            </c:if>
                            <c:if test="${not tender.hasTenderNotice}">
                                <span class="text-muted">No document attached</span>
                            </c:if>
                        </div>
                    </div>

                    <!-- Bids Section (Visible when tender is closed or under evaluation) -->
                    <c:if test="${tender.status == 'CLOSED' or tender.status == 'UNDER_EVALUATION' or tender.status == 'EVALUATED' or tender.status == 'AWARDED'}">
                        <div class="card">
                            <h3>Submitted Bids (${bidCount})</h3>

                            <c:choose>
                                <c:when test="${not empty bids}">
                                    <table class="bids-table">
                                        <thead>
                                            <tr>
                                                <th>Supplier</th>
                                                <th>Bid Amount</th>
                                                <th>Proposed Timeline</th>
                                                <th>Technical Statement</th>
                                                <th>Submitted Date</th>
                                                    <c:if test="${tender.status == 'UNDER_EVALUATION'}">
                                                    <th>Action</th>
                                                    </c:if>
                                                    <c:if test="${tender.status == 'EVALUATED' or tender.status == 'AWARDED'}">
                                                    <th>Final Score</th>
                                                    </c:if>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="bid" items="${bids}">
                                                <tr>
                                                    <td class="supplier-cell">${bid.supplierName}</td>
                                                    <td>${bid.formattedBidAmount}</td>
                                                    <td>${bid.proposedTimelineDays} days</td>
                                                    <td class="technical-statement">${bid.technicalComplianceStatement}</td>
                                                    <td>${bid.formattedSubmittedAt}</td>

                                                    <c:if test="${tender.status == 'UNDER_EVALUATION'}">
                                                        <td>
                                                            <c:choose>
                                                                <c:when test="${bid.hasCurrentUserScored}">
                                                                    <a href="${pageContext.request.contextPath}/evaluator/start-evaluation?action=view&id=${tender.tenderId}&bidId=${bid.bidId}" 
                                                                       class="btn-sm btn-outline">View Score</a>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <a href="${pageContext.request.contextPath}/evaluator/start-evaluation?action=view&id=${tender.tenderId}&bidId=${bid.bidId}" 
                                                                       class="btn-sm btn-primary">Score Bid</a>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </td>
                                                    </c:if>

                                                    <c:if test="${tender.status == 'EVALUATED' or tender.status == 'AWARDED'}">
                                                        <td class="score-cell">
                                                            <strong>${bid.formattedFinalScore}%</strong>
                                                            <c:if test="${bid.isWinner}">
                                                                <span class="winner-badge">Winner</span>
                                                            </c:if>
                                                        </td>
                                                    </c:if>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:when>
                                <c:otherwise>
                                    <div class="empty-message">
                                        <p>No bids have been submitted for this tender.</p>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>

                    <!-- Evaluation Progress Section -->
                    <c:if test="${tender.status == 'UNDER_EVALUATION'}">
                        <div class="card">
                            <h3>Evaluation Progress</h3>
                            <div class="progress-stats">
                                <div class="stat-card">
                                    <span class="stat-value">${scoresSubmitted}</span>
                                    <span class="stat-label">Your Scores Submitted</span>
                                </div>
                                <div class="stat-card">
                                    <span class="stat-value">${totalBids}</span>
                                    <span class="stat-label">Total Bids to Score</span>
                                </div>
                                <div class="stat-card">
                                    <span class="stat-value">${completionPercentage}%</span>
                                    <span class="stat-label">Your Completion</span>
                                </div>
                            </div>
                            <div class="progress-bar-container">
                                <div class="progress-bar" style="width: ${completionPercentage}%;">
                                    ${completionPercentage}% Complete
                                </div>
                            </div>
                        </div>
                    </c:if>

                    <!-- Evaluation Results Section -->
                    <c:if test="${tender.status == 'EVALUATED' or tender.status == 'AWARDED'}">
                        <div class="card">
                            <h3>Evaluation Results</h3>

                            <c:if test="${tender.status == 'AWARDED'}">
                                <div class="award-info">
                                    <h4>🏆 Contract Awarded</h4>
                                    <div class="award-details">
                                        <div class="award-item">
                                            <label>Winning Supplier:</label>
                                            <span class="winner-name">${tender.winningSupplierName}</span>
                                        </div>
                                        <div class="award-item">
                                            <label>Awarded Amount:</label>
                                            <span>${tender.formattedAwardedValue}</span>
                                        </div>
                                        <div class="award-item">
                                            <label>Award Date:</label>
                                            <span>${tender.awardDateFormatted}</span>
                                        </div>
                                        <div class="award-item">
                                            <label>Justification:</label>
                                            <span>${tender.awardJustification}</span>
                                        </div>
                                    </div>
                                </div>
                            </c:if>

                            <c:if test="${not empty rankedBids}">
                                <h4>Ranked Bids (by Final Score)</h4>
                                <table class="ranked-table">
                                    <thead>
                                        <tr>
                                            <th>Rank</th>
                                            <th>Supplier</th>
                                            <th>Bid Amount</th>
                                            <th>Final Score</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="bid" items="${rankedBids}" varStatus="loop">
                                            <tr class="${loop.index == 0 ? 'winner-row' : ''}">
                                                <td class="rank-cell">
                                                    <c:choose>
                                                        <c:when test="${loop.index == 0}">🏆 1st</c:when>
                                                        <c:when test="${loop.index == 1}">🥈 2nd</c:when>
                                                        <c:when test="${loop.index == 2}">🥉 3rd</c:when>
                                                        <c:otherwise>${loop.index + 1}th</c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>${bid.supplierName}</td>
                                                <td>${bid.formattedBidAmount}</td>
                                                <td class="score-cell"><strong>${bid.formattedFinalScore}%</strong></td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:if>
                        </div>
                    </c:if>

                    <!-- Action Buttons -->
                    <div class="action-buttons">
                        <c:if test="${tender.status == 'UNDER_EVALUATION'}">
                            <a href="${pageContext.request.contextPath}/evaluator/start-evaluation?id=${tender.tenderId}" 
                               class="btn btn-primary">Go to Evaluation Panel</a>
                        </c:if>
                        <c:if test="${tender.status == 'EVALUATED' or tender.status == 'AWARDED'}">
                            <a href="${pageContext.request.contextPath}/evaluator/view-scores?id=${tender.tenderId}" 
                               class="btn btn-primary">View My Scores</a>
                            <a href="${pageContext.request.contextPath}/evaluator/award-notice?id=${tender.tenderId}" 
                               class="btn btn-success">View Award Notice</a>
                        </c:if>
                        <a href="${pageContext.request.contextPath}/evaluator/dashboard" 
                           class="btn btn-secondary">Back to Dashboard</a>
                        <button onclick="window.print()" class="btn btn-outline">Print Details</button>
                    </div>

                </c:if>

                <!-- Tender Not Found -->
                <c:if test="${empty tender}">
                    <div class="empty-state">
                        <div class="empty-icon">📄</div>
                        <h3>Tender Not Found</h3>
                        <p>The requested tender could not be found.</p>
                        <a href="${pageContext.request.contextPath}/evaluator/dashboard" class="btn btn-primary">Back to Dashboard</a>
                    </div>
                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <style>
            .status-banner {
                display: flex;
                align-items: center;
                gap: 15px;
                padding: 15px 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                background: #f8f9fa;
                border-left: 4px solid;
            }

            .status-banner.status-draft {
                border-left-color: #6c757d;
                background: #f8f9fa;
            }
            .status-banner.status-open {
                border-left-color: #28a745;
                background: #d4edda;
            }
            .status-banner.status-closed {
                border-left-color: #dc3545;
                background: #f8d7da;
            }
            .status-banner.status-under_evaluation {
                border-left-color: #ffc107;
                background: #fff3cd;
            }
            .status-banner.status-evaluated {
                border-left-color: #17a2b8;
                background: #d1ecf1;
            }
            .status-banner.status-awarded {
                border-left-color: #00529B;
                background: #e8f4fd;
            }

            .status-icon {
                font-size: 28px;
            }

            .status-info {
                flex: 1;
            }

            .status-label {
                font-size: 12px;
                color: #666;
                display: block;
            }

            .status-value {
                font-size: 18px;
                font-weight: bold;
            }

            .card {
                background: white;
                border-radius: 8px;
                padding: 20px;
                margin-bottom: 20px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }

            .info-grid {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 15px;
                margin-top: 15px;
            }

            .info-item {
                display: flex;
                flex-direction: column;
            }

            .info-item label {
                font-size: 12px;
                color: #666;
                margin-bottom: 3px;
            }

            .info-item .value {
                font-size: 14px;
                font-weight: 500;
            }

            .full-width {
                grid-column: span 2;
            }

            .description-box {
                background: #f9f9f9;
                padding: 12px;
                border-radius: 6px;
                margin-top: 5px;
                line-height: 1.5;
            }

            .bids-table, .ranked-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 13px;
            }

            .bids-table th, .ranked-table th {
                background: #00529B;
                color: white;
                padding: 10px;
                text-align: left;
            }

            .bids-table td, .ranked-table td {
                padding: 8px 10px;
                border-bottom: 1px solid #e0e0e0;
            }

            .bids-table tr:hover, .ranked-table tr:hover {
                background: #f5f5f5;
            }

            .supplier-cell {
                font-weight: 500;
            }

            .technical-statement {
                max-width: 250px;
                word-wrap: break-word;
            }

            .score-cell {
                text-align: center;
                font-weight: bold;
            }

            .winner-badge {
                display: inline-block;
                background: #ffc107;
                color: #856404;
                padding: 2px 8px;
                border-radius: 12px;
                font-size: 10px;
                margin-left: 8px;
            }

            .btn-sm {
                padding: 5px 12px;
                font-size: 12px;
                border-radius: 4px;
                text-decoration: none;
            }

            .btn-sm.btn-primary {
                background: #00529B;
                color: white;
            }

            .btn-sm.btn-outline {
                border: 1px solid #00529B;
                color: #00529B;
                background: transparent;
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

            .progress-stats {
                display: flex;
                gap: 20px;
                margin-bottom: 15px;
            }

            .stat-card {
                flex: 1;
                background: #f8f9fa;
                padding: 15px;
                border-radius: 8px;
                text-align: center;
            }

            .stat-value {
                display: block;
                font-size: 28px;
                font-weight: bold;
                color: #00529B;
            }

            .stat-label {
                display: block;
                font-size: 12px;
                color: #666;
                margin-top: 5px;
            }

            .progress-bar-container {
                background: #e0e0e0;
                border-radius: 10px;
                height: 30px;
                overflow: hidden;
            }

            .progress-bar {
                background: #00529B;
                height: 100%;
                color: white;
                text-align: center;
                line-height: 30px;
                font-size: 14px;
                font-weight: bold;
                transition: width 0.3s ease;
            }

            .award-info {
                background: #e8f4fd;
                padding: 15px;
                border-radius: 8px;
                margin-bottom: 20px;
            }

            .award-info h4 {
                margin: 0 0 10px 0;
                color: #00529B;
            }

            .award-details {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 10px;
            }

            .award-item label {
                font-size: 11px;
                color: #666;
                display: block;
            }

            .winner-name {
                font-weight: bold;
                color: #00529B;
                font-size: 16px;
            }

            .rank-cell {
                font-weight: bold;
            }

            .winner-row {
                background: #d4edda;
            }

            .action-buttons {
                display: flex;
                gap: 15px;
                margin-top: 10px;
                justify-content: center;
                flex-wrap: wrap;
            }

            .btn-download {
                display: inline-block;
                padding: 6px 12px;
                background: #6c757d;
                color: white;
                text-decoration: none;
                border-radius: 4px;
                font-size: 13px;
            }

            .empty-message {
                text-align: center;
                padding: 30px;
                color: #666;
            }

            .text-muted {
                color: #999;
            }

            @media print {
                .sidebar, .navigation, .action-buttons, .back-link, .status-actions {
                    display: none;
                }
            }
        </style>

    </body>
</html>