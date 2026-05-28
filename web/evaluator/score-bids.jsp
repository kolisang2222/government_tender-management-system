<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Score Bids - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/officerdashboard.css">
        <style>
            /* ============================================================
       SCORE BIDS PAGE - PROCUREGOV EVALUATOR DASHBOARD
       Navy Blue, Forest Green, Slate Gray – Professional & Modern
       ============================================================ */

            /* ----- Variables (matching system theme) ----- */
            :root {
                --navy: #0B2B4F;
                --navy-dark: #071E38;
                --navy-light: #1E3A6F;
                --forest: #2C5E3A;
                --forest-light: #3B7A4C;
                --slate: #4A5B6E;
                --slate-light: #7A8B9F;
                --slate-lighter: #C8D2DC;
                --gray-bg: #F8F9FC;
                --white: #FFFFFF;
                --shadow-sm: 0 2px 6px rgba(0, 0, 0, 0.05);
                --shadow-md: 0 8px 20px rgba(0, 0, 0, 0.06);
                --radius-sm: 4px;
                --radius-md: 6px;
                --transition: 0.2s ease;
            }

            /* ----- Page Header (already present) ----- */
            .page-header {
                margin-bottom: 2rem;
            }

            .page-header h1 {
                font-size: 1.875rem;
                font-weight: 700;
                color: var(--navy-dark);
                margin-bottom: 0.25rem;
            }

            .page-header p {
                color: var(--slate-light);
                font-size: 0.95rem;
            }

            /* ----- Alerts (Success / Error) ----- */
            .alert {
                display: flex;
                align-items: center;
                gap: 0.75rem;
                padding: 1rem 1.25rem;
                border-radius: var(--radius-sm);
                margin-bottom: 1.25rem;
                border-left: 4px solid transparent;
                background: var(--white);
                box-shadow: var(--shadow-sm);
            }

            .alert-success {
                background: #EAF7E6;
                border-left-color: var(--forest);
                color: #1e6b3b;
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

            /* ----- Data Table (modern, zebra-striped) ----- */
            .data-table {
                width: 100%;
                border-collapse: collapse;
                background: var(--white);
                border-radius: var(--radius-md);
                overflow: hidden;
                box-shadow: var(--shadow-sm);
                margin-top: 0.5rem;
            }

            .data-table th {
                background: var(--gray-bg);
                padding: 1rem;
                text-align: left;
                font-size: 0.75rem;
                text-transform: uppercase;
                letter-spacing: 0.04em;
                font-weight: 700;
                color: var(--navy);
                border-bottom: 1px solid var(--slate-lighter);
            }

            .data-table td {
                padding: 1rem;
                border-bottom: 1px solid #EDF2F7;
                color: var(--slate);
                vertical-align: middle;
            }

            .data-table tbody tr:nth-child(even) {
                background-color: #FAFCFE;
            }

            .data-table tbody tr:hover {
                background-color: #F1F5F9;
                transition: background 0.15s;
            }

            /* ----- Progress Bar (small) ----- */
            .progress-bar-small {
                background-color: var(--slate-lighter);
                border-radius: 20px;
                height: 1.5rem;
                width: 100%;
                max-width: 120px;
                overflow: hidden;
                margin-bottom: 0.25rem;
            }

            .progress-fill {
                background-color: var(--forest);
                height: 100%;
                color: white;
                text-align: center;
                font-size: 0.65rem;
                font-weight: 600;
                line-height: 1.5rem;
                border-radius: 20px;
                white-space: nowrap;
                padding: 0 0.25rem;
            }

            td small {
                font-size: 0.7rem;
                color: var(--slate-light);
                display: block;
            }

            /* ----- Button (small primary) ----- */
            .btn-sm {
                display: inline-block;
                padding: 0.35rem 0.9rem;
                font-size: 0.7rem;
                font-weight: 600;
                border-radius: var(--radius-sm);
                text-decoration: none;
                transition: var(--transition);
                border: 1px solid transparent;
            }

            .btn-primary {
                background: var(--forest);
                color: white;
            }

            .btn-primary:hover {
                background: var(--forest-light);
                transform: translateY(-1px);
                box-shadow: 0 2px 6px rgba(44, 94, 58, 0.2);
            }

            /* ----- Empty State ----- */
            .empty-state {
                text-align: center;
                padding: 3rem;
                background: var(--white);
                border-radius: var(--radius-md);
                box-shadow: var(--shadow-sm);
            }

            .empty-state p {
                color: var(--slate-light);
                margin-bottom: 0.5rem;
            }

            .empty-state p strong {
                color: var(--navy);
            }

            /* ----- Responsive (mobile-friendly table) ----- */
            @media (max-width: 768px) {
                .data-table {
                    display: block;
                    overflow-x: auto;
                    white-space: nowrap;
                }

                .data-table th,
                .data-table td {
                    padding: 0.75rem;
                }

                .progress-bar-small {
                    max-width: 100px;
                }
            }

            @media (max-width: 640px) {
                .page-header h1 {
                    font-size: 1.5rem;
                }

                .data-table th,
                .data-table td {
                    font-size: 0.75rem;
                    padding: 0.5rem;
                }

                .btn-sm {
                    padding: 0.25rem 0.7rem;
                    font-size: 0.65rem;
                }
            }
        </style>
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <div class="page-header">
                    <h1>Score Bids</h1>
                    <p>View and score bids for tenders under evaluation</p>
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

                <c:choose>
                    <c:when test="${not empty tenders}">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Reference</th>
                                    <th>Title</th>
                                    <th>Category</th>
                                    <th>Closing Date</th>
                                    <th>Total Bids</th>
                                    <th>Your Progress</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="tender" items="${tenders}">
                                    <tr>
                                        <td><strong>${tender.referenceNumber}</strong></td>
                                        <td>${tender.title}</td>
                                        <td>${tender.category.displayName}</td>
                                        <td>${tender.formattedClosingDate}</td>
                                        <td style="text-align: center;">${tender.bidCount}</td>
                                        <td>
                                            <div class="progress-bar-small">
                                                <div class="progress-fill" style="width: ${tender.evaluatorProgressPercent}%;">
                                                    ${tender.evaluatorProgressPercent}%
                                                </div>
                                            </div>
                                            <small>${tender.scoredBids} of ${tender.bidCount} scored</small>
                                        </td>
                                        <td>
                                            <a href="${pageContext.request.contextPath}/evaluator/score-bids?tenderId=${tender.tenderId}" 
                                               class="btn-sm btn-primary">View Bids</a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <p>No tenders are currently available for scoring.</p>
                            <p>Tenders become available when they are moved to <strong>UNDER_EVALUATION</strong> status.</p>
                        </div>
                    </c:otherwise>
                </c:choose>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

    </body>
</html>