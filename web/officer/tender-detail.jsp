<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Tender Details - ProcureGov</title>
        <style>
            /* ---------- PROCUREGOV SYSTEM STYLES ---------- */
            * {
                margin: 0;
                padding: 0;
                box-sizing: border-box;
            }

            body {
                font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
                background: #F5F7FA;
                color: #2D3A4B;
                line-height: 1.5;
            }

            /* Dashboard layout (compatible with navigation and sidebar includes) */
            .dashboard-layout {
                display: flex;
                min-height: calc(100vh - 64px);
            }

            .dashboard-main {
                flex: 1;
                padding: 2rem;
                background: #F5F7FA;
                overflow-x: auto;
            }

            /* Header & Back link */
            .page-header {
                margin-bottom: 2rem;
            }

            .back-link {
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                color: #0B2B4F;
                text-decoration: none;
                font-size: 0.875rem;
                font-weight: 500;
                margin-bottom: 1rem;
                transition: 0.2s;
            }

            .back-link:hover {
                color: #2C5E3A;
                transform: translateX(-3px);
            }

            h1 {
                font-size: 1.875rem;
                font-weight: 700;
                color: #071E38;
                letter-spacing: -0.02em;
            }

            /* Action Bar (status badge + buttons) */
            .action-bar {
                display: flex;
                flex-wrap: wrap;
                justify-content: space-between;
                align-items: center;
                gap: 1rem;
                background: white;
                padding: 1rem 1.5rem;
                border-radius: 6px;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.05);
                margin-bottom: 1.75rem;
            }

            .status-badge-large {
                display: inline-block;
                padding: 0.5rem 1.25rem;
                border-radius: 30px;
                font-weight: 700;
                font-size: 0.875rem;
                background: #F0F2F5;
                color: #2D3A4B;
            }

            /* Dynamic status colors */
            .status-draft {
                background: #F0F2F5;
                color: #4A5B6E;
            }
            .status-published {
                background: #E8F0FE;
                color: #1a5a8a;
            }
            .status-closed {
                background: #FEF5E7;
                color: #b45f1b;
            }
            .status-under_evaluation {
                background: #FFF3E0;
                color: #c96f0d;
            }
            .status-evaluated {
                background: #EAF7E6;
                color: #2c5e2e;
            }
            .status-awarded {
                background: #E0F2E9;
                color: #1e6b3b;
                border: 1px solid #2C5E3A;
            }

            .action-buttons {
                display: flex;
                gap: 0.75rem;
                flex-wrap: wrap;
            }

            /* Buttons – crisp corners, subtle hover */
            .btn {
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
                padding: 0.6rem 1.25rem;
                border-radius: 4px;
                font-weight: 600;
                font-size: 0.875rem;
                text-decoration: none;
                transition: all 0.2s ease;
                border: 1px solid transparent;
                cursor: pointer;
            }

            .btn-primary {
                background: #0B2B4F;
                color: white;
                box-shadow: 0 1px 2px rgba(0,0,0,0.05);
            }
            .btn-primary:hover {
                background: #1E3A6F;
                transform: translateY(-1px);
                box-shadow: 0 4px 8px rgba(11,43,79,0.15);
            }

            .btn-success {
                background: #2C5E3A;
                color: white;
            }
            .btn-success:hover {
                background: #3B7A4C;
                transform: translateY(-1px);
                box-shadow: 0 4px 8px rgba(44,94,58,0.2);
            }

            .btn-secondary {
                background: transparent;
                border-color: #0B2B4F;
                color: #0B2B4F;
            }
            .btn-secondary:hover {
                background: rgba(11,43,79,0.05);
                border-color: #2C5E3A;
            }

            /* Detail Card */
            .detail-card {
                background: white;
                border-radius: 6px;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
                padding: 1.75rem;
                margin-bottom: 2rem;
                transition: box-shadow 0.2s;
            }
            .detail-card:hover {
                box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06);
            }

            .detail-card h2 {
                font-size: 1.5rem;
                color: #0B2B4F;
                margin-bottom: 1.5rem;
                padding-bottom: 0.75rem;
                border-bottom: 2px solid #EFF3F8;
            }

            .detail-grid {
                display: grid;
                grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
                gap: 1.25rem;
                margin-bottom: 1.25rem;
            }

            .detail-item {
                display: flex;
                flex-direction: column;
                gap: 0.25rem;
            }
            .detail-item label {
                font-size: 0.7rem;
                text-transform: uppercase;
                letter-spacing: 0.03em;
                color: #7A8B9F;
                font-weight: 600;
            }
            .detail-item .value {
                font-size: 0.95rem;
                font-weight: 500;
                color: #1F2C3C;
            }

            .full-width {
                grid-column: 1 / -1;
            }

            .description-box {
                background: #F8FAFD;
                padding: 1rem;
                border-radius: 4px;
                border-left: 3px solid #0B2B4F;
                line-height: 1.6;
            }

            /* Bids section */
            .bids-section {
                background: white;
                border-radius: 6px;
                box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
                padding: 1.5rem;
            }

            .bids-section h3 {
                font-size: 1.125rem;
                color: #0B2B4F;
                margin-bottom: 1.25rem;
                font-weight: 600;
            }

            .bids-table {
                width: 100%;
                border-collapse: collapse;
            }

            .bids-table th {
                text-align: left;
                padding: 0.875rem 1rem;
                background: #F0F4F9;
                font-size: 0.75rem;
                text-transform: uppercase;
                font-weight: 700;
                color: #0B2B4F;
                letter-spacing: 0.03em;
            }

            .bids-table td {
                padding: 0.875rem 1rem;
                border-bottom: 1px solid #EDF2F7;
                color: #4A5B6E;
            }

            .bids-table tbody tr:nth-child(even) {
                background-color: #FAFCFE;
            }

            .bids-table tbody tr:hover {
                background-color: #F1F5F9;
            }

            .empty-message {
                text-align: center;
                padding: 2rem;
                color: #7A8B9F;
                background: #FAFCFE;
                border-radius: 4px;
            }

            /* Responsive */
            @media (max-width: 768px) {
                .dashboard-main {
                    padding: 1.25rem;
                }
                .action-bar {
                    flex-direction: column;
                    align-items: stretch;
                }
                .action-buttons {
                    justify-content: flex-start;
                }
                .detail-grid {
                    grid-template-columns: 1fr;
                    gap: 1rem;
                }
            }

            @media (max-width: 640px) {
                .bids-table thead {
                    display: none;
                }
                .bids-table tbody tr {
                    display: block;
                    margin-bottom: 1rem;
                    border: 1px solid #E2E8F0;
                    border-radius: 6px;
                    padding: 0.75rem;
                    background: white;
                }
                .bids-table tbody td {
                    display: flex;
                    justify-content: space-between;
                    align-items: baseline;
                    padding: 0.5rem;
                    border-bottom: 1px solid #EDF2F7;
                }
                .bids-table tbody td:last-child {
                    border-bottom: none;
                }
                .bids-table td::before {
                    content: attr(data-label);
                    font-weight: 700;
                    color: #0B2B4F;
                    width: 110px;
                    font-size: 0.75rem;
                    text-transform: uppercase;
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
                    <a href="${pageContext.request.contextPath}/officer/tenders" class="back-link">← Back to Tenders</a>
                    <h1>Tender Details</h1>
                </div>

                <c:if test="${not empty tender}">

                    <!-- Status & Action Bar -->
                    <div class="action-bar">
                        <span class="status-badge-large status-${fn:toLowerCase(tender.status.name())}">
                            ${tender.status.displayName}
                        </span>
                        <div class="action-buttons">
                            <c:if test="${tender.status == 'DRAFT'}">
                                <a href="${pageContext.request.contextPath}/officer/edit-tender?id=${tender.tenderId}" 
                                   class="btn btn-primary">✏️ Edit Tender</a>
                                <a href="${pageContext.request.contextPath}/officer/publish-tender?id=${tender.tenderId}" 
                                   class="btn btn-success">📢 Publish Tender</a>
                            </c:if>
                            <c:if test="${tender.status == 'CLOSED'}">
                                <a href="${pageContext.request.contextPath}/officer/start-evaluation?action=startEvaluation&id=${tender.tenderId}" 
                                   class="btn btn-primary" 
                                   onclick="return confirm('Start evaluation for this tender? Committee members will be able to score bids.');">
                                    ⚖️ Start Evaluation
                                </a>
                            </c:if>
                            <c:if test="${tender.status == 'EVALUATED'}">
                                <a href="${pageContext.request.contextPath}/officer/award-tender?id=${tender.tenderId}" 
                                   class="btn btn-success">🏆 Award Contract</a>
                            </c:if>
                            <a href="${pageContext.request.contextPath}/officer/download-tender?id=${tender.tenderId}" 
                               class="btn btn-secondary">📄 Download Notice</a>
                        </div>
                    </div>

                    <!-- Tender Information Card -->
                    <div class="detail-card">
                        <h2>${tender.title}</h2>
                        <div class="detail-grid">
                            <div class="detail-item">
                                <label>Reference Number</label>
                                <span class="value">${tender.referenceNumber}</span>
                            </div>
                            <div class="detail-item">
                                <label>Category</label>
                                <span class="value">${tender.category.displayName}</span>
                            </div>
                            <div class="detail-item">
                                <label>Status</label>
                                <span class="value">${tender.status.displayName}</span>
                            </div>
                            <div class="detail-item">
                                <label>Estimated Value</label>
                                <span class="value">${tender.formattedEstimatedValue}</span>
                            </div>
                            <div class="detail-item">
                                <label>Closing Date</label>
                                <span class="value">${tender.formattedClosingDateTime}</span>
                            </div>
                            <div class="detail-item">
                                <label>Closing Time</label>
                                <span class="value">${tender.formattedClosingTime} (Lesotho Time)</span>
                            </div>
                            <div class="detail-item">
                                <label>Created By</label>
                                <span class="value">${tender.createdByName}</span>
                            </div>
                            <div class="detail-item">
                                <label>Created Date</label>
                                <span class="value">${tender.formattedCreatedAt}</span>
                            </div>
                        </div>
                        <div class="detail-item full-width">
                            <label>Description</label>
                            <div class="value description-box">${tender.description}</div>
                        </div>
                    </div>

                    <!-- Submitted Bids Section -->
                    <div class="bids-section">
                        <h3>📋 Submitted Bids (${bidCount})</h3>
                        <c:choose>
                            <c:when test="${not empty bids}">
                                <table class="bids-table">
                                    <thead>
                                        <tr>
                                            <th>Supplier</th>
                                            <th>Bid Amount</th>
                                            <th>Timeline</th>
                                            <th>Submitted</th>
                                            <th>Status</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach var="bid" items="${bids}">
                                            <tr>
                                                <td data-label="Supplier">${bid.supplierName}</td>
                                                <td data-label="Bid Amount">${bid.formattedBidAmount}</td>
                                                <td data-label="Timeline">${bid.proposedTimelineDays} days</td>
                                                <td data-label="Submitted">${bid.formattedSubmittedAt}</td>
                                                <td data-label="Status">
                                                    <c:choose>
                                                        <c:when test="${bid.finalScore > 0}">
                                                            <span style="background:#EAF7E6; padding:0.2rem 0.6rem; border-radius:30px; font-size:0.7rem; font-weight:600;">✓ Scored: ${bid.formattedFinalScore}</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span style="background:#FEF5E7; padding:0.2rem 0.6rem; border-radius:30px; font-size:0.7rem;">⏳ Pending</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </c:when>
                            <c:otherwise>
                                <p class="empty-message">No bids have been submitted for this tender yet.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

    </body>
</html>