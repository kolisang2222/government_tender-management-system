<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Award Results - ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/evaluatordashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/evaluator-results.css">
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
                        <h1>Award Results</h1>
                        <p>View awarded tenders and contract outcomes</p>
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
                        <div class="stat-icon">A</div>
                        <div class="stat-content">
                            <span class="stat-value">${totalAwarded}</span>
                            <span class="stat-label">Total Awarded</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">V</div>
                        <div class="stat-content">
                            <span class="stat-value">${totalAwardValue}</span>
                            <span class="stat-label">Total Award Value</span>
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-icon">M</div>
                        <div class="stat-content">
                            <span class="stat-value">${thisMonthAwards}</span>
                            <span class="stat-label">This Month</span>
                        </div>
                    </div>
                </div>

                <!-- Filter Section -->
                <div class="filter-section">
                    <form action="${pageContext.request.contextPath}/evaluator/results" method="GET" class="filter-form">
                        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                        <div class="filter-row">
                            <div class="filter-group">
                                <label for="dateFrom">From Date</label>
                                <input type="date" id="dateFrom" name="dateFrom" value="${param.dateFrom}">
                            </div>
                            <div class="filter-group">
                                <label for="dateTo">To Date</label>
                                <input type="date" id="dateTo" name="dateTo" value="${param.dateTo}">
                            </div>
                            <div class="filter-group">
                                <label for="categoryFilter">Category</label>
                                <select id="categoryFilter" name="category">
                                    <option value="">All Categories</option>
                                    <option value="CONSTRUCTION" ${param.category == 'CONSTRUCTION' ? 'selected' : ''}>Construction</option>
                                    <option value="ROADS" ${param.category == 'ROADS' ? 'selected' : ''}>Roads</option>
                                    <option value="ELECTRICAL" ${param.category == 'ELECTRICAL' ? 'selected' : ''}>Electrical</option>
                                    <option value="PLUMBING" ${param.category == 'PLUMBING' ? 'selected' : ''}>Plumbing</option>
                                    <option value="GENERAL_SERVICES" ${param.category == 'GENERAL_SERVICES' ? 'selected' : ''}>General Services</option>
                                </select>
                            </div>
                            <div class="filter-actions">
                                <button type="submit" class="btn btn-primary">Filter</button>
                                <a href="${pageContext.request.contextPath}/evaluator/results" class="btn btn-secondary">Clear</a>
                            </div>
                        </div>
                    </form>
                </div>

                <!-- Results List -->
                <div class="results-container">
                    <c:choose>
                        <c:when test="${not empty awardedTenders}">
                            <c:forEach var="tender" items="${awardedTenders}">
                                <div class="result-card">
                                    <div class="card-header">
                                        <div class="tender-info">
                                            <span class="ref-number">${tender.referenceNumber}</span>
                                            <h3>${tender.title}</h3>
                                            <div class="tender-meta">
                                                <span class="category-badge">${tender.category.displayName}</span>
                                                <span class="status-badge status-awarded">Awarded</span>
                                            </div>
                                        </div>
                                        <div class="award-date">
                                            ${tender.awardDateFormatted}
                                        </div>
                                    </div>

                                    <div class="card-body">
                                        <!-- Award Summary -->
                                        <div class="award-summary">
                                            <div class="summary-item">
                                                <span class="summary-label">Winning Supplier</span>
                                                <span class="summary-value highlight">${tender.winningSupplierName}</span>
                                            </div>
                                            <div class="summary-item">
                                                <span class="summary-label">Awarded Amount</span>
                                                <span class="summary-value amount">${tender.formattedAwardedValue}</span>
                                            </div>
                                            <div class="summary-item">
                                                <span class="summary-label">Estimated Value</span>
                                                <span class="summary-value">${tender.formattedEstimatedValue}</span>
                                            </div>
                                            <div class="summary-item">
                                                <span class="summary-label">Total Bids</span>
                                                <span class="summary-value">${tender.bidCount} bids</span>
                                            </div>
                                        </div>

                                        <!-- Award Justification -->
                                        <c:if test="${not empty tender.awardJustification}">
                                            <div class="award-justification">
                                                <span class="justification-label">Award Justification</span>
                                                <p>${tender.awardJustification}</p>
                                            </div>
                                        </c:if>

                                        <!-- Top Bids Comparison -->
                                        <c:if test="${not empty tender.rankedBids}">
                                            <div class="bids-comparison">
                                                <h4>Bid Comparison (Top 3)</h4>
                                                <table class="comparison-table">
                                                    <thead>
                                                        <tr>
                                                            <th>Rank</th>
                                                            <th>Supplier</th>
                                                            <th>Bid Amount</th>
                                                            <th>Final Score</th>
                                                        </tr>
                                                    </thead>
                                                    <tbody>
                                                        <c:forEach var="bid" items="${tender.rankedBids}" begin="0" end="2">
                                                            <tr class="${bid.rank == 1 ? 'winner' : ''}">
                                                                <td>
                                                                    <c:choose>
                                                                        <c:when test="${bid.rank == 1}">
                                                                            <span class="rank-badge gold">1st</span>
                                                                        </c:when>
                                                                        <c:when test="${bid.rank == 2}">
                                                                            <span class="rank-badge silver">2nd</span>
                                                                        </c:when>
                                                                        <c:when test="${bid.rank == 3}">
                                                                            <span class="rank-badge bronze">3rd</span>
                                                                        </c:when>
                                                                    </c:choose>
                                                                </td>
                                                                <td>${bid.supplierName}</td>
                                                                <td>${bid.formattedBidAmount}</td>
                                                                <td>
                                                                    <strong>${bid.formattedFinalScore}</strong>
                                                                </td>
                                                            </tr>
                                                        </c:forEach>
                                                    </tbody>
                                                </table>
                                            </div>
                                        </c:if>

                                        <!-- Your Evaluation Summary -->
                                        <c:if test="${not empty tender.evaluatorScores}">
                                            <div class="your-evaluation">
                                                <h4>Your Evaluation</h4>
                                                <div class="evaluation-stats">
                                                    <div class="stat-row">
                                                        <span>Bids Scored</span>
                                                        <span>${tender.scoredBids} / ${tender.bidCount}</span>
                                                    </div>
                                                    <div class="stat-row">
                                                        <span>Your Average Score</span>
                                                        <span>${tender.evaluatorAvgScore}</span>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>

                                    <div class="card-footer">
                                        <a href="${pageContext.request.contextPath}/evaluator/award-notice?id=${tender.tenderId}" 
                                           class="btn btn-outline">
                                            View Award Notice
                                        </a>
                                        <a href="${pageContext.request.contextPath}/evaluator/view-scores?id=${tender.tenderId}" 
                                           class="btn btn-outline">
                                            View All Scores
                                        </a>
                                        <a href="${pageContext.request.contextPath}/evaluator/tender-detail?id=${tender.tenderId}" 
                                           class="btn btn-primary">
                                            View Tender Details
                                        </a>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <div class="empty-state">
                                <div class="empty-icon">!</div>
                                <h3>No Award Results Found</h3>
                                <p>There are no awarded tenders matching your criteria.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <!-- Pagination -->
                <c:if test="${totalPages > 1}">
                    <div class="pagination">
                        <c:if test="${currentPage > 1}">
                            <a href="${pageContext.request.contextPath}/evaluator/results?page=${currentPage - 1}&dateFrom=${param.dateFrom}&dateTo=${param.dateTo}&category=${param.category}" 
                               class="page-link">Previous</a>
                        </c:if>

                        <c:forEach begin="1" end="${totalPages}" var="page">
                            <c:choose>
                                <c:when test="${page == currentPage}">
                                    <span class="page-link active">${page}</span>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/evaluator/results?page=${page}&dateFrom=${param.dateFrom}&dateTo=${param.dateTo}&category=${param.category}" 
                                       class="page-link">${page}</a>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>

                        <c:if test="${currentPage < totalPages}">
                            <a href="${pageContext.request.contextPath}/evaluator/results?page=${currentPage + 1}&dateFrom=${param.dateFrom}&dateTo=${param.dateTo}&category=${param.category}" 
                               class="page-link">Next</a>
                        </c:if>
                    </div>
                </c:if>

            </main>
        </div>

        <jsp:include page="/shared/footer.jsp" />

        <script>
            // Auto-submit form when category changes
            document.getElementById('categoryFilter').addEventListener('change', function () {
                this.form.submit();
            });

            // Set default dates
            const today = new Date();
            const firstOfYear = new Date(today.getFullYear(), 0, 1);

            if (!document.getElementById('dateFrom').value) {
                document.getElementById('dateFrom').value = firstOfYear.toISOString().split('T')[0];
            }
            if (!document.getElementById('dateTo').value) {
                document.getElementById('dateTo').value = today.toISOString().split('T')[0];
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