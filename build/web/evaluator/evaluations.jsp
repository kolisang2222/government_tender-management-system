<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Evaluations - ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/evaluatordashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/evaluator-evaluations.css">
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
                    <h1>My Evaluations</h1>
                    <p>View and manage your bid evaluations</p>
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
                        <span class="stat-value">${totalAssigned}</span>
                        <span class="stat-label">Total Assigned</span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">P</div>
                    <div class="stat-content">
                        <span class="stat-value">${inProgressCount}</span>
                        <span class="stat-label">In Progress</span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">C</div>
                    <div class="stat-content">
                        <span class="stat-value">${completedCount}</span>
                        <span class="stat-label">Completed</span>
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-icon">S</div>
                    <div class="stat-content">
                        <span class="stat-value">${totalScoresSubmitted}</span>
                        <span class="stat-label">Scores Submitted</span>
                    </div>
                </div>
            </div>

            <!-- Filter Section -->
            <div class="filter-section">
                <form action="${pageContext.request.contextPath}/evaluator/evaluations" method="GET" class="filter-form">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <div class="filter-row">
                        <div class="filter-group">
                            <label for="statusFilter">Status</label>
                            <select id="statusFilter" name="status">
                                <option value="">All</option>
                                <option value="IN_PROGRESS" ${param.status == 'IN_PROGRESS' ? 'selected' : ''}>In Progress</option>
                                <option value="COMPLETED" ${param.status == 'COMPLETED' ? 'selected' : ''}>Completed</option>
                            </select>
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
                            <a href="${pageContext.request.contextPath}/evaluator/evaluations" class="btn btn-secondary">Clear</a>
                        </div>
                    </div>
                </form>
            </div>

            <!-- Evaluations List -->
            <div class="evaluations-container">
                <c:choose>
                    <c:when test="${not empty evaluations}">
                        <c:forEach var="tender" items="${evaluations}">
                            <div class="evaluation-card ${tender.evaluationComplete ? 'completed' : 'in-progress'}">
                                <div class="card-header">
                                    <div class="tender-info">
                                        <span class="ref-number">${tender.referenceNumber}</span>
                                        <h3>${tender.title}</h3>
                                        <div class="tender-meta">
                                            <span class="category-badge">${tender.category.displayName}</span>
                                            <span class="status-badge status-${tender.status.name().toLowerCase()}">
                                                ${tender.status.displayName}
                                            </span>
                                        </div>
                                    </div>
                                    <div class="progress-badge">
                                        <c:choose>
                                            <c:when test="${tender.scoredBids >= tender.bidCount}">
                                                <span class="badge completed">Completed</span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge in-progress">In Progress</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                                
                                <div class="card-body">
                                    <!-- Progress Section -->
                                    <div class="progress-section">
                                        <div class="progress-header">
                                            <span class="progress-label">Your Progress</span>
                                            <span class="progress-count">${tender.scoredBids} / ${tender.bidCount} bids scored</span>
                                        </div>
                                        <div class="progress-bar-container">
                                            <div class="progress-bar" style="width: ${tender.evaluatorProgressPercent}%"></div>
                                        </div>
                                    </div>
                                    
                                    <!-- Scores Summary -->
                                    <c:if test="${tender.scoredBids > 0}">
                                        <div class="scores-summary">
                                            <h4>Your Scores Summary</h4>
                                            <table class="scores-table">
                                                <thead>
                                                    <tr>
                                                        <th>Supplier</th>
                                                        <th>Bid Amount</th>
                                                        <th>Technical Score</th>
                                                        <th>Price Score</th>
                                                        <th>Timeline Score</th>
                                                        <th>Weighted Total</th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <c:forEach var="score" items="${tender.evaluatorScores}">
                                                        <tr>
                                                            <td>${score.supplierName}</td>
                                                            <td>${score.formattedBidAmount}</td>
                                                            <td>${score.formattedTechnicalScore}</td>
                                                            <td>${score.formattedPriceScore}</td>
                                                            <td>${score.formattedTimelineScore}</td>
                                                            <td>
                                                                <strong>${score.formattedWeightedTotal}</strong>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </tbody>
                                            </table>
                                        </div>
                                    </c:if>
                                </div>
                                
                                <div class="card-footer">
                                    <a href="${pageContext.request.contextPath}/evaluator/tender-detail?id=${tender.tenderId}" 
                                       class="btn btn-outline">
                                        View Tender Details
                                    </a>
                                    
                                    <c:choose>
                                        <c:when test="${tender.scoredBids >= tender.bidCount}">
                                            <a href="${pageContext.request.contextPath}/evaluator/view-scores?id=${tender.tenderId}" 
                                               class="btn btn-outline">
                                                View All Scores
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/evaluator/score-bids?id=${tender.tenderId}" 
                                               class="btn btn-primary">
                                                Continue Evaluation
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-icon">!</div>
                            <h3>No Evaluations Found</h3>
                            <p>You have no evaluations assigned at this time.</p>
                            <a href="${pageContext.request.contextPath}/evaluator/tenders" class="btn btn-primary">
                                View Assigned Tenders
                            </a>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Pagination -->
            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/evaluator/evaluations?page=${currentPage - 1}&status=${param.status}&category=${param.category}" 
                           class="page-link">Previous</a>
                    </c:if>
                    
                    <c:forEach begin="1" end="${totalPages}" var="page">
                        <c:choose>
                            <c:when test="${page == currentPage}">
                                <span class="page-link active">${page}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/evaluator/evaluations?page=${page}&status=${param.status}&category=${param.category}" 
                                   class="page-link">${page}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>
                    
                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/evaluator/evaluations?page=${currentPage + 1}&status=${param.status}&category=${param.category}" 
                           class="page-link">Next</a>
                    </c:if>
                </div>
            </c:if>

        </main>
    </div>

    <jsp:include page="/shared/footer.jsp" />

    <script>
        // Auto-submit form when status or category changes
        document.getElementById('statusFilter').addEventListener('change', function() {
            this.form.submit();
        });
        
        document.getElementById('categoryFilter').addEventListener('change', function() {
            this.form.submit();
        });
        
        // Close alert messages after 5 seconds
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(function(alert) {
                alert.style.transition = 'opacity 0.5s';
                alert.style.opacity = '0';
                setTimeout(function() {
                    alert.remove();
                }, 500);
            });
        }, 5000);
    </script>

</body>
</html>