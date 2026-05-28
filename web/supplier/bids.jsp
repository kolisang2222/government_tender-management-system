<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>My Bids - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/supplier-bids.css">
        <link rel="stylesheet" href="procgov-dashboard.css">
    </head>
    <body>

        <jsp:include page="/shared/navigation.jsp" />

        <div class="dashboard-layout">

            <jsp:include page="/shared/sidebar.jsp" />

            <main class="dashboard-main">

                <!-- Page Header -->
                <div class="page-header">
                    <div class="header-left">
                        <h1>My Bids</h1>
                        <p>View and track all your submitted bids</p>
                    </div>
                    <div class="header-right">
                        <a href="${pageContext.request.contextPath}/supplier/tenders" class="btn btn-primary">
                            Browse Open Tenders
                        </a>
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

                <!-- Stats Summary -->
                <div class="stats-summary">
                    <div class="stat-card">
                        <div class="stat-icon">T</div>
                        <div class="stat-content">
                            <span class="stat-value">${totalBids}</span>
                            <span class="stat-label">Total Bids</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">P</div>
                        <div class="stat-content">
                            <span class="stat-value">${pendingBids}</span>
                            <span class="stat-label">Pending</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">W</div>
                        <div class="stat-content">
                            <span class="stat-value">${wonBids}</span>
                            <span class="stat-label">Won</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-content">
                            <span class="stat-value">${totalBidValue}</span>
                            <span class="stat-label">Total Bid Value</span>
                        </div>
                    </div>
                </div>

                <!-- Filter Section -->
                <div class="filter-section">
                    <form action="${pageContext.request.contextPath}/supplier/my-bids" method="GET" class="filter-form">
                       <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <div class="filter-row">
                            <div class="filter-group">
                                <label for="statusFilter">Status</label>
                                <select id="statusFilter" name="status">
                                    <option value="">All Statuses</option>
                                    <option value="PENDING" ${param.status == 'PENDING' ? 'selected' : ''}>Pending</option>
                                    <option value="EVALUATING" ${param.status == 'EVALUATING' ? 'selected' : ''}>Under Evaluation</option>
                                    <option value="WON" ${param.status == 'WON' ? 'selected' : ''}>Won</option>
                                    <option value="LOST" ${param.status == 'LOST' ? 'selected' : ''}>Not Awarded</option>
                                </select>
                            </div>
                            <div class="filter-group">
                                <label for="dateFrom">From Date</label>
                                <input type="date" id="dateFrom" name="dateFrom" value="${param.dateFrom}">
                            </div>
                            <div class="filter-group">
                                <label for="dateTo">To Date</label>
                                <input type="date" id="dateTo" name="dateTo" value="${param.dateTo}">
                            </div>
                            <div class="filter-actions">
                                <button type="submit" class="btn btn-primary">Filter</button>
                                <a href="${pageContext.request.contextPath}/supplier/my-bids" class="btn btn-secondary">Clear</a>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- Bids Table -->
                <div class="table-container">
                    <c:choose>
                        <c:when test="${not empty bids}">
                            <table class="bids-table">
                                <thead>
                                    <tr>
                                        <th>Tender Reference</th>
                                        <th>Tender Title</th>
                                        <th>Bid Amount</th>
                                        <th>Timeline</th>
                                        <th>Submitted Date</th>
                                        <th>Status</th>
                                        <th>Actions</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach var="bid" items="${bids}">
                                        <tr class="bid-row status-${bid.statusClass}">
                                            <td>
                                                <span class="ref-number">${bid.tenderReference}</span>
                                            </td>
                                            <td>
                                                <span class="tender-title">${bid.tenderTitle}</span>
                                            </td>
                                            <td>
                                                <span class="bid-amount">${bid.formattedBidAmount}</span>
                                            </td>
                                            <td>
                                                <span class="timeline">${bid.proposedTimelineDays} days</span>
                                            </td>
                                            <td>
                                                <span class="submitted-date">${bid.formattedSubmittedAt}</span>
                                            </td>
                                            <td>
                                                <span class="status-badge ${bid.statusClass}">
                                                    ${bid.statusDisplay}
                                                </span>
                                            </td>
                                            <td class="actions-cell">
                                                <a href="${pageContext.request.contextPath}/supplier/tender-detail?id=${bid.tenderId}" 
                                                   class="btn-icon" title="View Tender">View</a>

                                                <c:if test="${bid.canViewScore}">
                                                    <a href="${pageContext.request.contextPath}/supplier/bid-score?id=${bid.bidId}" 
                                                       class="btn-icon" title="View Score">Score</a>
                                                </c:if>

                                                <c:if test="${bid.isWinner}">
                                                    <a href="${pageContext.request.contextPath}/supplier/award-notice?id=${bid.tenderId}" 
                                                       class="btn-icon btn-success" title="View Award Notice">Award</a>
                                                </c:if>

                                                <c:if test="${bid.canWithdraw}">
                                                    <form action="${pageContext.request.contextPath}/supplier/withdraw-bid" method="POST" 
                                                          style="display: inline;" onsubmit="return confirm('Are you sure you want to withdraw this bid?');">
                                                        <input type="hidden" name="bidId" value="${bid.bidId}">
                                                        <button type="submit" class="btn-icon btn-danger" title="Withdraw Bid">X</button>
                                                    </form>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-icon">!</div>
                                <h3>No Bids Found</h3>
                                <p>You haven't submitted any bids yet.</p>
                                <a href="${pageContext.request.contextPath}/supplier/tenders" class="btn btn-primary">
                                    Browse Open Tenders
                                </a>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/supplier/my-bids?page=${currentPage - 1}&status=${param.status}&dateFrom=${param.dateFrom}&dateTo=${param.dateTo}" 
                               class="page-link">Previous</a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="page">
                            <c:choose>
                                <c:when test="${page == currentPage}">
                                    <span class="page-link active">${page}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/supplier/my-bids?page=${page}&status=${param.status}&dateFrom=${param.dateFrom}&dateTo=${param.dateTo}" 
                                       class="page-link">${page}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/supplier/my-bids?page=${currentPage + 1}&status=${param.status}&dateFrom=${param.dateFrom}&dateTo=${param.dateTo}" 
                               class="page-link">Next</a>
                        </c:if>
                    </div>
                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            document.getElementById('statusFilter').addEventListener('change', function () {
                this.form.submit();
            });

            setTimeout(function () {
                const today = new Date();
                const firstOfYear = new Date(today.getFullYear(), 0, 1);

                if (!document.getElementById('dateFrom').value) {
                    document.getElementById('dateFrom').value = firstOfYear.toISOString().split('T')[0];
                }
                if (!document.getElementById('dateTo').value) {
                    document.getElementById('dateTo').value = today.toISOString().split('T')[0];
                }

                const alerts = document.querySelectorAll('.alert');
                alerts.forEach(function (alert) {
                    alert.style.transition = 'opacity 0.5s';
                    alert.style.opacity = '0';
                    setTimeout(function () {
                        alert.remove();
                    }, 500);
                });
            }, 100);
        </script>

    </body>
</html>