<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Award Notice - ProcureGov</title>
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
                    <h1>Award Notice</h1>
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

                <!-- Award Notice Content -->
                <c:if test="${not empty tender}">
                    <div class="award-notice-container">

                        <!-- Ministry Header -->
                        <div class="notice-header">
                            <div class="ministry-logo">
                                <h2>MINISTRY OF PUBLIC WORKS</h2>
                                <h3>KINGDOM OF LESOTHO</h3>
                            </div>
                            <div class="notice-title">
                                <h1>CONTRACT AWARD NOTICE</h1>
                                <p>Reference: ${tender.referenceNumber}</p>
                            </div>
                        </div>

                        <!-- Notice Body -->
                        <div class="notice-body">

                            <!-- Tender Information -->
                            <div class="notice-section">
                                <h3>1. Tender Information</h3>
                                <div class="info-grid">
                                    <div class="info-item">
                                        <label>Tender Reference:</label>
                                        <span>${tender.referenceNumber}</span>
                                    </div>
                                    <div class="info-item">
                                        <label>Tender Title:</label>
                                        <span>${tender.title}</span>
                                    </div>
                                    <div class="info-item">
                                        <label>Tender Category:</label>
                                        <span>${tender.category.displayName}</span>
                                    </div>
                                    <div class="info-item">
                                        <label>Estimated Value:</label>
                                        <span>${tender.formattedEstimatedValue}</span>
                                    </div>
                                    <div class="info-item">
                                        <label>Closing Date:</label>
                                        <span>${tender.closingDateFormatted}</span>
                                    </div>
                                    <div class="info-item">
                                        <label>Tender Status:</label>
                                        <span class="status-badge status-awarded">Awarded</span>
                                    </div>
                                </div>
                            </div>

                            <!-- Awarded Supplier Information -->
                            <div class="notice-section highlight">
                                <h3>2. Awarded Supplier</h3>
                                <div class="info-grid">
                                    <div class="info-item">
                                        <label>Supplier Name:</label>
                                        <span class="highlight-text">${tender.winningSupplierName}</span>
                                    </div>
                                    <div class="info-item">
                                        <label>Awarded Amount:</label>
                                        <span class="amount">${tender.formattedAwardedValue}</span>
                                    </div>
                                    <div class="info-item">
                                        <label>Award Date:</label>
                                        <span>${tender.awardDateFormatted}</span>
                                    </div>
                                    <div class="info-item">
                                        <label>Award Justification:</label>
                                        <span>${tender.awardJustification}</span>
                                    </div>
                                </div>
                            </div>

                            <!-- Evaluation Summary -->
                            <div class="notice-section">
                                <h3>3. Evaluation Summary</h3>
                                <div class="info-grid">
                                    <div class="info-item">
                                        <label>Total Bids Received:</label>
                                        <span>${tender.bidCount}</span>
                                    </div>
                                    <div class="info-item">
                                        <label>Number of Evaluators:</label>
                                        <span>${totalEvaluators}</span>
                                    </div>
                                    <div class="info-item">
                                        <label>Evaluation Completed:</label>
                                        <span>${tender.evaluationCompletedDateFormatted}</span>
                                    </div>
                                </div>
                            </div>

                            <!-- Top Bids Comparison Table -->
                            <c:if test="${not empty rankedBids}">
                                <div class="notice-section">
                                    <h3>4. Bid Ranking (Top 5)</h3>
                                    <table class="ranking-table">
                                        <thead>
                                            <tr>
                                                <th>Rank</th>
                                                <th>Supplier</th>
                                                <th>Bid Amount</th>
                                                <th>Technical Score</th>
                                                <th>Price Score</th>
                                                <th>Timeline Score</th>
                                                <th>Final Score</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="bid" items="${rankedBids}" begin="0" end="4">
                                                <tr class="${bid.rank == 1 ? 'winner-row' : ''}">
                                                    <td class="rank-cell">
                                                        <c:choose>
                                                            <c:when test="${bid.rank == 1}">🏆 1st</c:when>
                                                            <c:when test="${bid.rank == 2}">🥈 2nd</c:when>
                                                            <c:when test="${bid.rank == 3}">🥉 3rd</c:when>
                                                            <c:otherwise>${bid.rank}th</c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>${bid.supplierName}</td>
                                                    <td>${bid.formattedBidAmount}</td>
                                                    <td class="score-cell">${bid.technicalScore}%</td>
                                                    <td class="score-cell">${bid.priceScore}%</td>
                                                    <td class="score-cell">${bid.timelineScore}%</td>
                                                    <td class="score-cell winner-score"><strong>${bid.formattedFinalScore}%</strong></td>
                                        </table>
                                    </c:forEach>
                                    </tbody>
                                    </table>
                                </div>
                            </c:if>

                            <!-- Footer Signatures -->
                            <div class="notice-footer">
                                <div class="signature-section">
                                    <div class="signature-line">
                                        <p>_________________________</p>
                                        <p>Procurement Officer</p>
                                        <p>Ministry of Public Works</p>
                                    </div>
                                    <div class="signature-line">
                                        <p>_________________________</p>
                                        <p>Director of ICT</p>
                                        <p>Ministry of Public Works</p>
                                    </div>
                                    <div class="signature-line">
                                        <p>_________________________</p>
                                        <p>Winning Supplier</p>
                                        <p>Date: _________________</p>
                                    </div>
                                </div>
                                <div class="notice-stamp">
                                    <p>OFFICIAL SEAL</p>
                                    <p>Date: ${currentDate}</p>
                                </div>
                            </div>

                            <!-- Action Buttons -->
                            <div class="action-buttons">
                                <button onclick="window.print()" class="btn btn-primary">🖨️ Print Award Notice</button>
                                <button onclick="downloadPDF()" class="btn btn-secondary">📥 Download PDF</button>
                                <a href="${pageContext.request.contextPath}/officer/send-award-notification?id=${tender.tenderId}" 
                                   class="btn btn-success" 
                                   onclick="return confirm('Send email notifications to all bidding suppliers?')">
                                    📧 Send Notifications
                                </a>
                                <a href="${pageContext.request.contextPath}/officer/tenders" class="btn btn-outline">Back to Tenders</a>
                            </div>

                        </div>
                    </div>
                </c:if>

                <!-- If tender not found -->
                <c:if test="${empty tender}">
                    <div class="empty-state">
                        <div class="empty-icon">📄</div>
                        <h3>Award Notice Not Found</h3>
                        <p>The requested award notice could not be found.</p>
                        <a href="${pageContext.request.contextPath}/officer/tenders" class="btn btn-primary">Back to Tenders</a>
                    </div>
                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <style>
            .award-notice-container {
                background: white;
                border-radius: 8px;
                padding: 30px;
                margin-bottom: 20px;
                box-shadow: 0 2px 4px rgba(0,0,0,0.1);
            }

            .notice-header {
                text-align: center;
                border-bottom: 2px solid #00529B;
                padding-bottom: 20px;
                margin-bottom: 25px;
            }

            .notice-header h2 {
                color: #00529B;
                margin: 0;
                font-size: 24px;
            }

            .notice-header h3 {
                margin: 5px 0 0 0;
                font-size: 16px;
                color: #666;
            }

            .notice-title h1 {
                color: #00529B;
                margin: 20px 0 5px 0;
                font-size: 28px;
            }

            .notice-title p {
                font-size: 14px;
                color: #666;
            }

            .notice-section {
                margin-bottom: 25px;
                padding: 15px;
                background: #f9f9f9;
                border-radius: 8px;
            }

            .notice-section.highlight {
                background: #e8f4fd;
                border-left: 4px solid #00529B;
            }

            .notice-section h3 {
                color: #00529B;
                margin-top: 0;
                margin-bottom: 15px;
                font-size: 18px;
                border-bottom: 1px solid #ddd;
                padding-bottom: 8px;
            }

            .info-grid {
                display: grid;
                grid-template-columns: repeat(2, 1fr);
                gap: 15px;
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

            .info-item span {
                font-size: 14px;
                font-weight: 500;
            }

            .highlight-text {
                color: #00529B;
                font-weight: bold;
                font-size: 16px;
            }

            .amount {
                font-size: 16px;
                font-weight: bold;
                color: #28a745;
            }

            .ranking-table {
                width: 100%;
                border-collapse: collapse;
                font-size: 13px;
            }

            .ranking-table th {
                background: #00529B;
                color: white;
                padding: 10px;
                text-align: left;
            }

            .ranking-table td {
                padding: 8px 10px;
                border-bottom: 1px solid #e0e0e0;
            }

            .ranking-table tr:hover {
                background: #f5f5f5;
            }

            .winner-row {
                background: #d4edda;
            }

            .score-cell {
                text-align: center;
            }

            .winner-score {
                font-size: 16px;
                color: #00529B;
            }

            .notice-footer {
                margin-top: 30px;
                padding-top: 20px;
                border-top: 1px solid #ddd;
            }

            .signature-section {
                display: flex;
                justify-content: space-between;
                margin-bottom: 20px;
                flex-wrap: wrap;
            }

            .signature-line {
                text-align: center;
                width: 30%;
            }

            .signature-line p {
                margin: 5px 0;
                font-size: 12px;
            }

            .notice-stamp {
                text-align: center;
                padding: 10px;
                border: 1px dashed #00529B;
                width: 200px;
                margin: 0 auto;
            }

            .action-buttons {
                display: flex;
                gap: 15px;
                margin-top: 25px;
                justify-content: center;
                flex-wrap: wrap;
            }

            .status-badge.status-awarded {
                background: #d4edda;
                color: #155724;
                padding: 4px 10px;
                border-radius: 20px;
                font-size: 12px;
                display: inline-block;
            }

            .rank-cell {
                font-weight: bold;
            }

            .empty-state {
                text-align: center;
                padding: 50px;
                background: white;
                border-radius: 8px;
            }

            .empty-icon {
                font-size: 48px;
                margin-bottom: 15px;
            }

            @media print {
                .dashboard-layout {
                    padding: 0;
                    margin: 0;
                }
                .sidebar, .navigation, .back-link, .action-buttons, .alert {
                    display: none;
                }
                .award-notice-container {
                    box-shadow: none;
                    padding: 0;
                }
                .notice-section {
                    break-inside: avoid;
                }
            }
        </style>

        <script>
            function downloadPDF() {
                window.print();
            }

            // Close alert messages after 5 seconds
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
        </script>

    </body>
</html>