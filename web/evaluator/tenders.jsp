<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Assigned Tenders - ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/evaluatordashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/evaluator-tenders.css">
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
                    <h1>Assigned Tenders</h1>
                    <p>View and evaluate tenders assigned to you</p>
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

            <!-- Tabs Navigation -->
            <div class="evaluator-tabs">
                <a href="#pending" class="tab-link active" data-tab="pending">
                    Pending Evaluation
                    <c:if test="${not empty pendingTenders}">
                        <span class="tab-count">${pendingTenders.size()}</span>
                    </c:if>
                </a>
                <a href="#in-progress" class="tab-link" data-tab="in-progress">
                    In Progress
                    <c:if test="${not empty inProgressTenders}">
                        <span class="tab-count">${inProgressTenders.size()}</span>
                    </c:if>
                </a>
                <a href="#completed" class="tab-link" data-tab="completed">
                    Completed
                    <c:if test="${not empty completedTenders}">
                        <span class="tab-count">${completedTenders.size()}</span>
                    </c:if>
                </a>
            </div>

            <!-- ======================================================== -->
            <!-- PENDING EVALUATIONS TAB                                  -->
            <!-- ======================================================== -->
            <div id="pending" class="tab-content active">
                <c:choose>
                    <c:when test="${not empty pendingTenders}">
                        <div class="tender-cards">
                            <c:forEach var="tender" items="${pendingTenders}">
                                <div class="tender-card">
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
                                        <div class="bid-summary">
                                            <div class="bid-stat">
                                                <span class="stat-value">${tender.bidCount}</span>
                                                <span class="stat-label">Bids to Evaluate</span>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="card-body">
                                        <div class="tender-description">
                                            <p>${tender.description}</p>
                                        </div>
                                        <div class="tender-dates">
                                            <div class="date-item">
                                                <span class="date-label">Closing Date:</span>
                                                <span class="date-value">${tender.formattedClosingDateTime}</span>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="card-footer">
                                        <a href="${pageContext.request.contextPath}/evaluator/tender-detail?id=${tender.tenderId}" 
                                           class="btn btn-outline">
                                            View Details
                                        </a>
                                        <a href="${pageContext.request.contextPath}/evaluator/score-bids?id=${tender.tenderId}" 
                                           class="btn btn-primary">
                                            Start Evaluation
                                        </a>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-icon">!</div>
                            <h3>No Pending Evaluations</h3>
                            <p>You have no tenders waiting for evaluation.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- ======================================================== -->
            <!-- IN PROGRESS EVALUATIONS TAB                              -->
            <!-- ======================================================== -->
            <div id="in-progress" class="tab-content">
                <c:choose>
                    <c:when test="${not empty inProgressTenders}">
                        <div class="tender-cards">
                            <c:forEach var="tender" items="${inProgressTenders}">
                                <div class="tender-card">
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
                                        <div class="progress-indicator">
                                            <div class="progress-circle">
                                                <span class="progress-text">${tender.scoredBids}/${tender.bidCount}</span>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="card-body">
                                        <div class="progress-section">
                                            <div class="progress-label">
                                                <span>Your Progress</span>
                                                <span>${tender.scoredBids} of ${tender.bidCount} bids scored</span>
                                            </div>
                                            <div class="progress-bar-container">
                                                <div class="progress-bar" style="width: ${tender.evaluatorProgressPercent}%"></div>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div class="card-footer">
                                        <a href="${pageContext.request.contextPath}/evaluator/tender-detail?id=${tender.tenderId}" 
                                           class="btn btn-outline">
                                            View Details
                                        </a>
                                        <a href="${pageContext.request.contextPath}/evaluator/score-bids?id=${tender.tenderId}" 
                                           class="btn btn-primary">
                                            Continue Evaluation
                                        </a>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-icon">!</div>
                            <h3>No Evaluations In Progress</h3>
                            <p>You have no evaluations currently in progress.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- ======================================================== -->
            <!-- COMPLETED EVALUATIONS TAB                                -->
            <!-- ======================================================== -->
            <div id="completed" class="tab-content">
                <c:choose>
                    <c:when test="${not empty completedTenders}">
                        <div class="tender-cards">
                            <c:forEach var="tender" items="${completedTenders}">
                                <div class="tender-card completed">
                                    <div class="card-header">
                                        <div class="tender-info">
                                            <span class="ref-number">${tender.referenceNumber}</span>
                                            <h3>${tender.title}</h3>
                                            <div class="tender-meta">
                                                <span class="category-badge">${tender.category.displayName}</span>
                                                <span class="completion-badge">Completed</span>
                                            </div>
                                        </div>
                                        <div class="completion-icon">OK</div>
                                    </div>
                                    
                                    <div class="card-body">
                                        <div class="completion-details">
                                            <div class="detail-row">
                                                <span class="detail-label">Bids Evaluated:</span>
                                                <span class="detail-value">${tender.bidCount} bids</span>
                                            </div>
                                            <div class="detail-row">
                                                <span class="detail-label">Completed Date:</span>
                                                <span class="detail-value">${tender.formattedEvaluationCompletedDate}</span>
                                            </div>
                                            <c:if test="${tender.status == 'AWARDED'}">
                                                <div class="detail-row">
                                                    <span class="detail-label">Awarded To:</span>
                                                    <span class="detail-value highlight">${tender.winningSupplierName}</span>
                                                </div>
                                                <div class="detail-row">
                                                    <span class="detail-label">Award Amount:</span>
                                                    <span class="detail-value amount">${tender.formattedAwardedValue}</span>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                    
                                    <div class="card-footer">
                                        <a href="${pageContext.request.contextPath}/evaluator/view-scores?id=${tender.tenderId}" 
                                           class="btn btn-outline">
                                            View My Scores
                                        </a>
                                        <c:if test="${tender.status == 'AWARDED'}">
                                            <a href="${pageContext.request.contextPath}/evaluator/award-notice?id=${tender.tenderId}" 
                                               class="btn btn-outline">
                                                View Award Notice
                                            </a>
                                        </c:if>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="empty-state">
                            <div class="empty-icon">!</div>
                            <h3>No Completed Evaluations</h3>
                            <p>You have not completed any evaluations yet.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </main>
    </div>

    <jsp:include page="/shared/footer.jsp" />

    <script>
        // Tab switching functionality
        document.querySelectorAll('.tab-link').forEach(tab => {
            tab.addEventListener('click', function(e) {
                e.preventDefault();
                
                document.querySelectorAll('.tab-link').forEach(t => t.classList.remove('active'));
                document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
                
                this.classList.add('active');
                const tabId = this.getAttribute('data-tab');
                document.getElementById(tabId).classList.add('active');
            });
        });
        
        // Check URL hash for tab
        if (window.location.hash) {
            const hash = window.location.hash.substring(1);
            const tab = document.querySelector(`.tab-link[data-tab="${hash}"]`);
            if (tab) {
                tab.click();
            }
        }
        
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