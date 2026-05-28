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
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/supplierdashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/award-notice.css">
        <link rel="stylesheet" href="procgov-dashboard.css">
        <style>
            /* ============================================================
   PROCUREGOV AWARD NOTICE PAGE STYLES
   Official Award Notice – Ministry of Public Works
   Navy Blue (primary) | Forest Green (success) | Slate Gray (text)
   ============================================================ */

            :root {
                --navy: #grey;
                --navy-dark: #071E38;
                --navy-light: #1E3A6F;
                --forest: #2C5E3A;
                --forest-light: #3B7A4C;
                --forest-bg: #EAF7E6;
                --slate: #4A5B6E;
                --slate-light: #7A8B9F;
                --slate-lighter: #C8D2DC;
                --gray-bg: #F8F9FC;
                --white: #FFFFFF;
                --shadow-sm: 0 2px 6px rgba(0, 0, 0, 0.05);
                --shadow-md: 0 8px 20px rgba(0, 0, 0, 0.06);
                --radius-sm: 4px;
                --radius-md: 6px;
                --radius-lg: 8px;
                --transition: 0.2s ease;
            }

            /* ========== PAGE HEADER ========== */
            .page-header {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                flex-wrap: wrap;
                gap: 1rem;
                margin-bottom: 2rem;
            }

            .header-left .back-link {
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                color: var(--navy);
                text-decoration: none;
                font-size: 0.875rem;
                font-weight: 500;
                margin-bottom: 0.75rem;
                transition: var(--transition);
            }

            .header-left .back-link:hover {
                color: var(--forest);
                transform: translateX(-3px);
            }

            .header-left h1 {
                font-size: 1.875rem;
                font-weight: 700;
                color: var(--navy-dark);
                margin-bottom: 0.25rem;
            }

            .header-left p {
                color: var(--slate-light);
            }

            /* ========== BANNERS (Success / Info) ========== */
            .banner {
                display: flex;
                align-items: center;
                gap: 1.25rem;
                padding: 1.5rem;
                border-radius: var(--radius-md);
                margin-bottom: 2rem;
                box-shadow: var(--shadow-sm);
            }

            .banner-success {
                background: var(--forest-bg);
                border-left: 5px solid var(--forest);
            }

            .banner-info {
                background: var(--gray-bg);
                border-left: 5px solid var(--navy);
            }

            .banner-icon {
                width: 50px;
                height: 50px;
                background: var(--white);
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 1.75rem;
                font-weight: 700;
                box-shadow: var(--shadow-sm);
            }

            .banner-success .banner-icon {
                color: var(--forest);
            }

            .banner-info .banner-icon {
                color: var(--navy);
            }

            .banner-content h2 {
                font-size: 1.5rem;
                margin-bottom: 0.25rem;
                color: var(--navy-dark);
            }

            .banner-content p {
                color: var(--slate);
            }

            /* ========== MAIN AWARD NOTICE CARD ========== */
            .award-notice-card {
                background: var(--white);
                border-radius: var(--radius-md);
                box-shadow: var(--shadow-md);
                overflow: hidden;
                margin-bottom: 2rem;
            }

            .notice-header {
                background: var(--navy);
                color: var(--white);
                padding: 2rem 2rem 1.5rem;
                text-align: center;
                border-bottom: 3px solid var(--forest);
            }

            .ministry-logo h3 {
                font-size: 1.125rem;
                letter-spacing: 2px;
                margin-bottom: 0.25rem;
            }

            .ministry-logo h4 {
                font-size: 0.875rem;
                font-weight: 400;
                opacity: 0.85;
                margin-bottom: 1rem;
            }

            .notice-title h2 {
                font-size: 1.5rem;
                letter-spacing: 2px;
                border-top: 1px solid rgba(255,255,255,0.2);
                display: inline-block;
                padding-top: 0.75rem;
            }

            .notice-title p {
                font-size: 0.8rem;
                opacity: 0.7;
                margin-top: 0.5rem;
            }

            .notice-body {
                padding: 2rem;
            }

            /* Notice Sections */
            .notice-section {
                margin-bottom: 2rem;
            }

            .notice-section h3 {
                font-size: 1.125rem;
                font-weight: 600;
                color: var(--navy);
                margin-bottom: 1rem;
                padding-bottom: 0.5rem;
                border-bottom: 2px solid var(--gray-bg);
            }

            /* Info Table */
            .info-table {
                width: 100%;
                border-collapse: collapse;
            }

            .info-table td {
                padding: 0.75rem 0.5rem;
                border-bottom: 1px solid var(--slate-lighter);
                vertical-align: top;
            }

            .info-table .info-label {
                width: 35%;
                font-weight: 600;
                color: var(--slate);
                font-size: 0.875rem;
            }

            .info-table .info-value {
                width: 65%;
                color: var(--slate);
            }

            .info-table .highlight {
                font-weight: 700;
                color: var(--navy);
            }

            .info-table .amount {
                font-weight: 700;
                color: var(--forest);
                font-size: 1.125rem;
            }

            /* Justification Box */
            .justification-box {
                background: var(--gray-bg);
                padding: 1rem 1.25rem;
                border-radius: var(--radius-sm);
                border-left: 3px solid var(--forest);
            }

            .justification-box p {
                margin: 0;
                color: var(--slate);
                line-height: 1.6;
            }

            /* Next Steps */
            .next-steps {
                background: var(--gray-bg);
                padding: 1.25rem;
                border-radius: var(--radius-md);
            }

            .next-steps p {
                margin-bottom: 0.75rem;
                color: var(--slate);
            }

            .contact-info {
                margin-top: 1rem;
                padding-top: 0.75rem;
                border-top: 1px solid var(--slate-lighter);
                font-size: 0.875rem;
                color: var(--navy);
            }

            /* Notice Footer */
            .notice-footer {
                display: flex;
                justify-content: space-between;
                align-items: flex-end;
                flex-wrap: wrap;
                gap: 1rem;
                margin-top: 2rem;
                padding-top: 1.5rem;
                border-top: 2px solid var(--gray-bg);
            }

            .signature-line p {
                margin: 0.25rem 0;
                color: var(--slate);
                font-size: 0.875rem;
            }

            .signature-name {
                font-weight: 700;
                color: var(--navy);
                margin-top: 0.5rem !important;
            }

            .signature-title {
                font-style: italic;
            }

            .notice-date {
                text-align: right;
                font-size: 0.8rem;
                color: var(--slate-light);
            }

            /* ========== ACTION BUTTONS ========== */
            .action-buttons {
                display: flex;
                flex-wrap: wrap;
                gap: 1rem;
                margin-top: 1.5rem;
            }

            .btn {
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                padding: 0.6rem 1.25rem;
                border-radius: var(--radius-sm);
                font-weight: 600;
                font-size: 0.875rem;
                text-decoration: none;
                transition: var(--transition);
                cursor: pointer;
                border: 2px solid transparent;
            }

            .btn-primary {
                background: var(--forest);
                color: var(--white);
            }

            .btn-primary:hover {
                background: var(--forest-light);
                transform: translateY(-1px);
                box-shadow: 0 4px 10px rgba(44, 94, 58, 0.25);
            }

            .btn-outline {
                background: transparent;
                border-color: var(--navy);
                color: var(--navy);
            }

            .btn-outline:hover {
                background: rgba(11, 43, 79, 0.05);
                transform: translateY(-1px);
            }

            /* ========== ALERTS ========== */
            .alert {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                padding: 1rem 1.25rem;
                border-radius: var(--radius-sm);
                margin-bottom: 1.5rem;
                border-left: 4px solid transparent;
                background: var(--white);
                box-shadow: var(--shadow-sm);
            }

            .alert-success {
                background: var(--forest-bg);
                border-left-color: var(--forest);
                color: var(--forest-dark);
            }

            .alert-error {
                background: #FEF2F0;
                border-left-color: #E06C6C;
                color: #c0392b;
            }

            .close-alert {
                margin-left: auto;
                background: none;
                border: none;
                font-size: 1.25rem;
                cursor: pointer;
                opacity: 0.5;
                transition: var(--transition);
            }

            .close-alert:hover {
                opacity: 1;
            }

            /* ========== ERROR CARD ========== */
            .error-card {
                text-align: center;
                padding: 3rem;
                background: var(--white);
                border-radius: var(--radius-md);
                box-shadow: var(--shadow-sm);
            }

            .error-icon {
                width: 60px;
                height: 60px;
                background: #FEF2F0;
                color: #c0392b;
                font-size: 2rem;
                font-weight: 700;
                display: flex;
                align-items: center;
                justify-content: center;
                border-radius: 50%;
                margin: 0 auto 1rem;
            }

            .error-card h3 {
                font-size: 1.25rem;
                color: var(--navy);
                margin-bottom: 0.5rem;
            }

            .error-card p {
                color: var(--slate-light);
                margin-bottom: 1.5rem;
            }

            /* ========== PRINT STYLES ========== */
            @media print {
                .dashboard-layout .sidebar,
                .header-actions,
                .back-link,
                .action-buttons,
                .btn,
                .alert,
                .close-alert,
                .page-header .header-right {
                    display: none !important;
                }

                .dashboard-main {
                    padding: 0 !important;
                    background: white !important;
                }

                .award-notice-card {
                    box-shadow: none !important;
                    border: 1px solid #ddd;
                }

                .notice-header {
                    background: var(--navy);
                    color: white;
                    -webkit-print-color-adjust: exact;
                    print-color-adjust: exact;
                }

                .banner {
                    background: #f5f5f5 !important;
                    border-left: 5px solid #666;
                    -webkit-print-color-adjust: exact;
                    print-color-adjust: exact;
                }

                .info-table td {
                    border-bottom: 1px solid #ccc;
                }
            }

            /* ========== RESPONSIVE ========== */
            @media (max-width: 768px) {
                .page-header {
                    flex-direction: column;
                    align-items: flex-start;
                }

                .notice-header {
                    padding: 1.5rem;
                }

                .notice-body {
                    padding: 1.25rem;
                }

                .info-table td {
                    display: block;
                    width: 100%;
                    padding: 0.5rem 0;
                }

                .info-table .info-label {
                    width: 100%;
                    font-weight: 700;
                    padding-bottom: 0;
                }

                .info-table .info-value {
                    width: 100%;
                    padding-top: 0;
                }

                .notice-footer {
                    flex-direction: column;
                    align-items: flex-start;
                    text-align: left;
                }

                .notice-date {
                    text-align: left;
                }

                .banner {
                    flex-direction: column;
                    text-align: center;
                }
            }
        </style>
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <!-- Page Header -->
                <div class="page-header">
                    <div class="header-left">
                        <a href="${pageContext.request.contextPath}/supplier/bids" class="back-link">
                            Back to My Bids
                        </a>
                        <h1>Award Notice</h1>
                        <p>Official tender award notification</p>
                    </div>
                    <div class="header-right">
                        <button class="btn btn-outline" onclick="window.print()">
                            Print Notice
                        </button>
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

                <c:if test="${not empty tender}">

                    <!-- Award Status Banner -->
                    <c:choose>
                        <c:when test="${isWinner}">
                            <div class="banner banner-success">
                                <div class="banner-icon">W</div>
                                <div class="banner-content">
                                    <h2>Congratulations!</h2>
                                    <p>Your bid for this tender was <strong>SUCCESSFUL</strong>.</p>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="banner banner-info">
                                <div class="banner-icon">I</div>
                                <div class="banner-content">
                                    <h2>Tender Awarded</h2>
                                    <p>This tender has been awarded. Your bid was not successful on this occasion.</p>
                                </div>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <!-- Official Award Notice Card -->
                    <div class="award-notice-card">
                        <div class="notice-header">
                            <div class="ministry-logo">
                                <h3>MINISTRY OF PUBLIC WORKS</h3>
                                <h4>KINGDOM OF LESOTHO</h4>
                            </div>
                            <div class="notice-title">
                                <h2>OFFICIAL AWARD NOTICE</h2>
                                <p>ProcureGov Tender Management System</p>
                            </div>
                        </div>

                        <div class="notice-body">

                            <!-- Tender Information -->
                            <div class="notice-section">
                                <h3>Tender Information</h3>
                                <table class="info-table">
                                    <tr>
                                        <td class="info-label">Tender Reference</td>
                                        <td class="info-value highlight">${tender.referenceNumber}</td>
                                    </tr>
                                    <tr>
                                        <td class="info-label">Tender Title</td>
                                        <td class="info-value">${tender.title}</td>
                                    </tr>
                                    <tr>
                                        <td class="info-label">Category</td>
                                        <td class="info-value">${tender.category.displayName}</td>
                                    </tr>
                                    <tr>
                                        <td class="info-label">Estimated Value</td>
                                        <td class="info-value">${tender.formattedEstimatedValue}</td>
                                    </tr>
                                    <tr>
                                        <td class="info-label">Publication Date</td>
                                        <td class="info-value">${tender.formattedCreatedAt}</td>
                                    </tr>
                                    <tr>
                                        <td class="info-label">Closing Date</td>
                                        <td class="info-value">${tender.formattedClosingDateTime}</td>
                                    </tr>
                                </table>
                            </div>

                            <!-- Award Information -->
                            <div class="notice-section">
                                <h3>Award Information</h3>
                                <table class="info-table">
                                    <tr>
                                        <td class="info-label">Winning Supplier</td>
                                        <td class="info-value highlight">${tender.winningSupplierName}</td>
                                    </tr>
                                    <tr>
                                        <td class="info-label">Awarded Amount</td>
                                        <td class="info-value amount">${tender.formattedAwardedValue}</td>
                                    </tr>
                                    <tr>
                                        <td class="info-label">Award Date</td>
                                        <td class="info-value">${tender.formattedAwardDate}</td>
                                    </tr>
                                </table>
                            </div>

                            <!-- Award Justification -->
                            <c:if test="${not empty tender.awardJustification}">
                                <div class="notice-section">
                                    <h3>Award Justification</h3>
                                    <div class="justification-box">
                                        <p>${tender.awardJustification}</p>
                                    </div>
                                </div>
                            </c:if>

                            <!-- Your Bid Information -->
                            <c:if test="${not empty supplierBid}">
                                <div class="notice-section">
                                    <h3>Your Bid Summary</h3>
                                    <table class="info-table">
                                        <tr>
                                            <td class="info-label">Bid Amount</td>
                                            <td class="info-value">${supplierBid.formattedBidAmount}</td>
                                        </tr>
                                        <tr>
                                            <td class="info-label">Proposed Timeline</td>
                                            <td class="info-value">${supplierBid.proposedTimelineDays} days</td>
                                        </tr>
                                        <tr>
                                            <td class="info-label">Submitted Date</td>
                                            <td class="info-value">${supplierBid.formattedSubmittedAt}</td>
                                        </tr>
                                        <c:if test="${not empty supplierBid.finalScore}">
                                            <tr>
                                                <td class="info-label">Final Score</td>
                                                <td class="info-value">${supplierBid.formattedFinalScore}</td>
                                            </tr>
                                        </c:if>
                                        <c:if test="${not empty supplierBid.rank}">
                                            <tr>
                                                <td class="info-label">Rank</td>
                                                <td class="info-value">
                                                    <c:choose>
                                                        <c:when test="${supplierBid.rank == 1}">1st Place</c:when>
                                                        <c:when test="${supplierBid.rank == 2}">2nd Place</c:when>
                                                        <c:when test="${supplierBid.rank == 3}">3rd Place</c:when>
                                                        <c:otherwise>${supplierBid.rank}th Place</c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:if>
                                    </table>
                                </div>
                            </c:if>

                            <!-- Next Steps -->
                            <div class="notice-section">
                                <h3>Next Steps</h3>
                                <div class="next-steps">
                                    <c:choose>
                                        <c:when test="${isWinner}">
                                            <p>A contract document will be prepared and sent to you within 7 working days.</p>
                                            <p>Please ensure your company profile and contact information are up to date.</p>
                                            <p>For any queries, contact the Procurement Office:</p>
                                        </c:when>
                                        <c:otherwise>
                                            <p>We appreciate your participation in this tender process.</p>
                                            <p>We encourage you to participate in future tender opportunities.</p>
                                            <p>For feedback or queries, contact the Procurement Office:</p>
                                        </c:otherwise>
                                    </c:choose>
                                    <div class="contact-info">
                                        <p>Tel: +266 2232 1000</p>
                                        <p>Email: procurement@mpw.gov.ls</p>
                                        <p>Address: P.O. Box 20, Maseru 100, Lesotho</p>
                                    </div>
                                </div>
                            </div>

                            <!-- Official Stamp -->
                            <div class="notice-footer">
                                <div class="signature-line">
                                    <p>Issued by:</p>
                                    <p class="signature-name">Thabo Mokoena</p>
                                    <p class="signature-title">Chief Procurement Officer</p>
                                    <p>Ministry of Public Works</p>
                                </div>
                                <div class="notice-date">
                                    <p>Date Issued: ${tender.formattedAwardDate}</p>
                                    <p>Notice ID: AW-${tender.referenceNumber}</p>
                                </div>
                            </div>

                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="action-buttons">
                        <a href="${pageContext.request.contextPath}/supplier/bids" class="btn btn-primary">
                            Back to My Bids
                        </a>
                        <a href="${pageContext.request.contextPath}/supplier/tenders" class="btn btn-outline">
                            Browse More Tenders
                        </a>
                        <button class="btn btn-outline" onclick="window.print()">
                            Print Award Notice
                        </button>
                    </div>

                </c:if>

                <c:if test="${empty tender}">
                    <div class="error-card">
                        <div class="error-icon">!</div>
                        <h3>Award Notice Not Found</h3>
                        <p>The award notice you are looking for could not be found.</p>
                        <a href="${pageContext.request.contextPath}/supplier/bids" class="btn btn-primary">
                            Back to My Bids
                        </a>
                    </div>
                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
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