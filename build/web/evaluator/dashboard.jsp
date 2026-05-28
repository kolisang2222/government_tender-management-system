<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Evaluator Dashboard - ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/ministry-style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/evaluatordashboard.css">
    <link rel="stylesheet" href="procgov-dashboard.css">
</head>
<body>

    <jsp:include page="/shared/navigation.jsp" />

    <div class="dashboard-layout">
        
        <jsp:include page="/shared/sidebar.jsp" />

        <main class="dashboard-main">
            
            <!-- Page Header -->
            <div class="page-header">
                <h1>Evaluator Dashboard</h1>
                <p>Evaluate bids for tenders that are under evaluation</p>
            </div>

            <!-- Message Display -->
            <c:if test="${not empty param.success}">
                <div class="alert alert-success">
                    <span>OK</span>
                    <span>${param.success}</span>
                    <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                </div>
            </c:if>
            <c:if test="${not empty param.error}">
                <div class="alert alert-error">
                    <span>!</span>
                    <span>${param.error}</span>
                    <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                </div>
            </c:if>
            
            <c:if test="${not empty sessionScope.successMessage}">
                <div class="alert alert-success">
                    <span>OK</span>
                    <span>${sessionScope.successMessage}</span>
                    <button class="close-alert" onclick="this.parentElement.remove()">&times;</button>
                </div>
                <c:remove var="successMessage" scope="session" />
            </c:if>

            <!-- Stats Cards -->
            <div class="stats-container">
                <div class="stat-card">
                    <div class="stat-value">${totalAssignedTenders}</div>
                    <div class="stat-label">Total Assigned</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${pendingEvaluationsCount}</div>
                    <div class="stat-label">Pending</div>
                </div>
                <div class="stat-card">
                    <div class="stat-value">${completedEvaluationsCount}</div>
                    <div class="stat-label">Completed</div>
                </div>
            </div>

            <!-- Pending Evaluations -->
            <div class="dashboard-card full-width">
                <div class="card-header">
                    <h3>Tenders Awaiting Your Evaluation</h3>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty pendingEvaluations}">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Tender Reference</th>
                                        <th>Title</th>
                                        <th>Category</th>
                                        <th>Total Bids</th>
                                        <th>Your Progress</th>
                                        <th>Action</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${pendingEvaluations}" var="tender">
                                        <tr>
                                            <td>${tender.referenceNumber}</td>
                                            <td>${tender.title}</td>
                                            <td>${tender.category.displayName}</td>
                                            <td>${tender.bidCount}</td>
                                            <td>
                                                <div class="progress-bar">
                                                    <div class="progress-fill" style="width: ${tender.evaluatorProgressPercent}%"></div>
                                                </div>
                                                <small>${tender.scoredBids} / ${tender.bidCount} bids scored</small>
                                            </td>
                                            <td>
                                                <a href="${pageContext.request.contextPath}/evaluator/score-bids?id=${tender.tenderId}" 
                                                   class="btn-primary">
                                                    ${tender.scoredBids > 0 ? 'Continue' : 'Start'}
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <p class="empty-state">No tenders awaiting evaluation.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <!-- Recent Awarded Tenders -->
            <div class="dashboard-card full-width">
                <div class="card-header">
                    <h3>Recent Award Results</h3>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty recentAwardedTenders}">
                            <table class="data-table">
                                <thead>
                                    <tr>
                                        <th>Tender Reference</th>
                                        <th>Title</th>
                                        <th>Winning Supplier</th>
                                        <th>Award Date</th>
                                        <th>Award Value</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${recentAwardedTenders}" var="tender">
                                        <tr>
                                            <td>${tender.referenceNumber}</td>
                                            <td>${tender.title}</td>
                                            <td>${tender.winningSupplierName}</td>
                                            <td>${tender.formattedAwardDate}</td>
                                            <td>${tender.formattedAwardedValue}</td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                            </table>
                        </c:when>
                        <c:otherwise>
                            <p class="empty-state">No recent award results.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

        </main>
    </div>

    <jsp:include page="/shared/footer.jsp" />

    <script>
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